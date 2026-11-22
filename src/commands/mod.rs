use super::{coins, shop};
use basalt_server::core::{Args, Ctx};

fn balance(ctx: Ctx) {
    if let Some(target) = basalt_server::commands::arg_str(ctx, "target") {
        let player = basalt_server::players::by_name(&target);
        if player == 0 {
            basalt_server::commands::reply(ctx, "@balance-usage", &Args::new());
            return;
        }
        let name = basalt_server::players::name(player).unwrap_or_default();
        let args = Args::new().add("name", &name).add("amount", &coins::get(player).to_string());
        basalt_server::commands::reply(ctx, "@balance-other", &args);
        return;
    }
    if basalt_server::commands::is_console(ctx) {
        basalt_server::commands::reply(ctx, "@balance-usage", &Args::new());
        return;
    }
    let source = basalt_server::commands::source_name(ctx).unwrap_or_default();
    let player = basalt_server::players::by_name(&source);
    let args = Args::new().add("amount", &coins::get(player).to_string());
    basalt_server::commands::reply(ctx, "@balance-self", &args);
}

fn top(ctx: Ctx) {
    let limit = basalt_server::commands::arg_i64(ctx, "limit").unwrap_or(10);
    let rows = basalt_server::data::store_top("coins", limit as i32);
    if rows.is_empty() {
        basalt_server::commands::reply(ctx, "@top-empty", &Args::new());
        return;
    }
    basalt_server::commands::reply(ctx, "@top-header", &Args::new());
    for (index, (name, amount)) in rows.iter().enumerate() {
        let args = Args::new()
            .add("rank", &(index + 1).to_string())
            .add("name", name)
            .add("amount", &amount.to_string());
        basalt_server::commands::reply(ctx, "@top-row", &args);
    }
}

fn open_shop(ctx: Ctx) {
    if basalt_server::commands::is_console(ctx) {
        basalt_server::commands::reply(ctx, "@shop-players-only", &Args::new());
        return;
    }
    let source = basalt_server::commands::source_name(ctx).unwrap_or_default();
    basalt_server::menus::menu_open(shop::menu(), basalt_server::players::by_name(&source));
}

pub fn register() {
    basalt_server::commands::register("coins [target: word]", "", balance);
    basalt_server::commands::register("coins top [limit: int 1..25]", "", top);
    basalt_server::commands::register("shop", "example.shop", open_shop);
}
