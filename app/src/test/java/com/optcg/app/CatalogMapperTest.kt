package com.optcg.app

import com.optcg.app.catalog.CatalogJson
import com.optcg.app.catalog.CatalogMapper
import com.optcg.app.catalog.SUPPORTED_SCHEMA_VERSION
import com.optcg.app.data.VIRTUAL_ANNOUNCED
import com.optcg.app.data.VIRTUAL_DONS
import com.optcg.app.images.ImageUrls
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogMapperTest {
    private val catalog = BundledCatalog.catalog
    private val sets = CatalogMapper.sets(catalog)
    private val printings = CatalogMapper.printings(catalog).associateBy { it.id }

    @Test
    fun bundledCatalogIsReadable() {
        assertTrue(catalog.schemaVersion <= SUPPORTED_SCHEMA_VERSION)
        assertEquals(catalog.printings.size, printings.size)
        assertTrue("expected thousands of printings", printings.size > 4000)
    }

    @Test
    fun japanesePrintingsCarryEnglishText() {
        val zoro = printings.getValue("OP01-001_p2")
        assertEquals("OP01-001", zoro.code)
        assertEquals(2, zoro.variant)
        assertEquals("Roronoa Zoro", zoro.nameEn)
        assertEquals("ロロノア・ゾロ", zoro.nameJp)
        assertEquals("550101", zoro.setId)
        assertEquals("red", zoro.colors)
    }

    @Test
    fun donsAndAnnouncedCardsGetVirtualSets() {
        val setIds = sets.map { it.id }
        assertTrue(VIRTUAL_DONS in setIds)
        assertTrue(VIRTUAL_ANNOUNCED in setIds)
        assertEquals(VIRTUAL_DONS, printings.getValue("DON-0001").setId)
        val announced = printings.values.filter { it.status == "announced" && it.kind == "card" }
        assertTrue(announced.isNotEmpty())
        assertTrue(announced.all { it.setId == VIRTUAL_ANNOUNCED })
        // Every printing points at a set that exists.
        assertTrue(printings.values.all { it.setId in setIds })
    }

    @Test
    fun combinedEnglishSetHasNoBorrowedName() {
        val eb04 = sets.first { it.code == "EB-04" }
        assertNull(eb04.nameEn)
    }

    @Test
    fun imageFallsBackToOfficialImageButNotThePdf() {
        val card = printings.getValue("OP01-001")
        assertTrue(ImageUrls.thumb("", card)!!.startsWith("https://www.onepiece-cardgame.com/images/cardlist/card/OP01-001.png"))
        // Dons that only exist in the PDF have no image until hosted images exist.
        assertNull(ImageUrls.thumb("", printings.getValue("DON-0001")))
        // Announced dons have their announcement image.
        assertNotNull(ImageUrls.detail("", printings.getValue("DON-0288")))
    }

    @Test
    fun catalogCarriesTheImageBase() {
        val json = """{"version":"v","generated_at":"g","schema_version":1,"image_base":"https://img.example/img",
            "sets":[],"printings":[],"pull_profiles":{}}"""
        assertEquals("https://img.example/img", CatalogJson.decode(json.byteInputStream()).imageBase)
        // Catalogs from before the field existed still load.
        val old = json.replace(""""image_base":"https://img.example/img",""", "")
        assertNull(CatalogJson.decode(old.byteInputStream()).imageBase)
    }

    @Test
    fun hostedImagesWinWhenConfigured() {
        val card = printings.getValue("OP01-001").copy(imageThumb = "abc.webp", imageDetail = "def.webp")
        assertEquals("https://img.example/abc.webp", ImageUrls.thumb("https://img.example/", card))
        assertEquals("https://img.example/def.webp", ImageUrls.detail("https://img.example", card))
    }
}
