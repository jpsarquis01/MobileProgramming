# Glade Towns — Final Report

## 1. Project overview
Glade Towns is a cozy Android town-builder. You **draw a shape** on a grid with
your finger; the game **recognises** it (rectangle/circle/triangle/irregular) and
**procedurally generates** a building from it; buildings **grow into a town** with
neighbour-aware heights, palettes and auto-routed roads; the town can be **sealed**
into a permanent, collectible **snow-globe diorama** and later **explored** in a
read-only camera mode. A **time-of-day** system (Morning/Afternoon/Sunset/Night)
re-lights the whole scene with animated transitions and atmosphere (warm windows,
fireflies, vignette).

Built in Kotlin + Jetpack Compose, MVVM over a Clean-Architecture domain core,
Room for metadata and an event-sourced blob store for the towns themselves.

## 2. Architecture summary
**Layers.** `feature/*` (Compose screens + ViewModels) → `core/domain`
(pure models, use cases, procgen) → `core/data` (Room, blob store). The domain
layer has no Android dependencies and is fully unit-testable on the JVM.

**Event-sourced towns.** A town *is* its command log; `TownState = fold(commands)`.
Saves store **inputs + derived seeds**, never generated geometry, so a town is
tiny on disk and replays bit-identically on any device (SplitMix64 seed tree).
Buildings and roads are always *derived* at load time by `TownGrowthEngine`,
never persisted — which is exactly why adding new visuals this phase required
**zero** save-format changes.

**Rendering.** All drawing now flows through one shared module,
`core/ui/render/`: `GridMapping` (screen↔grid), `drawTown` (ground, grid, roads,
massing, roofs, windows) and `drawSelection`. Play and Explore render through the
identical path. The Filament/`SceneCommandSink` seam from the design phase is
left intact and still fed (grid + time), ready to swap in for the Compose canvas
without touching domain code.

**Lighting & atmosphere.** `TimeOfDayPalette` maps each `TimeOfDay` to ground/
road colours plus an *animated overlay* spec. Crucially, the animated scrim,
vignette and fireflies live in a **separate** `AtmosphereOverlay` composable, so
the infinite animation never invalidates the heavy town Canvas. Transitions use
`animateColorAsState`/`animateFloatAsState` (1200 ms tween).

**Diorama gallery.** Previews are a compact `TownPreview` downsample (28×28),
built lazily off the main thread, cached for the session and streamed in
progressively; cards show a seeded fallback until their preview lands. Sealing
flips DRAFT→SEALED; sealed towns open in Explore, drafts reopen in Play.

**Exploration.** `ExploreScreen` uses `Modifier.graphicsLayer { … }` (deferred-read
lambda form) for pan/zoom so only the layer — not `drawTown` — re-runs;
`detectTransformGestures` drives pinch/pan and a separate tap maps back through
the base `GridMapping` to inspect a building. Time-of-day here is **view-only**
(no command written) so a sealed diorama is never mutated by being looked at.

## 3. Review: issues found & fixed this phase
- **Duplicate rendering** between Play and the new Explore → extracted to
  `core/ui/render/` (single source of truth).
- **Animation invalidating the town Canvas** → isolated all animation into
  `AtmosphereOverlay`.
- **Stroke capture was O(n²)** (`stroke = stroke + point` rebuilt the list every
  pointer event) → switched to `mutableStateListOf` (amortised O(1) appends that
  invalidate only the Canvas).
- **Gesture detectors restarting** on every layout change → `pointerInput` keys
  stabilised (`gridCells` for draw, `Unit` for tap).
- **Scattered colour magic numbers** → centralised in `TimeOfDayPalette` + renderer.

## 4. Remaining limitations
- **Preview generation cost.** Each gallery preview loads + replays + grows its
  town (O(n) town loads). Cached per session but **not persisted** — a cold
  gallery with very many towns does real work on first view.
- **Road routing is O(n²)** overall (nearest-neighbour over building count).
  Fine for hand-drawn counts (≲200); pathological for very dense towns.
- **Reroll/demolish/undo trigger a full `growth.rebuild()`**, acceptable at
  typical sizes but not incremental.
- **`snapshot()` copies the `ownerByCell` map** on every emit (minor allocation).
- **Rendering is still top-down Compose.** The Filament seam exists but is not
  yet the renderer; there is no true 3D/parallax yet.
- **Explore time-of-day is non-persistent** by design.

## 5. Future expansion ideas
- **Persist previews at seal time** (store a WebP or the `TownPreview` blob) to
  make the gallery instant and O(1).
- **Incremental growth** (dirty-region rebuild) and a spatial index for road
  routing to scale to dense towns.
- **Activate the Filament renderer** behind `SceneCommandSink` for real 3D
  massing, soft shadows and day/night lighting — no domain changes needed.
- **Diorama sharing**: because a town is just a small command log + seed, towns
  are trivially shareable as a code/QR; recipients replay an identical town.
- **More archetypes & decor** (bridges, walls, gardens, water) via new grammar
  rules — the classifier→grammar→massing pipeline is built to extend.
- **Seasons/weather** layered on the existing atmosphere overlay (snow, rain).
- **Soundscape** tied to time-of-day and structure count.
