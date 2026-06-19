# Glade Towns — Hallways, the room guide, and brick walls

Builds on `ROOMS_UPDATE.md`. Three additions, all faithful to the
event-sourced design (every new feature is just another command or a
render-time derivation).

## 1. In-app tutorial (info button)
- New **info icon** in the Play and Explore top bars opens `RoomGuideDialog`.
- The dialog lists every room with a **drawn picture** (Compose Canvas — no
  image assets) of the shape that makes it, plus its size rule. The rules are
  written to mirror `RoomClassifier`, so guide and gameplay never drift.

> **Why your towns were all green:** a room's type is decided by the drawn
> bounding box, and **anything 10×10 or larger becomes a Garden** (rendered
> open, no walls). On a 50/100 grid a finger-drawn blob easily exceeds 10
> tiles, so almost everything was a garden. Draw **small** shapes — or use the
> 20×20 board — to get bathrooms/bedrooms/kitchens and see the wall textures.

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
