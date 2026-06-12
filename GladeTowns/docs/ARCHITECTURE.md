# Glade Towns — Foundation Architecture

This document explains every system created in the foundation phase, why it
is shaped the way it is, and how the next phases plug into it. It implements
the project's Technical Design Document (TDD); section references point there.

---

## 1. Project structure & package organization

### 1.1 Layers

```
feature/*        Compose screens + ViewModels (presentation)
core/domain      pure Kotlin: models, commands, reducer, RNG,
                 repository INTERFACES, use cases
core/data        Room + blob store + repository IMPLEMENTATIONS + DI
core/engine      renderer contract + placeholder implementation
core/ui          theme + shared composables
navigation       NavHost + route contract
```

Dependency rule (enforced by convention now, by Gradle modules later):
`feature → domain ← data`, `feature → engine-interface`, and **nothing in
`core/domain` imports an Android or Filament class**. The whole generative
core will therefore run and be tested on a plain JVM.

### 1.2 Why a single Gradle module (ADR-002)

The TDD plans a multi-module split (`:core:domain`, `:core:data`, …). The
foundation deliberately ships as one `:app` module with the *same package
boundaries*: at this size, module plumbing adds build friction without
isolation benefits. The split is mechanical later (packages already match the
target modules one-to-one) and is scheduled for the start of Phase 2, when
the procgen code and the desktop `:tools:genlab` harness need a pure-JVM
domain module. `settings.gradle.kts` carries the placeholder includes.

## 2. MVVM architecture

- **One ViewModel per screen** exposing a single `StateFlow<UiState>`
  (immutable data class) and one `onEvent(Event)` entry point (sealed
  interface). Unidirectional data flow throughout.
- ViewModels call **use cases**, never repositories directly for writes with
  business meaning (e.g. `CreateTownUseCase` is where the master seed is
  rolled — exactly once per town, the root of all determinism).
- ViewModels never see Room entities (mapping isolated in `core/data/repo/
  Mappers.kt`) and never see the renderer (only `SceneCommandSink`).
- `SavedStateHandle` carries the `townId` route argument, so process death
  during play recovers cleanly: the authoritative town state always
  re-hydrates from the command log on disk.

Screens in this phase:

| Screen | ViewModel state | Notes |
|---|---|---|
| Menu | diorama count, last draft chip | reactive: Room flows |
| Play | `Setup` → `Building(meta, townState)` | Setup = grid size + name; Building shows the engine-surface placeholder |
| Gallery | list of `TownMeta` | adaptive grid of SnowGlobeCards, soft delete, empty state |

## 3. Navigation system

`navigation/Routes.kt` is the single route contract; `GladeNavHost.kt` builds
the graph: `menu` → `play?townId={id}` / `gallery`, gallery → play(townId).
`townId` is optional and nullable — its absence *is* the "new town" signal,
which keeps one Play destination handling both creation and resumption (and
later, reopening sealed dioramas as copies). Explore joins the graph in
Phase 4 as a sibling of Play sharing a nav-graph-scoped town session.

## 4. Data models (core/domain/model)

- `GridSpec` / `GridSize` — 20/50/100 boards; world sizes in **fixed-point**
  (1/64 units) because persisted or decision-feeding numbers must be
  bit-identical across devices (TDD §6, §8.1).
- `TownMeta` / `TownStats` / `TownStatus` — gallery-facing metadata, mirrors
  the `diorama` table.
- `Structure`, `Footprint`, `StructureArchetype` — the building vocabulary.
  Footprint cells pack (x, y) into a single Int: compact, allocation-light,
  deterministic to sort. The resolved visual plan (modules, roofs, props) is
  deliberately NOT stored — it regenerates from (archetype, footprint, seed).
- `TimeOfDay` — four art-directed lighting states.
- `TownState` — the live, immutable reduction of the command log, built on
  persistent collections (cheap structural sharing → free undo history).
  `structuresOrdered()` is the only sanctioned iteration order: unordered map
  iteration is the classic hidden nondeterminism bug and is banned.

## 5. Save system architecture (the load-bearing decision)

**Event sourcing:** a town IS its command log. `TownCommand` (sealed,
`@Serializable`) records player inputs *plus the derived seeds used*, never
generated geometry. `TownReducer` is a pure fold: `TownState =
replay(commands)`.

Consequences, all already realized in code:

1. **Permanence** — replaying the log reproduces the identical town forever;
   `generatorVersion` is stamped into every blob so future rule changes can
   keep sealed towns rendering under their original rules (TDD §7.4).
2. **Tiny saves** — a whole town is kilobytes of CBOR.
3. **Crash safety** — saving is rewriting a small file atomically:
   `TownBlobStore` writes `log.bin.tmp` → rotates the old file to `log.bak`
   → renames; a CRC32 in the `CommandLogCodec` header rejects torn files and
   falls back to the backup.
4. **Undo/redo (Phase 1)** — a pointer into the log; `rewriteCommands`
   already exists for trimming discarded redo tails on exit.

Storage split (TDD §7.1): **Room for queryable metadata, filesystem blobs for
content, (later) WebP files for thumbnails** — each job gets its best tool.

```
Room: diorama (metadata, indices)  town_blob (path+CRC)  settings
Disk: files/towns/{id}/log.bin (+ .bak)        [Phase 4: state.bin, thumbs/]
```

Blob format: `[magic][blobVersion][generatorVersion][len][crc32][gzip(cbor)]`.
The header is frozen; payload evolution rides on `blobVersion` and the
additive nature of sealed-class CBOR polymorphism (new command types append,
existing ones never change shape).

## 6. Room database setup

Schema v1 (TDD §16): `diorama`, `town_blob` (FK cascade), `settings`, with
indices for gallery sort and the soft-delete purge query. Schemas export to
`app/schemas/` for CI diffing; destructive migrations are forbidden — every
future version ships an explicit `Migration`. Soft delete (`is_deleted` +
30-day `delete_after`) protects the "permanent collection" promise from
accidental taps; `purgeExpired` is wired for a later WorkManager job.

Foundation simplification (ADR-003): the gallery uses `Flow<List<TownMeta>>`
rather than Paging 3. Collections of a few hundred rows render fine in a lazy
grid; the DAO gains a `PagingSource` overload when collections grow.

## 7. What lands next (phase plug-in points)

| Phase | Arrives | Plugs into |
|---|---|---|
| 1 | Filament host, touch→stroke capture, recognition, placeholder buildings, undo | `SceneCommandSink` impl swap; new `TownCommand` fields are additive; reducer already handles Place/Demolish/Reroll |
| 2 | Module kit, real generators, roof solver | consumes `Structure(archetype, footprint, seed)` — persistence unchanged |
| 3 | Merge rules, paths, autosave debounce, perf pass | `AdjacencyAnalyzer` over `TownState`; `appendCommands` already atomic |
| 4 | Seal flow, thumbnails, Explore, day/night transitions | `thumb_version` column + `SealTown` command reserved; `TimeOfDay` model in place |

## 8. Testing posture

The determinism-critical core is tested from day one:

- `SplitMix64Test` — seed derivation stability (a failure here would silently
  change every saved town: release blocker).
- `TownReducerTest` — replay correctness, seq-order independence, reroll
  isolation.
- `CommandLogCodecTest` — round-trip fidelity, CRC corruption rejection,
  magic validation.

Phase 2 adds the desktop genlab harness with golden plan-hash tests; Phase 3
adds Robolectric repository tests and fault-injection (disk full, torn write).
