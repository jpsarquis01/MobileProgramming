# Glade Towns — bigger rooms, diorama furniture & music

## 1. Room sizes scaled up
Drawing a 4×4 room on a 100×100 board felt tiny, so the size bands are larger
(constants in `RoomClassifier`, easy to retune):

| Room | Larger side (tiles) |
|---|---|
| Doorway | thin, or ≤ 2 |
| Bathroom | ≤ 8 |
| Bedroom | 9 – 14 |
| Kitchen | 15 – 22 |
| Garden | 23 + |

Circle → Common area and triangle → TV area are still any size.

## 2. Furniture in dioramas
Open a sealed creation from **Dioramas** (or the Explore preview while building)
and each room is now decorated with matched top-down furniture, drawn purely
with Canvas (no image assets) in `FurnitureRenderer.kt`:

- **Bedroom** → bed (frame, pillow, blanket)
- **Bathroom** → toilet + sink
- **Kitchen** → counter with stove burners + sink
- **Common area** → sofa with cushions
- **TV area** → flat-screen on a stand + a couch facing it
- **Garden** → scattered trees + flowers
- **Doorway** → left empty

Furniture is sized to each room's interior and only shows in the diorama view
(`drawRooms(..., decorate = true)`), so the editor stays clean while building.

## 3. Chill background music
`MusicManager` loops a low-volume track and is driven by the activity lifecycle
(plays on resume, pauses on stop). A **mute toggle** sits in the menu's top-right.

> **You must add the audio file** — I can't bundle binary audio. Drop a track at:
> ```
> app/src/main/res/raw/chill_music.mp3      (or .ogg / .m4a)
> ```
> Raw resource names must be lowercase letters/digits/underscores. The track is
> resolved by name at runtime, so the app builds and runs fine without it (music
> is simply silent until you add one). Any calm CC0/royalty-free lo-fi loop works.
