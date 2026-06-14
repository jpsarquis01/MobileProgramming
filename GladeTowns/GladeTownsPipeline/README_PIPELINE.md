# Glade Towns — Gameplay Creation Pipeline (drop-in files)

Copy this folder's `app/` over your project's `app/` (it mirrors the exact
package layout). Files are either NEW or MODIFIED relative to the foundation.

## MODIFIED (overwrite existing)
- core/domain/model/structure/Structure.kt      (+CHAPEL archetype)
- core/domain/model/command/TownCommand.kt       (+optional RawStroke on PlaceStructure)
- core/domain/usecase/LoadTownUseCase.kt          (LoadedTown now carries `commands`)
- feature/play/PlayViewModel.kt                   (full creation session)
- feature/play/PlayScreen.kt                      (interactive draw/reroll/demolish canvas)

## NEW
- core/domain/model/shape/RawStroke.kt
- core/domain/model/shape/ShapeModels.kt
- core/domain/model/structure/StructurePlan.kt
- core/domain/model/town/TownLayout.kt
- core/domain/procgen/recognize/ShapeRecognizer.kt
- core/domain/procgen/ArchetypeMapping.kt
- core/domain/procgen/generate/BuildingGenerator.kt
- core/domain/procgen/merge/TownGrowthEngine.kt
- core/domain/usecase/DrawShapeUseCase.kt
- app/src/test/.../ShapeRecognizerTest.kt
- app/src/test/.../BuildingGeneratorTest.kt
- app/src/test/.../TownGrowthEngineTest.kt

## Notes
- No DI/Gradle changes needed: TownRepository is already bound by Hilt,
  DrawShapeUseCase is constructor-injected, material-icons-extended is present.
- Determinism: command log stores inputs + recognized footprint + seed only;
  buildings/roads/merging are re-derived by TownGrowthEngine on load, so
  revisited saved towns rebuild identically.
- Rendering stays top-down Compose (the Filament/SceneCommandSink seam is
  untouched and consumes the same TownLayout later).
