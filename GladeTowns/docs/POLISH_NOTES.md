# Glade Towns — tile alignment, wall completion & menu polish

## 1. Grid now matches the real tiles
The grid was drawn with at most 50 lines, but a LARGE town has 100 cells — so
each grid square actually contained a 2×2 block of real tiles (the "4 tiles
inside a tile" you saw; the centre dots were the true per-tile marks). The grid
is now drawn at the real cell resolution, so **one grid square == one tile**
that gets counted when drawing.

## 2. Walls always close
A border tile used to drop its wall whenever *any* neighbour was a different
room OR a road, which left gaps. Now a tile is a wall when it faces the outside
— exterior ground, the board edge, **or a different room** — and a wall only
opens into a **doorway where a hallway/road touches it**. Perimeters and the
boundary between two rooms are always closed.

## 3. Prettier menu
New `AnimatedLandscape` backdrop: a warm sky, a hazy bobbing sun, clouds that
drift and loop, and three parallax rolling-hill layers — all drawn with Canvas
(no assets), so it loops forever cheaply. The menu blurs it (`Modifier.blur`,
a no-op below Android 12) and lays a soft scrim on top so the title and buttons
stay crisp.
