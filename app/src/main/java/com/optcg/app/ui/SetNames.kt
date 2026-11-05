package com.optcg.app.ui

/** "BOOSTER PACK -ROMANCE DAWN- [OP01]" -> "ROMANCE DAWN"; the set code is shown separately. */
fun shortEnglishSetName(name: String): String {
    val withoutCode = name.replace(Regex("""\s*\[[^\]]*]\s*$"""), "")
    val inner = Regex("""-(.+)-""").find(withoutCode)?.groupValues?.get(1) ?: withoutCode
    // Bandai's capitalisation is kept: title-casing breaks names like MONKEY.D.LUFFY and 3D2Y.
    return inner.trim()
}

/** "ブースターパック 世界最強の戦士【OP-17】" -> "世界最強の戦士". */
fun shortJapaneseSetName(name: String): String {
    val withoutCode = name.replace(Regex("""\s*【[^】]*】\s*$"""), "").trim()
    val parts = withoutCode.split(' ', '　', limit = 2)
    return if (parts.size == 2 && parts[1].isNotBlank()) parts[1].trim() else withoutCode
}
