<p align="center">
  <img src="docs/assets/basalt-logo.png" alt="Basalt" width="360">
</p>

<h2 align="center">ServerPlugin-Example</h2>

<p align="center">
  <strong>A reference plugin for Basalt Server's plugin API.</strong>
</p>

<p align="center">
  <img alt="Minecraft" src="https://img.shields.io/badge/Minecraft-26.3-62B47A?logo=minecraft&logoColor=white">
  <img alt="Lua" src="https://img.shields.io/badge/Lua-5.5-000080?logo=lua&logoColor=white">
  <img alt="Plugin API" src="https://img.shields.io/badge/plugin_api-1-000080">
  <img alt="License" src="https://img.shields.io/badge/license-GPL--3.0-green">
</p>

## Overview

This repository is a reference plugin for [Basalt Server](https://github.com/BasaltProject/Server)'s
native Lua plugin API. It exists to show what a real plugin looks like against
that API, so its code tracks the API as it evolves rather than freezing at
whatever it looked like when it was written.

The plugin itself is a greeter that pays coins for playing, a leaderboard, and
a shop to spend them in.

It is a `.bspkg` package in source form: `plugin.toml` at the root, code under
`src/`, everything else under `resources/`. Dropped into a server's `plugins/`
folder as a loose directory it loads exactly as it would packed, which is the
point of that layout: iterating on it is saving a file, not building a zip.

## What it shows

| File | API surface |
|---|---|
| `plugin.toml` | The manifest: id, version, `api`, and the one capability it asks for (`players_data`) |
| `src/main.lua` | The entry point. It only wires the other modules together |
| `src/coins.lua` | Reading and writing `player.data`, a balance namespaced to this plugin in `players.db` |
| `src/events.lua` | `on("player.join", ...)`, `every(...)` for a recurring payout, and `placeholder(...)` |
| `src/shop.lua` | `menu{...}`, `menu:set(...)` with a click handler, and items as plain tables |
| `src/commands.lua` | `cmd(...)` with a literal, a word argument, an int argument with a range, and `store.top(...)` |
| `resources/config.toml` | Coin amounts and prices, read back as `config.welcome_bonus`, `config.shop.sparkles_price`, ... |
| `resources/lang/en_us.toml` | Every message the plugin sends, as `@key`, resolved through `t(...)` and `player:send(...)` |

Nothing here needs `packets`, `network` or `storage`: it sticks to the parts of
the API most plugins reach for first. A single `src/main.lua` is a complete
plugin on its own; splitting this one into `coins.lua`, `shop.lua`,
`events.lua` and `commands.lua` is just what makes a plugin this size easier
to read.

## Trying it out

Copy this repository's contents into `plugins/example/` of a Basalt Server
install (or symlink it there while editing), and start the server:

```bash
cp -r ServerPlugin-Example /path/to/server/plugins/example
```

Then, in game:

- Join: a welcome message and a one-time coin bonus. Join again and it is a
  shorter "welcome back" with the current balance instead.
- `/coins` shows your own balance, `/coins <player>` shows somebody else's, and
  `/coins top [limit]` prints the leaderboard.
- `/shop` opens a menu with two items; buying one takes the coins and plays a
  particle effect or a sound.

Coins keep trickling in every minute (`payout-period` in `config.toml`) for
whoever is online.

## License

ServerPlugin-Example is distributed under the [GNU General Public License v3.0](LICENSE).

Basalt is not affiliated with Mojang Studios or Microsoft. Minecraft is a
trademark of Mojang Studios.
