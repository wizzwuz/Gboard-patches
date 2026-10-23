package dev.jason.gboardpatches.patches.gboard.registry

import app.morphe.patcher.patch.Patch
import app.morphe.patcher.patch.ResourcePatchBuilder
import dev.jason.gboardpatches.patches.gboard.features.addsymbols.gboardZhuyinCustomSymbolsCorpusPatch
import dev.jason.gboardpatches.patches.gboard.features.addsymbols.gboardZhuyinCustomSymbolsEmoticonStatePatch
import dev.jason.gboardpatches.patches.gboard.features.addsymbols.gboardZhuyinCustomSymbolsEntryPatch
import dev.jason.gboardpatches.patches.gboard.features.addsymbols.gboardZhuyinCustomSymbolsHistoryPatch
import dev.jason.gboardpatches.patches.gboard.features.addsymbols.gboardZhuyinCustomSymbolsRecyclerPatch
import dev.jason.gboardpatches.patches.gboard.features.addsymbols.gboardZhuyinCustomSymbolsRoutingPatch
import dev.jason.gboardpatches.patches.gboard.features.advancedvoice.gboardAdvancedVoice1803ZhTwPatch
import dev.jason.gboardpatches.patches.gboard.features.advancedvoice.gboardAdvancedVoiceFlagValuePatch
import dev.jason.gboardpatches.patches.gboard.features.englishqwerty.gboardEnglishQwertyPointerPatch
import dev.jason.gboardpatches.patches.gboard.features.englishqwerty.gboardEnglishQwertySoftKeyPatch
import dev.jason.gboardpatches.patches.gboard.features.flowmode.gboardFlowModeFlagValuePatch
import dev.jason.gboardpatches.patches.gboard.features.flowmode.gboardFlowModeKeyEventPatch
import dev.jason.gboardpatches.patches.gboard.features.longpressquickactions.gboardLongPressQuickActionsInputEventPatch
import dev.jason.gboardpatches.patches.gboard.features.longpressquickactions.gboardLongPressQuickActionsGesturePatch
import dev.jason.gboardpatches.patches.gboard.features.longpressquickactions.gboardLongPressQuickActionsPointerOwnerPatch
import dev.jason.gboardpatches.patches.gboard.features.longpressquickactions.gboardLongPressQuickActionsSoftKeyPatch
import dev.jason.gboardpatches.patches.gboard.features.ocr.gboardOcrFlagValuePatch
import dev.jason.gboardpatches.patches.gboard.features.ocr.gboardOcrRuntimePatch
import dev.jason.gboardpatches.patches.gboard.features.rambler.gboardRambler1803OfficialSelectorPatch
import dev.jason.gboardpatches.patches.gboard.features.roundedkeyboard.gboardRoundedKeyboardBytecodePatch
import dev.jason.gboardpatches.patches.gboard.features.spacebarlogo.gboardSpacebarLogoSoftKeyPatch
import dev.jason.gboardpatches.patches.gboard.features.toprowswipe.gboardTopRowSwipeGesturePatch
import dev.jason.gboardpatches.patches.gboard.features.toprowswipe.gboardTopRowSwipePointerPatch
import dev.jason.gboardpatches.patches.gboard.features.toprowswipe.gboardTopRowSwipeSoftKeyPatch
import dev.jason.gboardpatches.patches.gboard.features.zhuyinslide.gboardZhuyinSlidePointerAnchorPatch
import dev.jason.gboardpatches.patches.gboard.features.zhuyinslide.gboardZhuyinSlideSoftKeyPatch
import dev.jason.gboardpatches.patches.gboard.features.zhuyintraditionalsimplifiedtoggle.gboardZhuyinTraditionalSimplifiedToggleGesturePatch
import dev.jason.gboardpatches.patches.gboard.features.zhuyintraditionalsimplifiedtoggle.gboardZhuyinTraditionalSimplifiedTogglePopupPatch
import dev.jason.gboardpatches.patches.gboard.features.zhuyintraditionalsimplifiedtoggle.gboardZhuyinTraditionalSimplifiedToggleSoftKeyPatch

internal class GboardContributionWiringUnit(
    val contributionIds: Set<String>,
    private val dependencySupplier: () -> Array<Patch<*>>,
) {
    fun dependencies(): Array<Patch<*>> = dependencySupplier()
}

internal object GboardContributionWiring {
    private val unitsByFeature = emptyMap<String, List<GboardContributionWiringUnit>>()

    val registeredContributionIdsByFeature: Map<String, Set<String>> =
        unitsByFeature.mapValues { (_, units) ->
            units.flatMapTo(linkedSetOf()) { unit -> unit.contributionIds }
        }

    fun activeContributionIds(featureId: String, admitted: Set<String>): Set<String> =
        activeUnits(featureId, admitted).flatMapTo(linkedSetOf()) { unit -> unit.contributionIds }

    fun wireAdmitted(
        featureId: String,
        admitted: Set<String>,
        builder: ResourcePatchBuilder,
    ) {
        activeUnits(featureId, admitted).forEach { unit ->
            builder.dependsOn(*unit.dependencies())
        }
    }

    private fun activeUnits(
        featureId: String,
        admitted: Set<String>,
    ): List<GboardContributionWiringUnit> {
        val units = unitsByFeature[featureId] ?: return emptyList()
        val registeredIds = units.flatMapTo(linkedSetOf()) { unit -> unit.contributionIds }
        check(admitted.all { contributionId -> contributionId in registeredIds }) {
            "$featureId admits unregistered contribution IDs ${(admitted - registeredIds).sorted()}"
        }
        units.forEach { unit ->
            val admittedUnitIds = unit.contributionIds.intersect(admitted)
            check(admittedUnitIds.isEmpty() || admittedUnitIds == unit.contributionIds) {
                "$featureId partially admits indivisible execution unit " +
                    "${unit.contributionIds.sorted()}: ${admittedUnitIds.sorted()}"
            }
        }
        return units.filter { unit -> unit.contributionIds.all(admitted::contains) }
    }

    private fun unit(
        vararg contributionIds: String,
        dependencySupplier: () -> Array<Patch<*>>,
    ) = GboardContributionWiringUnit(contributionIds.toSet(), dependencySupplier)
}
