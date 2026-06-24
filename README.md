# Cord HUD

Client-side Fabric mod for the Chaos Cubed event. Tracks the live
coordinates of every player your client currently knows about, on any
server — no server-side install required.

- Press **Numpad 6** to open the Player Coordinate Tracker GUI.
- Lists each visible player's name, X/Y/Z, dimension, and distance from you.
- Search box to filter by name, button to toggle sort (name / distance).
- Pure client mod (`"environment": "client"` in `fabric.mod.json`) — install
  it on your own client only, the server doesn't need it.

Note: this only shows players your client has actually received entity data
for from the server (normal player tracking/render distance rules apply).
It cannot reveal players the server never sends to your client.

## Requirements

- Minecraft 1.26.2 ("Chaos Cubed Update")
- Fabric Loader
- Fabric API
- Java 21

## Building

```
./gradlew build
```

The compiled jar will be in `build/libs/cord-hud-1.0.0.jar`. Drop it into
your `.minecraft/mods` folder along with the matching Fabric API version.

If `minecraft_version` / `yarn_mappings` / `fabric_version` in
`gradle.properties` don't match what's published on
https://fabricmc.net/develop/ for 1.26.2 by the time you build, update them
to the real values — they're often only known once that version actually
ships.
