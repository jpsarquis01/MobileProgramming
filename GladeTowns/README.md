# Glade Towns — Foundation Phase

A cozy procedural town-builder for Android: draw shapes with your finger, watch
buildings grow, keep every town forever as a collectible diorama.

This repository contains the **foundation phase only**: project structure,
MVVM architecture, navigation, the three screens (Menu, Play, Dioramas),
the event-sourced save system, data models, and the Room database. Drawing,
procedural generation, buildings, and exploration are intentionally absent
and arrive in subsequent phases (see `docs/ARCHITECTURE.md §7`).

## Build & run

1. Open the project root in Android Studio (Ladybug or newer).
2. Let Gradle sync (AGP 8.6.x, Kotlin 2.0.x, JDK 17).
3. Run the `app` configuration on a device/emulator with API 26+.

Run unit tests: `./gradlew :app:testDebugUnitTest`

## What works right now

- Main Menu with **Play**, **Continue last draft**, and **Dioramas**.
- New-town setup: name + grid size (20×20 / 50×50 / 100×100) → persisted town.
- Diorama Gallery: every town appears as a seeded snow-globe card; tap to
  reopen; soft-deletable. Empty-shelf state for first launch.
- Play screen: loads a town by replaying its command log; shows the grid
  placeholder where the 3D engine surface will mount.
- Save system: Room metadata + atomic, CRC-guarded, CBOR command-log blobs
  with backup-file recovery. Deterministic seed derivation (SplitMix64).
- Unit tests covering the determinism-critical core (RNG, reducer, codec).

## Documentation

- `docs/ARCHITECTURE.md` — every system explained, plus the phase plan.
- `docs/RENDERING.md` — rendering architecture recommendation (Filament).

## Layout

```
app/src/main/java/com/studio/gladetowns/
  navigation/      NavHost + routes
  core/domain/     models, commands, reducer, RNG, repos (interfaces), use cases
  core/data/       Room (db/dao/entity), blob store, repository impls, DI
  core/engine/     renderer contract (SceneCommandSink) + placeholder impl
  core/ui/         theme + shared composables
  feature/menu|play|gallery
docs/              architecture + rendering docs
```
