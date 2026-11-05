"""Polite HTTP fetching with an on-disk cache and conditional requests."""

from __future__ import annotations

import hashlib
import json
import time
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path
from typing import Dict, Optional

USER_AGENT = "optcg-catalog-builder/0.1 (+https://github.com/andrewooi924/ops)"


class FetchError(RuntimeError):
    pass


def to_uri(url: str) -> str:
    """Percent-encode non-ASCII characters (some asset paths are Japanese)."""
    parts = urllib.parse.urlsplit(url)
    path = urllib.parse.quote(parts.path, safe="/%:@!$&'()*+,;=-._~")
    query = urllib.parse.quote(parts.query, safe="=&%:@!$'()*+,;/?-._~")
    return urllib.parse.urlunsplit((parts.scheme, parts.netloc.encode("idna").decode("ascii"), path, query, ""))


class Fetcher:
    """Fetches URLs with a per-host delay, retries and a local cache.

    ``max_age`` (seconds) lets a fresh cached copy be reused without any
    request. Older copies are revalidated with ETag / Last-Modified, so
    unchanged pages and images cost a 304 instead of a full download.
    ``offline`` serves only cached copies and never touches the network.
    """

    def __init__(
        self,
        cache_dir: Optional[Path] = None,
        delay: float = 1.0,
        retries: int = 3,
        timeout: float = 60.0,
        offline: bool = False,
    ) -> None:
        self.cache_dir = Path(cache_dir) if cache_dir else None
        self.delay = delay
        self.retries = retries
        self.timeout = timeout
        self.offline = offline
        self._last_request: Dict[str, float] = {}
        self.stats = {"network": 0, "not_modified": 0, "cache": 0}
        if self.cache_dir:
            self.cache_dir.mkdir(parents=True, exist_ok=True)

    def _paths(self, url: str):
        key = hashlib.sha256(url.encode("utf-8")).hexdigest()
        assert self.cache_dir is not None
        return self.cache_dir / f"{key}.body", self.cache_dir / f"{key}.json"

    def _read_cache(self, url: str):
        if not self.cache_dir:
            return None, None
        body_path, meta_path = self._paths(url)
        if body_path.exists() and meta_path.exists():
            return body_path.read_bytes(), json.loads(meta_path.read_text("utf-8"))
        return None, None

    def _write_cache(self, url: str, body: bytes, headers) -> None:
        if not self.cache_dir:
            return
        body_path, meta_path = self._paths(url)
        body_path.write_bytes(body)
        meta = {
            "url": url,
            "fetched_at": time.time(),
            "etag": headers.get("ETag"),
            "last_modified": headers.get("Last-Modified"),
        }
        meta_path.write_text(json.dumps(meta), "utf-8")

    def _touch_cache(self, url: str, meta: dict) -> None:
        if not self.cache_dir:
            return
        _, meta_path = self._paths(url)
        meta["fetched_at"] = time.time()
        meta_path.write_text(json.dumps(meta), "utf-8")

    def _wait_for_host(self, url: str) -> None:
        host = urllib.parse.urlsplit(url).netloc
        last = self._last_request.get(host)
        if last is not None:
            remaining = self.delay - (time.monotonic() - last)
            if remaining > 0:
                time.sleep(remaining)
        self._last_request[host] = time.monotonic()

    def get(self, url: str, max_age: Optional[float] = None) -> bytes:
        cached, meta = self._read_cache(url)
        if cached is not None and (
            self.offline
            or (max_age is not None and time.time() - meta["fetched_at"] < max_age)
        ):
            self.stats["cache"] += 1
            return cached
        if self.offline:
            raise FetchError(f"offline and not cached: {url}")

        headers = {"User-Agent": USER_AGENT}
        if cached is not None:
            if meta.get("etag"):
                headers["If-None-Match"] = meta["etag"]
            if meta.get("last_modified"):
                headers["If-Modified-Since"] = meta["last_modified"]

        last_error: Optional[Exception] = None
        for attempt in range(self.retries):
            self._wait_for_host(url)
            request = urllib.request.Request(to_uri(url), headers=headers)
            try:
                with urllib.request.urlopen(request, timeout=self.timeout) as response:
                    body = response.read()
                    self.stats["network"] += 1
                    self._write_cache(url, body, response.headers)
                    return body
            except urllib.error.HTTPError as error:
                if error.code == 304 and cached is not None:
                    self.stats["not_modified"] += 1
                    self._touch_cache(url, meta)
                    return cached
                if error.code in (403, 404, 410):
                    raise FetchError(f"HTTP {error.code} for {url}") from error
                last_error = error
            except (urllib.error.URLError, TimeoutError, ConnectionError) as error:
                last_error = error
            time.sleep(2 ** attempt)
        raise FetchError(f"failed after {self.retries} attempts: {url}: {last_error}")

    def get_text(self, url: str, max_age: Optional[float] = None) -> str:
        return self.get(url, max_age=max_age).decode("utf-8", errors="replace")
