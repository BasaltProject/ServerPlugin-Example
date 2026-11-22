#include "commands.hpp"

#include "coins.hpp"
#include "shop.hpp"
#include "text.hpp"

namespace commands {

namespace {

long long read_i64(const char* at) {
    unsigned long long value = 0;
    for (int i = 7; i >= 0; --i) {
        value = (value << 8) | static_cast<unsigned char>(at[i]);
    }
    return static_cast<long long>(value);
}

int read_u32(const char* at) {
    return static_cast<unsigned char>(at[0]) | (static_cast<unsigned char>(at[1]) << 8) |
           (static_cast<unsigned char>(at[2]) << 16) | (static_cast<unsigned char>(at[3]) << 24);
}

void balance(basalt::Ctx ctx) {
    const auto target = basalt::command_arg_str(ctx, "target");
    if (target.ok()) {
        const basalt::Player player = basalt::player_by_name(target.c_str());
        if (player == 0) {
            basalt::command_reply(ctx, "@balance-usage");
            return;
        }
        const auto name = basalt::player_name(player);
        basalt::Args args;
        args.add("name", name.c_str()).add("amount", Buffer().add(coins::get(player)).c_str());
        basalt::command_reply(ctx, "@balance-other", &args);
        return;
    }
    if (basalt::command_is_console(ctx)) {
        basalt::command_reply(ctx, "@balance-usage");
        return;
    }
    const auto source = basalt::command_source_name(ctx);
    const basalt::Player player = basalt::player_by_name(source.c_str());
    basalt::Args args;
    args.add("amount", Buffer().add(coins::get(player)).c_str());
    basalt::command_reply(ctx, "@balance-self", &args);
}

void top(basalt::Ctx ctx) {
    long long limit = 10;
    basalt::command_arg_i64(ctx, "limit", &limit);
    char rows[1024];
    const int size = basalt::store_top("coins", static_cast<int>(limit), rows, sizeof rows);
    if (size <= 0 || size > static_cast<int>(sizeof rows)) {
        basalt::command_reply(ctx, "@top-empty");
        return;
    }
    basalt::command_reply(ctx, "@top-header");
    int at = 0;
    for (long long rank = 1; at + 4 <= size; ++rank) {
        const int length = read_u32(rows + at);
        char name[64] = {};
        for (int i = 0; i < length && i < 63; ++i) {
            name[i] = rows[at + 4 + i];
        }
        const long long amount = read_i64(rows + at + 4 + length);
        at += 4 + length + 8;
        basalt::Args args;
        args.add("rank", Buffer().add(rank).c_str()).add("name", name).add("amount", Buffer().add(amount).c_str());
        basalt::command_reply(ctx, "@top-row", &args);
    }
}

void open_shop(basalt::Ctx ctx) {
    if (basalt::command_is_console(ctx)) {
        basalt::command_reply(ctx, "@shop-players-only");
        return;
    }
    const auto source = basalt::command_source_name(ctx);
    basalt::menu_open(shop::menu(), basalt::player_by_name(source.c_str()));
}

} // namespace

void register_all() {
    basalt::command("coins [target: word]", "", balance);
    basalt::command("coins top [limit: int 1..25]", "", top);
    basalt::command("shop", "example.shop", open_shop);
}

} // namespace commands
