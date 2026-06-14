# Glade Towns — Final Systems Drop

This folder contains **only the files that changed** in the final implementation
phase (diorama system, exploration mode, time-of-day, visual polish, and the
review/refactor pass). Drop them into the existing `GladeTowns` project at the
matching paths under `app/src/main/java/com/studio/gladetowns/` (and the test
under `app/src/test/...`).

No Gradle, DI module, or Room schema-version changes are required: every new
class is constructor-injected (Hilt already has the bindings), the new DAO query
is additive, and no entity columns changed.

## NEW files (9)
| Path | Purpose |
|---|---|
| `core/ui/render/TimeOfDayPalette.kt` | Per-time palette + atmosphere params |
| `core/ui/render/TownRenderer.kt` | Shared top-down draw (GridMapping, drawTown, drawSelection) |
| `core/ui/render/AtmosphereOverlay.kt` | Animated lighting/fireflies + shared TimeOfDayBar |
| `core/domain/model/town/TownPreview.kt` | Compact downsample for gallery previews |
| `core/domain/usecase/LoadTownLayoutUseCase.kt` | Read-only load → replay → grow → layout |
| `core/domain/usecase/SealTownUseCase.kt` | DRAFT → SEALED |
| `feature/explore/ExploreViewModel.kt` | Read-only viewer state/logic |
| `feature/explore/ExploreScreen.kt` | Pan/zoom/inspect UI |
| `app/src/test/.../core/domain/TownPreviewTest.kt` | Preview downsampler tests |

## MODIFIED files (10)
| Path | Change |
|---|---|
| `core/domain/repository/DioramaRepository.kt` | + `suspend fun seal(id)` |
| `core/data/db/dao/DioramaDao.kt` | + `seal(id, now)` query (additive) |
| `core/data/repo/DioramaRepositoryImpl.kt` | + `seal` implementation |
| `core/ui/components/SnowGlobeCard.kt` | Real preview inside dome + long-press |
| `feature/gallery/GalleryViewModel.kt` | Lazy cached preview building + delete |
| `feature/gallery/GalleryScreen.kt` | Previews, delete dialog, sealed→Explore routing |
| `feature/play/PlayViewModel.kt` | + Seal event/use case |
| `feature/play/PlayScreen.kt` | Shared renderer, time bar, atmosphere, Explore/Seal, stroke perf fix |
| `navigation/Routes.kt` | + `EXPLORE` route |
| `navigation/GladeNavHost.kt` | Wired Explore; gallery/play routing |

See `docs/FINAL_REPORT.md` for the architecture write-up, limitations and roadmap.
