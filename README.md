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
  <strong>A reference plugin for Basalt Server's Lua plugin API.</strong>
</p>

<p align="center">
  <img alt="Java Edition" src="https://img.shields.io/badge/Java_Edition-26.3-62B47A?logo=minecraft&logoColor=white">
  <img alt="Lua" src="https://img.shields.io/badge/Lua-5.5.1-000080?logo=lua&logoColor=white">
  <img alt="Plugin API" src="https://img.shields.io/badge/plugin_api-1-000080">
  <img alt="License" src="https://img.shields.io/badge/license-GPL--3.0-green">
</p>

## About

A greeter that pays coins for playing, a leaderboard and a shop to spend them in. It shows what a real plugin looks
like against the native Lua API, and its code tracks the API as it evolves.

It is a `.bspkg` in source form (`plugin.toml`, code under `src/`, everything else under `resources/`), so dropping it
into `plugins/` as a loose folder loads it exactly as it would packed.

| File | API surface |
|---|---|
| `src/coins.lua` | `player.data`, a balance stored per plugin |
| `src/events.lua` | `on("player.join", ...)`, `every(...)`, `placeholder(...)` |
| `src/shop.lua` | `menu{...}` with click handlers |
| `src/commands.lua` | `cmd(...)` with literal, word and ranged int arguments |
| `resources/` | `config.toml` and `lang/en_us.toml` messages |

## Trying it

```bash
cp -r ServerPlugin-Example /path/to/server/plugins/example
```

Join to get a welcome bonus, use `/coins` and `/coins top`, and `/shop` to spend them. The
[Kiln](https://github.com/BasaltProject/Kiln) check runs on every push; fork this repository for your own plugin and
it runs there too.

## License

[GPL-3.0](LICENSE).

Basalt is not affiliated with Mojang Studios or Microsoft. Minecraft is a trademark of Mojang Studios.
