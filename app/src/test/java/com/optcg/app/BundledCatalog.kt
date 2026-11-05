package com.optcg.app

import com.optcg.app.catalog.Catalog
import com.optcg.app.catalog.CatalogJson
import java.io.File

/** The catalog snapshot shipped in the app; unit tests run from the app module directory. */
object BundledCatalog {
    val catalog: Catalog by lazy {
        File("src/main/assets/catalog.json.gzip").inputStream().use(CatalogJson::decodeGzip)
    }
}
