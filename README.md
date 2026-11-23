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
  <img alt="C#" src="https://img.shields.io/badge/language-C%23-512BD4?logo=dotnet&logoColor=white">
  <img alt="WebAssembly" src="https://img.shields.io/badge/WebAssembly-654FF0?logo=webassembly&logoColor=white">
  <img alt="Plugin ABI" src="https://img.shields.io/badge/plugin_abi-1-000080">
  <img alt="License" src="https://img.shields.io/badge/license-GPL--3.0-green">
</p>

## About

A greeter that pays coins for playing, a leaderboard and a shop to spend them in. It shows what a real plugin looks
like against the plugin ABI, and its code tracks the ABI as it evolves. This branch is written in C# with the SDK
that [Kiln](https://github.com/BasaltProject/Kiln) ships.

The plugin is exactly the same in every language, one branch each:

| | Branches |
|---|---|
| Recommended | `cpp` (default), `zig`, `rust` |
| By preference | `assemblyscript`, `go`, `kotlin`, `csharp`, `java` |

The by-preference branches are not the choice when maximum performance is the goal.

| File | API surface |
|---|---|
| `src/Coins/Wallet.cs` | `data_get_i64` and `data_set_i64`, a balance stored per player |
| `src/Events/PlayerListeners.cs` | `on("player.join", ...)`, `every(...)`, `placeholder(...)` |
| `src/Shop/Storefront.cs` | `menu_create` and `menu_set` with click handlers |
| `src/Commands/CoinCommands.cs` | `command(...)` with literal, word and ranged int arguments |
| `src/Main.cs` | Plugin entry, the `plugin.enable` event and `log_info` |
| `src/Events/EventExamples.cs` | `event_on_ex` with filters, `event_get` and `event_set`, player properties, forms, dialogs and plugin events |
| `src/Services/Server.cs` | Permissions, broadcasts, `server_call`, files, the key-value store, services, delayed tasks and dynamic commands |
| `src/Worlds/WorldTour.cs` | Worlds, chunks, blocks, structures and noise |
| `src/Hud/DisplayExamples.cs` | Scoreboards, boss bars, holograms, NPCs, the tab list and sidebar surfaces |
| `src/Items/InventoryCommands.cs` | Inventories, ender chests, cooldowns, recipes, menus, merchants and loot |
| `src/Entities/MobExamples.cs` | Spawning, motion, properties, goals, pathing, snapshots and spawn rules |
| `src/Packets/ChannelExamples.cs` | Packet and channel listeners, sending and receiving packets |
| `resources/` | `config.toml` and `lang/en_us.toml` messages |

The other areas are tours: each one registers commands (`/srv`, `/worlds`, `/hud`, `/items`, `/entities`, `/pkt`)
that call the imports of one area.

## Trying it

```bash
kiln build && kiln check && kiln pack
```

Drop the `.bspkg` from `build/` into the server's `plugins/` folder. Join to get a welcome bonus, use `/coins` and
`/coins top`, and `/shop` to spend them.

`test/run.sh` replays every `test/*scenario.txt` with `basalt-wasm-trace` (from Wire's tools, on `PATH` or in
`BASALT_WASM_TRACE`) against `build/plugin.wasm` and diffs each output with its `*expected.trace`, which is identical on
every branch.

## License

[GPL-3.0](LICENSE).

Basalt is not affiliated with Mojang Studios or Microsoft. Minecraft is a trademark of Mojang Studios.
