# Session Switch (Fabric, Minecraft 1.21.4)

Adds a "Session Login" button to the multiplayer screen. Paste your own session ID (access token),
and the game switches to that account until you restore the original or quit.

## What it does / doesn't do
- One network request: GET https://api.minecraftservices.com/minecraft/profile (to validate the token).
- Token is kept in memory only. Never written to disk, never logged, never sent anywhere else.
- No file access, no process execution, no reflection, no other dependencies.
- While swapped, chat-signing keys are disabled (they belong to the original account), so servers that
  enforce secure chat signing may reject chat messages.

## Build
Requires JDK 21.
    gradle wrapper --gradle-version 8.12   # once, if you don't have ./gradlew
    ./gradlew build
Output: build/libs/sessionswitch-1.0.0.jar  (use this one, not the -sources jar)

Install into .minecraft/mods on a Fabric 1.21.4 profile with Fabric API.

## Build on GitHub (no local setup)
1. Create a new repo and push this folder to it (branch `main`).
2. Open the **Actions** tab. The "Build" workflow runs on every push; you can also run it by hand.
3. When it finishes, open the run and download the **sessionswitch** artifact (a zip containing the jar).
4. For a permanent download link: `git tag v1.0.0 && git push origin v1.0.0` and the jar is attached to a Release.

## If the build fails
Version numbers live in gradle.properties; check https://fabricmc.net/develop/ for current ones.
Only use this with accounts you own, and follow each server's rules.
