# Rendering Architecture Recommendation

## Decision

**Google Filament** for the 3D world, embedded as a `SurfaceView` inside the
Play/Explore composables via `AndroidView`; **Jetpack Compose** for all 2D UI
(menus, HUD, gallery). This is recorded as ADR-001.

## Why Filament

| Need | How Filament answers it |
|---|---|
| Cozy soft lighting, 4 times of day | Built-in physically-based directional light + image-based lighting (IBL). Each `TimeOfDay` maps to one small pre-filtered KTX environment + sun parameters; transitions are parameter lerps. |
| Mid-range 60 fps | Lean Vulkan/OpenGL backend designed for mobile; tiny (~2–3 MB) runtime. |
| Modular building kits | First-class glTF loading (`gltfio-android`); buildings = instanced module meshes → tens of draw calls for hundreds of buildings. |
| Kotlin / Android Studio constraint | Official Android Kotlin/Java API, plain Gradle dependency, no engine editor or licensing. |
| Thumbnails for dioramas | Off-screen render targets → WebP snapshot capture for the gallery. |

### Alternatives considered

- **Custom OpenGL ES** — maximum control, but we would re-implement lighting,
  shadows, materials and asset loading. Kept only as an escape hatch.
- **libGDX** — Kotlin-friendly, but its 3D lighting/PBR stack would need heavy
  custom shader work for the target look.
- **Unity / Godot** — violate the "entirely Kotlin in Android Studio"
  requirement and bring heavyweight runtimes.
- **Compose Canvas (2D only)** — cannot deliver 3D dioramas or time-of-day
  lighting; it is used for UI and, in this foundation phase, the temporary
  grid placeholder.

## Integration contract (already in place)

The rest of the app never touches Filament types. Everything goes through
`core/engine/SceneCommandSink`:

```
ViewModel ──► SceneCommandSink (interface)
                   ▲
                   │ implements
   PlaceholderEngineController   (foundation — no-op)
   FilamentEngineController      (Phase 1 — render-thread owner)
```

Threading rule for Phase 1: one render thread owns ALL Filament objects
(engine, scene, swap chain). Domain output arrives as immutable `TownDiff`
batches through a single-producer queue drained at frame start. ViewModels
and use cases stay JVM-testable because they only ever see the interface.

## Performance posture baked into the foundation

- Town content is *data about inputs* (command log), so the renderer is a
  pure projector — it can rebuild, instance, chunk and LOD freely.
- `Footprint` packs cells into ints; `TownState` uses persistent maps with
  structural sharing; iteration order is fixed (`structuresOrdered()`).
- Grid placeholder caps drawn lines; the real grid is a single shader quad.
