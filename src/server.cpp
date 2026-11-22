#include "server.hpp"

#include "text.hpp"

namespace server_area {

namespace {

namespace srv = basalt::server;

using Doc = basalt::ValueBuffer<256>;

void say(basalt::Ctx ctx, const char *text) {
  basalt::Args args;
  args.add("text", text);
  basalt::command_reply(ctx, "@server-line", &args);
}

void say_number(basalt::Ctx ctx, const char *label, long long value) {
  say(ctx, Buffer().add(label).add(" ").add(value).c_str());
}

void note_number(const char *label, long long value) {
  basalt::log_info(Buffer().add(label).add(" ").add(value).c_str());
}

basalt::Player source_player(basalt::Ctx ctx) {
  const auto source = basalt::command_source_name(ctx);
  return basalt::player_by_name(source.c_str());
}

void permissions(basalt::Ctx ctx) {
  const basalt::Player player = source_player(ctx);
  say_number(ctx, "has example.vip",
             srv::permission_has(player, "example.vip"));
  say_number(ctx, "console has example.vip",
             srv::permission_check("console", "example.vip"));
  say_number(ctx, "grant", srv::permission_grant(player, "example.vip"));
  say_number(ctx, "has example.vip",
             srv::permission_has(player, "example.vip"));
  say_number(ctx, "drop", srv::permission_drop(player, "example.vip"));
  say_number(ctx, "has example.vip",
             srv::permission_has(player, "example.vip"));
  say(ctx, srv::permission_meta("Steve", "prefix").c_str());
  say_number(ctx, "unknown subject",
             srv::permission_check("Nobody", "example.vip"));
}

void vip_provider(basalt::Ctx ctx) {
  const auto node = srv::permission_query(ctx, "node");
  const auto kind = srv::permission_query(ctx, "kind");
  if (kind.c_str()[0] == 'c') {
    srv::permission_answer(ctx, node.c_str()[8] == 'v' ? 1 : -1);
  } else {
    srv::permission_answer_meta(ctx, "VIP");
  }
}

void broadcast(basalt::Ctx ctx) {
  basalt::Args args;
  args.add("text", "hello everyone");
  say_number(ctx, "reached",
             srv::broadcast("@server-line", &args,
                            srv::kBroadcastPlayers | srv::kBroadcastConsole));
  say_number(ctx, "console only",
             srv::broadcast("plain text", nullptr, srv::kBroadcastConsole));
  say_number(ctx, "nobody", srv::broadcast("x", nullptr, 0));
  say(ctx, srv::placeholder_fill("%player_name% is here", source_player(ctx))
               .c_str());
}

void info(basalt::Ctx ctx) {
  basalt::Reply<> reply;
  say_number(ctx, "info", srv::info(reply));
  const long long online = reply.value.find("online").i64_or(-1);
  say_number(ctx, "online", online);
  const char *software = nullptr;
  int length = 0;
  reply.value.find("software").as_str(software, length);
  say(ctx, Buffer().add("software ").add(length).c_str());
  say_number(ctx, "plugins", reply.value.find("plugins").count());
  say_number(ctx, "lane", reply.value.find("plugin").find("lane").i64_or(-1));
}

void admin(basalt::Ctx ctx) {
  basalt::Reply<> reply;
  Doc key;
  key.map().key("key").string("motd").end();
  say_number(ctx, "settings.get", srv::call("settings.get", &key, reply));
  const char *motd = nullptr;
  int length = 0;
  reply.value.as_str(motd, length);
  say(ctx, Buffer().add("motd length ").add(length).c_str());
  say_number(ctx, "tick.rate", srv::call("tick.rate", nullptr, reply));
  say_number(ctx, "rate", static_cast<long long>(reply.value.f64_or(-1.0)));
  say_number(ctx, "ops", srv::call("ops", nullptr, reply));
  say_number(ctx, "op count", reply.value.count());
  say_number(ctx, "unknown op", srv::call("nope.nope", nullptr, reply));
  say_number(ctx, "datapacks", srv::call("datapacks.list", nullptr, reply));
}

void files(basalt::Ctx ctx) {
  say_number(ctx, "write", srv::file_write("notes/a.txt", "hello"));
  say_number(ctx, "exists", srv::file_exists("notes/a.txt"));
  char text[64];
  const int size = srv::file_read("notes/a.txt", text, sizeof text - 1);
  text[size > 0 ? size : 0] = 0;
  say(ctx, text);
  basalt::Reply<> listing;
  say_number(ctx, "list", srv::file_list("notes", listing));
  say_number(ctx, "entries", listing.value.count());
  basalt::Reply<> stat;
  say_number(ctx, "stat", srv::file_stat("notes/a.txt", stat));
  say_number(ctx, "size", stat.value.find("size").i64_or(-1));
  say_number(ctx, "outside", srv::file_write("secret/a.txt", "x"));
  say_number(ctx, "escape", srv::file_write("notes/../a.txt", "x"));
  char readme[32];
  say_number(ctx, "packaged",
             srv::file_read("data/readme.txt", readme, sizeof readme));
  say_number(ctx, "delete", srv::file_delete("notes/a.txt"));
  say_number(ctx, "gone", srv::file_exists("notes/a.txt"));
}

void store(basalt::Ctx ctx) {
  say_number(ctx, "set", srv::store_set("visits", 3LL));
  basalt::Reply<> reply;
  say_number(ctx, "get", srv::store_get("visits", reply));
  say_number(ctx, "visits", reply.value.i64_or(-1));
  say_number(ctx, "missing", srv::store_get("nothing", reply));
  Doc doc;
  doc.map()
      .key("kills")
      .integer(7)
      .key("tags")
      .list()
      .string("a")
      .string("b")
      .end()
      .end();
  say_number(ctx, "write", srv::data_write("stats", doc));
  say_number(ctx, "read", srv::data_read("stats", reply));
  say_number(ctx, "kills", reply.value.find("kills").i64_or(-1));
  say_number(ctx, "tags", reply.value.find("tags").count());
  say_number(ctx, "config", srv::config_value("shop", reply));
  say_number(ctx, "price", reply.value.find("sparkles-price").i64_or(-1));
  say_number(ctx, "dashed", srv::config_value("payout_period", reply));
}

void echo_service(basalt::Ctx ctx) {
  basalt::Reply<> call;
  srv::result(ctx, call);
  Doc answer;
  answer.map()
      .key("echo")
      .integer(call.value.find("args").find("n").i64_or(0) * 2)
      .end();
  srv::service_reply(ctx, answer);
}

void echo_done(basalt::Ctx ctx) {
  basalt::Reply<> reply;
  srv::result(ctx, reply);
  note_number("ok", reply.value.find("ok").bool_or(false) ? 1 : 0);
  note_number("echo", reply.value.find("value").find("echo").i64_or(-1));
}

void missing_done(basalt::Ctx ctx) {
  basalt::Reply<> reply;
  srv::result(ctx, reply);
  note_number("late ok", reply.value.find("ok").bool_or(true) ? 1 : 0);
}

void service(basalt::Ctx ctx) {
  say_number(ctx, "has", srv::service_has("example.echo"));
  say_number(ctx, "has other", srv::service_has("example.other"));
  Doc args;
  args.map().key("n").integer(21).end();
  say_number(ctx, "call",
             srv::service_call("example.echo", args, 0, echo_done) > 0 ? 1 : 0);
  say_number(ctx, "absent",
             srv::service_call("example.other", args, 0, missing_done));
}

int g_waits = 0;

void waited(basalt::Ctx) { note_number("waited", ++g_waits); }

void placed(basalt::Ctx) { note_number("placed", ++g_waits); }

void looked(basalt::Ctx ctx) {
  basalt::Reply<> reply;
  srv::result(ctx, reply);
  const char *uuid = nullptr;
  int length = 0;
  reply.value.find("uuid").as_str(uuid, length);
  note_number("profile", reply.value.is_null() ? 0 : length);
}

void later(basalt::Ctx ctx) {
  say_number(ctx, "wait", srv::wait(20, waited) > 0 ? 1 : 0);
  say_number(ctx, "at", srv::at("world", 0.0, 0.0, placed) > 0 ? 1 : 0);
  say_number(ctx, "at unknown", srv::at("elsewhere", 0.0, 0.0, placed));
  say_number(ctx, "lookup", srv::profile_lookup("Notch", looked) > 0 ? 1 : 0);
}

void dynamic(basalt::Ctx ctx) {
  basalt::Args args;
  args.add("text", "dynamic ran");
  basalt::command_reply(ctx, "@server-line", &args);
  double amount = 0.0;
  int flag = 0;
  srv::command_arg_f64(ctx, "amount", &amount);
  srv::command_arg_bool(ctx, "loud", &flag);
  say_number(ctx, "amount", static_cast<long long>(amount * 10));
  say_number(ctx, "loud", flag);
}

void dynamic_complete(basalt::Ctx ctx) {
  srv::complete_add(ctx, "alpha");
  srv::complete_add(ctx, "beta");
  const auto typed = srv::complete_input(ctx);
  if (typed.length > 0) {
    srv::complete_add(ctx, typed.c_str());
  }
}

void manage(basalt::Ctx ctx) {
  say_number(ctx, "register",
             srv::command("dyn [amount: float] [loud: bool]", "", dynamic,
                          dynamic_complete));
  say_number(ctx, "again",
             srv::command("dyn [amount: float] [loud: bool]", "", dynamic));
  say_number(ctx, "alias", srv::command_alias("dyn", "dy"));
  say_number(ctx, "alias missing", srv::command_alias("nothing", "no"));
  say_number(ctx, "run", srv::command_run("srv hello", 0));
  say_number(ctx, "run as player",
             srv::command_run("srv hello", source_player(ctx)));
}

void unmanage(basalt::Ctx ctx) {
  say_number(ctx, "unregister", srv::command_unregister("dyn"));
  say_number(ctx, "unregister again", srv::command_unregister("dyn"));
}

void hello(basalt::Ctx ctx) { say(ctx, "hello from the command"); }

} // namespace

void register_all() {
  srv::permission_provide(10, srv::kProvideCheck | srv::kProvideMeta,
                          vip_provider);
  srv::service_register("example.echo", echo_service);
  basalt::command("srv hello", "", hello);
  basalt::command("srv perms", "", permissions);
  basalt::command("srv broadcast", "", broadcast);
  basalt::command("srv info", "", info);
  basalt::command("srv admin", "", admin);
  basalt::command("srv files", "", files);
  basalt::command("srv store", "", store);
  basalt::command("srv service", "", service);
  basalt::command("srv later", "", later);
  basalt::command("srv manage", "", manage);
  basalt::command("srv unmanage", "", unmanage);
}

} // namespace server_area
