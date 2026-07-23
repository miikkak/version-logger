# version-logger

A Velocity plugin that logs each player's username and client protocol version on login.

## Why

Velocity routes players on outdated clients to a limbo server, and only the lobby has
ViaVersion's status logging. That means the client version never gets logged for a player who
gets bounced to limbo. This plugin logs username + protocol version unconditionally for every
player who logs in, regardless of where the proxy ends up routing them.

## How it works

Listens for Velocity's `PostLoginEvent`, which fires once a player is authenticated (Mojang
online-mode auth has already happened by this point - the proxy is always online-mode, so
pre-auth connection attempts aren't logged and aren't of interest) and fully connected to the
proxy, but before Velocity decides which backend server to send them to. Logging here guarantees
the message is written before any limbo-vs-lobby routing decision, for every player, every time:

```text
[INFO] [velocity]: playername connected with protocol 769 (1.21.4)
```

## Requirements

- JDK 25 to build (Gradle toolchain-managed)
- Velocity 4.x

## Building

```bash
./gradlew build
```

## Testing a release build

Tagging `main` with `vX.Y.Z` (or running the `Release` workflow manually with a `tag` input)
builds the jar and attaches it to a GitHub Release. Download and drop it into a Velocity
server's `plugins/` directory to test:

```bash
gh release download vX.Y.Z -R miikkak/version-logger -p '*.jar' -D /path/to/velocity/plugins/
```

There is no automated deploy yet - this is manual, on-demand testing only.

## Design notes

- The plugin's reported version (shown in Velocity's "Loaded plugin ..." log line) is generated
  from the Gradle project version at build time, so it can't drift from the jar filename.

## License

TBD
