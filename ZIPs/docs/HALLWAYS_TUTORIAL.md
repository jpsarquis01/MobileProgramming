# Glade Towns — Hallways, the room guide, and brick walls

Builds on `ROOMS_UPDATE.md`. Three additions, all faithful to the
event-sourced design (every new feature is just another command or a
render-time derivation).

## 1. In-app tutorial (info button)
- New **info icon** in the Play and Explore top bars opens `RoomGuideDialog`.
- The dialog lists every room with a **drawn picture** (Compose Canvas — no
  image assets) of the shape that makes it, plus its size rule. The rules are
  written to mirror `RoomClassifier`, so guide and gameplay never drift.

> **Why every town came out all-green (fixed):** the classifier used to check
> size *before* shape (`≥10 tiles → Garden`), so on a 100×100 grid every
> finger-drawn blob — huge by tile count — became a garden. It's now
> **shape-first** (see below) and the Play screen supports **pinch-zoom**, so
> you can zoom in and draw precise small rooms.

## Shape-first room rules (revised)
The room is decided by the *shape* you draw, then size only chooses among the
rectangle-family rooms — so any drawing resolves to a usable room and "all
gardens" can't happen by accident:

| Draw this | You get | Size |
|---|---|---|
| Circle / oval | Common area | any size |
| Triangle | TV area | any size |
| Small shape | Bathroom | up to 4×4 |
| Shape | Bedroom | 5×5–7×7 |
| Shape | Kitchen | 8×8–10×10 |
| Large freeform | Garden | 11×11+ |
| Thin / tiny rectangle | Doorway | — |

The old "irregular → Lounge" dead-end is gone: an irregular blob is just sized
like a rectangle. `RoomType.LOUNGE` stays in the enum (unused) for save compat.

## Pinch-zoom while drawing
- Play's canvas: **one finger draws, two fingers pinch-zoom + pan**, plus
  +/−/recenter buttons. Zoom is baked into `GridMapping` (not a graphicsLayer),
  so finger position and tiles stay aligned at any zoom — essential for drawing
  a 4-tile bathroom on a 100×100 board.

## 2. Hallways (draw a line to connect rooms)
- A **Room / Hallway** toggle sits above the time bar in Play.
- In **Hallway** mode, dragging traces a line; the cells it crosses become
  walkable **path** cells (rendered as roads). A Bresenham fill keeps the line
  unbroken on fast swipes.
- Where a hallway touches a room's border tile, that tile stops being a wall
  and becomes a **doorway** (the renderer already opens walls that face a
  path). Hallway cells that fall on a room are ignored, so the no-overlap rule
  still holds.

### How it flows through the architecture
- New `TownCommand.DrawPath(cells)` — CBOR-additive, so old saves still load.
- `TownReducer` folds it into `TownState.paths` (a set of packed cells).
- `TownGrowthEngine.setManualPaths(...)` unions those cells into the layout's
  roads at `snapshot()` time (skipping cells under buildings). Re-applied in
  `PlayViewModel.emit()` and `LoadTownLayoutUseCase`, so hallways survive
  reroll/demolish/undo and reload identically.

## 3. Brick wall texture
- `drawWallTile` now paints a mid mortar course plus a running-bond vertical
  joint (offset per row), so wall rings read as masonry instead of flat blocks.

## Tests
- `PathCommandTest` — DrawPath records cells; manual paths show up as roads and
  are clipped to the board.
- `RoomTypeTest` — unchanged classifier coverage.
