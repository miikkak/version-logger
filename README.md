# version-logger

A Velocity plugin that logs each player's username, UUID, and client protocol version on login.

## Why

Velocity routes players on outdated clients to a limbo server, and only the lobby has
ViaVersion's status logging. That means the client version never gets logged for a player who
gets bounced to limbo. This plugin logs username + UUID + protocol version unconditionally for
every player who logs in, regardless of where the proxy ends up routing them. The UUID is
included because a username alone doesn't survive a player renaming their account - the UUID
does.

## How it works

Listens for Velocity's `PostLoginEvent`
(`com.velocitypowered.api.event.connection.PostLoginEvent`), which fires once a player is
authenticated (Mojang
online-mode auth has already happened by this point - the proxy is always online-mode, so
pre-auth connection attempts aren't logged and aren't of interest) and fully connected to the
proxy, but before Velocity decides which backend server to send them to. Logging here guarantees
the message is written before any limbo-vs-lobby routing decision, for every player, every time:

```text
[INFO] [velocity]: playername (a1b2c3d4-e5f6-7890-abcd-ef1234567890) connected with protocol 769 (1.21.4)
```

## Requirements

- JDK 25 to build (Gradle toolchain-managed) - pinned to match the Oracle GraalVM 25.x used by
  the deployment containers, not a Velocity version-support requirement
- Velocity 4.x

## Building

```bash
./gradlew build
```

## Releases

A merged PR labeled `release:major`, `release:minor`, or `release:patch` triggers
`semantic-release` on merge to `main`, which tags the resulting commit `vX.Y.Z`. That tag push
triggers the `Release` workflow, which builds the jar and attaches it to a GitHub Release.
`release:none` skips this entirely - use it for docs/CI-only changes.

## Testing a release build

Tagging `main` with `vX.Y.Z` (or running the `Release` workflow manually with a `tag` input)
builds the jar and attaches it to a GitHub Release. Download and drop it into a Velocity
server's `plugins/` directory to test:

```bash
gh release download vX.Y.Z -R miikkak/version-logger -p '*.jar' -D /path/to/velocity/plugins/
```

There is no automated deploy yet - this is manual, on-demand testing only.

## Design notes

- Dependency versions are pinned in `gradle.lockfile` (`dependencyLocking` in `build.gradle.kts`)
  so CI vulnerability scanning has a real dependency graph to check. Gradle fails the build if a
  declared dependency's resolved version drifts from the lock - after bumping a version in
  `build.gradle.kts`, regenerate it with `./gradlew dependencies --write-locks` and commit the
  result alongside the change.
  - `com.velocitypowered:velocity-brigadier:1.0.0-SNAPSHOT` is a transitive dependency of
    `velocity-api:4.0.0` and is locked like everything else, but SNAPSHOT artifacts are mutable
    on the remote repo - the lock pins the version string, not the artifact content, so this one
    dependency doesn't get the same reproducibility guarantee as the rest of the lockfile. This
    is inherited from Velocity's own POM, not something fixable here.
- The plugin's reported version (shown in Velocity's "Loaded plugin ..." log line) is generated
  from the Gradle project version at build time, so it can't drift from the jar filename.

## License

TBD
