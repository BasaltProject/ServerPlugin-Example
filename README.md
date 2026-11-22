<p align="center">
  <a href="https://raw.githubusercontent.com/BasaltProject/.github/main/assets/basalt-logo-white.svg#gh-dark-mode-only">
    <img src="https://raw.githubusercontent.com/BasaltProject/.github/main/assets/basalt-logo-white.svg" alt="Basalt" width="360">
  </a>
  <a href="https://raw.githubusercontent.com/BasaltProject/.github/main/assets/basalt-logo-black.svg#gh-light-mode-only">
    <img src="https://raw.githubusercontent.com/BasaltProject/.github/main/assets/basalt-logo-black.svg" alt="Basalt" width="360">
  </a>
</p>

<h2 align="center">ServerPlugin-Example</h2>

<p align="center">
  <strong>A reference plugin for Basalt Server's WebAssembly plugin API.</strong>
</p>

<p align="center">
  <img alt="Java Edition" src="https://img.shields.io/badge/Java_Edition-26.3-62B47A?logo=minecraft&logoColor=white">
  <img alt="Go" src="https://img.shields.io/badge/language-Go_(TinyGo)-00ADD8?logo=go&logoColor=white">
  <img alt="WebAssembly" src="https://img.shields.io/badge/WebAssembly-654FF0?logo=webassembly&logoColor=white">
  <img alt="Plugin ABI" src="https://img.shields.io/badge/plugin_abi-1-000080">
  <img alt="License" src="https://img.shields.io/badge/license-GPL--3.0-green">
</p>

## About

A greeter that pays coins for playing, a leaderboard and a shop to spend them in. It shows what a real plugin looks
like against the plugin ABI, and its code tracks the ABI as it evolves. This branch is written in Go with the SDK
that [Kiln](https://github.com/BasaltProject/Kiln) ships.

The plugin is exactly the same in every language, one branch each:

| | Branches |
|---|---|
| Recommended | `cpp` (default), `zig`, `rust` |
| By preference | `assemblyscript`, `go`, `kotlin`, `csharp`, `java` |

The by-preference branches are not the choice when maximum performance is the goal.

| File | API surface |
|---|---|
| `internal/coins/coins.go` | `data_get_i64`, a balance stored per plugin |
| `internal/events/events.go` | `on("player.join", ...)`, `every(...)`, `placeholder(...)` |
| `internal/shop/shop.go` | `menu_create` and `menu_set` with click handlers |
| `internal/commands/commands.go` | `command(...)` with literal, word and ranged int arguments |
| `resources/` | `config.toml` and `lang/en_us.toml` messages |

## Trying it

```bash
kiln build && kiln pack
```

Drop the `.bspkg` from `build/` into the server's `plugins/` folder. Join to get a welcome bonus, use `/coins` and
`/coins top`, and `/shop` to spend them. `test/run.sh` replays `test/scenario.txt` with `basalt-wasm-trace` and diffs
the output with `test/expected.trace`, which is identical on every branch.

## License

[GPL-3.0](LICENSE).

Basalt is not affiliated with Mojang Studios or Microsoft. Minecraft is a trademark of Mojang Studios.
