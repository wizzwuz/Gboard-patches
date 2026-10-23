package dev.jason.gboardpatches.patches.gboard.registry

import com.google.gson.JsonParser
import dev.jason.gboardpatches.patches.gboard.shared.generated.GboardTargetAdmission
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GboardFrostedGlassSinglePatchTest {
    @Test
    fun catalogExposesExactlyOneFrostedGlassPatch() {
        val registrations = GboardPublishedPatchCatalog.morpheRegistrations
        assertEquals(1, registrations.size)
        val singlePatch = registrations.single()
        assertEquals("Frosted Glass", singlePatch.name)
        assertTrue(singlePatch.use)
    }

    @Test
    fun admissionAdmitsOnlyFrostedGlassFeature() {
        assertEquals(
            setOf("frosted_glass"),
            GboardTargetAdmission.admittedContributionIdsByFeature.keys,
        )
    }

    @Test
    fun publishedInventoryContainsOnlyFrostedGlass() {
        val inventory = JsonParser.parseString(
            GboardPublishedPatchCatalog.publishedInventory("test-version"),
        ).asJsonObject
        val patches = inventory.getAsJsonArray("patches")
        assertEquals(1, patches.size())
        assertEquals("Frosted Glass", patches[0].asJsonObject.get("name").asString)
    }
}
