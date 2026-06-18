# Glade Towns — Rooms, Walls & Explore-view fix

## What changed
This drop turns each drawn shape into an **interior room** with **wall tiles**,
adds **8 room types** chosen by shape + size, and fixes the broken Explore
viewport. The event-sourced save format is unchanged — room type and walls are
derived at render time.

### Files
**NEW**
- `core/domain/model/structure/RoomType.kt` — `RoomType` enum + `RoomClassifier`.
- `app/src/test/.../core/domain/RoomTypeTest.kt` — classifier tests.

**MODIFIED**
- `core/ui/render/TownRenderer.kt` — now renders rooms: floor tiles per type +
  beveled wall tiles on border cells, with a precomputed `TownRoomModel`.
- `feature/explore/ExploreScreen.kt` — **layout fix** (Column + weighted
  viewport) so the diorama fills the screen; inspector now names the room.
- `feature/play/PlayScreen.kt` — same room rendering; placement toast names the
  room that was drawn.

> `DrawShapeUseCase` already prevents overlap (it clips cells already owned by
> another structure), so "rooms can't overlap" needed no change.

## Walls vs. hallways
Every border tile of a room is a **wall**, *unless* it faces a **hallway** —
defined as an adjacent **path** tile or an adjacent **different room**. Those
faces become open floor (a doorway). Interior tiles are floor. **Gardens** and
**doorways** render open (no walls).

## Room table (shape recovered from archetype; size = bounding box, larger side `s`)
| Room | Shape | Size rule |
|---|---|---|
| Garden | any | `s ≥ 10` |
| Doorway | rectangle | thin (min side ≤ 1) or `s ≤ 2` |
| Bathroom | rectangle | `s ≤ 4` |
| Bedroom | rectangle | `5 ≤ s ≤ 7` |
| Kitchen | rectangle | `8 ≤ s ≤ 9` |
| Common area | circle/oval | `s ≤ 9` |
| TV area | triangle | `s ≤ 9` |
| Lounge | irregular | `s ≤ 9` |

### Decisions on the spec's ambiguities (tweak in `RoomClassifier`)
- Your list jumped from #5 to #7, so **#6 became "Lounge"** — the room for
  irregular shapes under 10×10 (otherwise nothing handled them). Total = 8.
- Overlapping ranges were made **non-overlapping & exhaustive**: bedroom owns 7
  ("no more"), kitchen is 8–9, and **Garden wins at exactly 10** (it's the only
  "any shape, 10 or more" rule), so kitchen/common cap at 9.
- **Door vs. bathroom** (both small rectangles) split by shape: a *thin* or
  *tiny* rectangle is a door; anything else small is a bathroom.

All thresholds live in one place (`RoomClassifier.classify`) if you want to
re-balance them.
