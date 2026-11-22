#include "worlds.hpp"

#include "text.hpp"

namespace worlds {

namespace {

int32_t kl(int32_t length, int32_t capacity) { return length > 0 && length <= capacity ? length : 0; }

void done(basalt::Ctx ctx) {
    char out[16];
    basalt::worlds::world_value_result(ctx, out, 16);
}

void later2(basalt::Ctx) {
    int32_t w2 = 0;
    {
        basalt::ValueBuffer<512> d20000;
        d20000.map().key("name").string("tour").end();
        basalt::worlds::world_create(d20000.data(), d20000.finish());
    }
    {
        basalt::worlds::world_unload("nope", 0);
    }
    {
        basalt::worlds::world_unload("tour", 0);
    }
    {
        basalt::worlds::world_delete("tour");
    }
    static_cast<void>(w2);
}

void later(basalt::Ctx) {
    int32_t w2 = 0;
    {
        basalt::ValueBuffer<512> d10000;
        d10000.map().key("name").string("tour").end();
        w2 = basalt::worlds::world_create(d10000.data(), d10000.finish());
    }
    {
        basalt::worlds::world_at(w2, 0.5, 0.5, later2);
    }
    static_cast<void>(w2);
}

void tour(basalt::Ctx) {
    basalt::worlds::chunk_coord(-17);
    basalt::worlds::block_distance(0, 0, 0, 3, 4, 0);
    int32_t pos[3] = {1, 2, 3};
    basalt::worlds::pos_offset(pos, 1, 1, 1);
    int32_t w = 0, p = basalt::player_by_name("Steve"), w2 = 0, rq = 0, nz = 0;
    char s1[2048] = {};
    int32_t s1n = 0;
    {
        w = basalt::worlds::world_by_name("world");
    }
    {
        basalt::worlds::world_by_name("nope");
    }
    {
        basalt::worlds::world_default();
    }
    {
        int32_t o30[8] = {};
        basalt::worlds::world_list(o30, 8);
    }
    {
        char o41[128];
        basalt::worlds::world_name(w, o41, 128);
    }
    {
        char o51[128];
        basalt::worlds::world_name(999, o51, 128);
    }
    {
        char o62[128];
        basalt::worlds::world_get(w, "time", o62, 128);
    }
    {
        char o72[128];
        basalt::worlds::world_get(w, "nope", o72, 128);
    }
    {
        basalt::ValueBuffer<512> d82;
        d82.integer(1000);
        basalt::worlds::world_set(w, "day_time", d82.data(), d82.finish());
    }
    {
        basalt::ValueBuffer<512> d92;
        d92.integer(1);
        basalt::worlds::world_set(w, "nope", d92.data(), d92.finish());
    }
    {
        char o102[128];
        basalt::worlds::world_rule_get(w, "doDaylightCycle", o102, 128);
    }
    {
        char o112[128];
        basalt::worlds::world_rule_get(w, "nope", o112, 128);
    }
    {
        basalt::worlds::world_rule_set(w, "doDaylightCycle", "false");
    }
    {
        rq = basalt::worlds::world_at(w, 1.5, 1.5, done);
    }
    {
        basalt::worlds::world_request_cancel(rq);
    }
    {
        basalt::worlds::world_at(w, 2.5, 2.5, done);
    }
    {
        basalt::worlds::world_block_set(w, 0, 64, 0, "minecraft:stone", 0);
    }
    {
        basalt::worlds::world_block_set(w, 0, 64, 0, "nope:block", 0);
    }
    {
        char o185[128];
        basalt::worlds::world_block_get(w, 0, 64, 0, 0, o185, 128);
    }
    {
        basalt::worlds::world_state_get(w, 0, 64, 0, 0);
    }
    {
        basalt::worlds::world_state_set(w, 1, 64, 0, 3, 0);
    }
    {
        basalt::worlds::world_state_get(999, 0, 64, 0, 0);
    }
    {
        static const int32_t i221[] = {0, 64, 0, 1, 64, 0};
        int32_t o223[2] = {};
        basalt::worlds::world_states_read(w, i221, 2, o223, 0);
    }
    {
        static const int32_t i231[] = {2, 64, 0, 3, 64, 0};
        static const int32_t i232[] = {5, 5};
        basalt::worlds::world_states_write(w, i231, i232, 2, 0);
    }
    {
        basalt::worlds::world_fill(w, 2, 64, 2, 5, 65, 5, 5, 0);
    }
    {
        basalt::worlds::world_fill(999, 0, 0, 0, 1, 1, 1, 1, 0);
    }
    {
        int32_t o265[4096] = {};
        basalt::worlds::world_section_read(w, 0, 4, 0, 0, o265);
    }
    {
        int32_t i274[4096] = {};
        basalt::worlds::world_section_write(w, 0, 4, 0, i274, 0);
    }
    {
        int32_t i284[4096] = {};
        basalt::worlds::world_section_write(w, 0, 200, 0, i284, 0);
    }
    {
        int32_t o294[8] = {};
        basalt::worlds::world_chunk_read(w, 0, 0, 0, o294, 8);
    }
    {
        static const int32_t i303[] = {4};
        int32_t i305[4096] = {};
        int32_t o307[4096] = {};
        basalt::worlds::world_chunk_apply(w, 0, 0, i303, 1, i305, 0, o307);
    }
    {
        static const int32_t i311[] = {0, 64, 0};
        static const int32_t i312[] = {5};
        basalt::worlds::world_changes_apply(w, i311, i312, 1, 0);
    }
    {
        static const int32_t i321[] = {0, 64, 0};
        static const int32_t i322[] = {0};
        basalt::worlds::world_blocks_settle(w, i321, i322, 1, 0);
    }
    {
        static const int32_t i331[] = {0, 64, 0};
        static const int32_t i332[] = {1, 64, 0};
        basalt::worlds::world_blocks_notify(w, i331, i332, 1);
    }
    {
        static const int32_t i341[] = {0, 64, 0};
        basalt::worlds::world_blocks_reshape(w, i341, 1, 0);
    }
    {
        basalt::worlds::world_state_valid(w, 0, 64, 0, 1);
    }
    {
        basalt::worlds::world_tick_schedule(w, 0, 64, 0, 5, 0);
    }
    {
        basalt::worlds::world_tick_pending(w, 0, 64, 0);
    }
    {
        basalt::worlds::world_state_set(w, 3, 64, 0, 6, 0);
    }
    {
        static const char b395[] = {10, 0, 0, 0};
        basalt::worlds::world_block_entity_edit(w, 1, 3, 64, 0, b395, 4);
    }
    {
        char o404[256];
        basalt::worlds::world_block_entity_get(w, 3, 64, 0, o404, 256);
    }
    {
        char o414[256];
        basalt::worlds::world_block_entity_get(w, 9, 64, 9, o414, 256);
    }
    {
        int32_t o423[8] = {};
        basalt::worlds::world_block_entities(w, 0, 0, o423, 8);
    }
    {
        static const char b435[] = {10, 0, 0, 0};
        basalt::worlds::world_data_set(w, 0, 0, 0, 0, b435, 4);
    }
    {
        char o445[256];
        basalt::worlds::world_data_get(w, 0, 0, 0, 0, o445, 256);
    }
    {
        char o455[256];
        basalt::worlds::world_data_get(w, 1, 9, 0, 9, o455, 256);
    }
    {
        basalt::worlds::world_chunk_status(w, 0, 0);
    }
    {
        basalt::worlds::world_chunk_status(w, 9, 9);
    }
    {
        basalt::worlds::world_chunk_load(w, 3, 3, 8);
    }
    {
        basalt::worlds::world_chunk_unload(w, 3, 3);
    }
    {
        basalt::worlds::world_chunk_request(w, 4, 4, 8, 0, done);
    }
    {
        int32_t o511[16] = {};
        basalt::worlds::world_chunks_loaded(w, o511, 16);
    }
    {
        static const int32_t i522[] = {4, 4};
        basalt::worlds::world_chunk_hold(w, "tour", i522, 1);
    }
    {
        int32_t o532[8] = {};
        basalt::worlds::world_chunk_held(w, "tour", o532, 8);
    }
    {
        static const int32_t i542[] = {4, 4};
        basalt::worlds::world_chunk_release(w, "tour", i542, 1);
    }
    {
        static const int32_t i551[] = {0, 0};
        basalt::worlds::world_chunks_resend(w, i551, 1);
    }
    {
        static const char b563[] = {10, 0, 0, 0};
        basalt::worlds::world_stored_put(w, 7, 7, b563, 4);
    }
    {
        char o573[256];
        basalt::worlds::world_stored_get(w, 7, 7, o573, 256);
    }
    {
        char o583[256];
        basalt::worlds::world_stored_get(w, 8, 8, o583, 256);
    }
    {
        int32_t o593[8] = {};
        basalt::worlds::world_stored_list(w, 0, 0, o593, 8);
    }
    {
        basalt::worlds::world_light_at(w, 0, 64, 0, 0);
    }
    {
        char o616[2048];
        basalt::worlds::world_light_read(w, 0, 4, 0, 1, 1, o616, 2048);
    }
    {
        char z625[2048] = {};
        basalt::worlds::world_light_write(w, 0, 4, 0, 1, z625, 2048);
    }
    {
        static const char b635[] = {10, 0, 0, 0};
        basalt::worlds::world_light_write(w, 0, 4, 0, 1, b635, 4);
    }
    {
        static const int32_t i641[] = {0, 0};
        basalt::worlds::world_light_check(w, i641, 1, done);
    }
    {
        char o654[128];
        basalt::worlds::world_biome_get(w, 0, 64, 0, o654, 128);
    }
    {
        basalt::worlds::world_biome_set(w, 0, 64, 0, "minecraft:desert");
    }
    {
        basalt::worlds::world_biome_set(w, 0, 64, 0, "nope:biome");
    }
    {
        int32_t o683[8] = {};
        basalt::worlds::world_biomes_read(w, 0, 0, o683, 8);
    }
    {
        static const int32_t i693[] = {1, 1, 1, 1};
        basalt::worlds::world_biomes_write(w, 0, 0, i693, 4);
    }
    {
        basalt::worlds::world_sound(w, "minecraft:block.note_block.bell", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0LL);
    }
    {
        basalt::worlds::world_sound(w, "", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0LL);
    }
    {
        basalt::ValueBuffer<512> d721;
        d721.map().key("type").string("minecraft:flame").key("x").number(0.5).key("y").number(65.0).key("z").number(0.5).key("count").integer(3).end();
        basalt::worlds::world_particle(w, d721.data(), d721.finish());
    }
    {
        basalt::ValueBuffer<512> d731;
        d731.map().key("count").integer(1).end();
        basalt::worlds::world_particle(w, d731.data(), d731.finish());
    }
    {
        basalt::worlds::world_level_event(w, 1000, 0, 64, 0, 0, 0);
    }
    {
        basalt::worlds::world_block_event(w, 0, 64, 0, 1, 2);
    }
    {
        basalt::worlds::world_explode(w, 0.5, 64.0, 0.5, 0.0f, 0, 0);
    }
    {
        basalt::worlds::world_lightning(w, 0.5, 64.0, 0.5);
    }
    {
        basalt::worlds::world_game_event(w, "minecraft:block_change", 0.5, 64.0, 0.5, "");
    }
    {
        basalt::worlds::world_game_event(w, "", 0.5, 64.0, 0.5, "");
    }
    {
        basalt::ValueBuffer<512> d802;
        d802.list().string("minecraft:block.note_block.bell").string("block").number(0.5).number(64.0).number(0.5).number(1.0).number(1.0).integer(0).end();
        char o803[64];
        basalt::worlds::world_player_effect(p, "sound_at", d802.data(), d802.finish(), o803, 64);
    }
    {
        basalt::ValueBuffer<512> d812;
        d812.list().end();
        char o813[64];
        basalt::worlds::world_player_effect(p, "nope", d812.data(), d812.finish(), o813, 64);
    }
    {
        char o821[512];
        basalt::worlds::world_border_get(w, o821, 512);
    }
    {
        basalt::ValueBuffer<512> d831;
        d831.map().key("size").number(100.0).end();
        basalt::worlds::world_border_set(w, d831.data(), d831.finish());
    }
    {
        basalt::ValueBuffer<512> d841;
        d841.map().key("size").number(-1.0).key("seconds").number(-1.0).end();
        basalt::worlds::world_border_set(w, d841.data(), d841.finish());
    }
    {
        basalt::ValueBuffer<512> d852;
        d852.map().end();
        char o853[1024];
        basalt::worlds::world_call(w, "ops", d852.data(), d852.finish(), o853, 1024);
    }
    {
        basalt::ValueBuffer<512> d862;
        d862.map().key("x").integer(2).key("z").integer(2).end();
        char o863[256];
        basalt::worlds::world_call(w, "highest", d862.data(), d862.finish(), o863, 256);
    }
    {
        basalt::ValueBuffer<512> d872;
        d872.map().end();
        char o873[256];
        basalt::worlds::world_call(w, "nope", d872.data(), d872.finish(), o873, 256);
    }
    {
        s1n = basalt::worlds::structure_capture(w, 0, 64, 0, 3, 2, 3, 0, s1, 2048);
    }
    {
        basalt::worlds::structure_put("tour:a", s1, kl(s1n, 2048), 0);
    }
    {
        static const char b901[] = {10, 0, 0, 0};
        basalt::worlds::structure_put("tour:b", b901, 4, 0);
    }
    {
        char o910[256];
        basalt::worlds::structure_list(o910, 256);
    }
    {
        char o921[256];
        basalt::worlds::structure_get("tour:a", o921, 256);
    }
    {
        char o931[256];
        basalt::worlds::structure_get("tour:zzz", o931, 256);
    }
    {
        char o941[128];
        basalt::worlds::structure_file("tour:a", o941, 128);
    }
    {
        basalt::ValueBuffer<512> d951;
        d951.map().key("x").integer(0).key("y").integer(64).key("z").integer(0).end();
        char o952[1024];
        basalt::worlds::structure_scan(s1, kl(s1n, 2048), d951.data(), d951.finish(), o952, 1024);
    }
    {
        basalt::ValueBuffer<512> d961;
        d961.map().key("world").string("world").key("x").integer(8).key("y").integer(64).key("z").integer(8).end();
        basalt::worlds::structure_place(s1, kl(s1n, 2048), d961.data(), d961.finish());
    }
    {
        basalt::worlds::structure_remove("tour:a", 0);
    }
    {
        basalt::worlds::structure_remove("tour:a", 0);
    }
    {
        basalt::ValueBuffer<512> d990;
        d990.map().key("op").string("shape").key("settings").string("minecraft:overworld").key("seed").integer(0).end();
        char o991[256];
        basalt::worlds::noise_query(d990.data(), d990.finish(), o991, 256);
    }
    {
        basalt::ValueBuffer<512> d1000;
        d1000.map().key("op").string("nope").end();
        char o1001[256];
        basalt::worlds::noise_query(d1000.data(), d1000.finish(), o1001, 256);
    }
    {
        basalt::ValueBuffer<512> d1010;
        d1010.map().key("id").string("minecraft:temperature").key("seed").integer(0).end();
        nz = basalt::worlds::noise_create(d1010.data(), d1010.finish());
    }
    {
        double f1024 = 0;
        basalt::worlds::noise_sample(nz, 0.0, 0.0, 0.0, &f1024);
    }
    {
        char o1031[128];
        basalt::worlds::noise_info(nz, o1031, 128);
    }
    {
        basalt::worlds::noise_release(nz);
    }
    {
        basalt::worlds::noise_release(nz);
    }
    {
        basalt::worlds::block_state_id("minecraft:stone");
    }
    {
        basalt::worlds::block_state_id("nope:block");
    }
    {
        char o1081[128];
        basalt::worlds::block_state_text(1, o1081, 128);
    }
    {
        basalt::ValueBuffer<512> d1091;
        d1091.map().end();
        char o1092[1024];
        basalt::worlds::worlds_data("tables", d1091.data(), d1091.finish(), o1092, 1024);
    }
    {
        basalt::ValueBuffer<512> d1101;
        d1101.map().end();
        char o1102[512];
        basalt::worlds::worlds_data("blocks", d1101.data(), d1101.finish(), o1102, 512);
    }
    {
        basalt::ValueBuffer<512> d1111;
        d1111.map().end();
        char o1112[64];
        basalt::worlds::worlds_data("nope", d1111.data(), d1111.finish(), o1112, 64);
    }
    {
        basalt::ValueBuffer<512> d1121;
        d1121.map().end();
        char o1122[128];
        basalt::worlds::datafix_call("types", d1121.data(), d1121.finish(), o1122, 128);
    }
    {
        basalt::ValueBuffer<512> d1131;
        d1131.map().key("type").string("chunk").end();
        char o1132[64];
        basalt::worlds::datafix_call("supports", d1131.data(), d1131.finish(), o1132, 64);
    }
    {
        basalt::ValueBuffer<512> d1141;
        d1141.map().key("type").string("chunk").end();
        char o1142[256];
        basalt::worlds::datafix_call("steps", d1141.data(), d1141.finish(), o1142, 256);
    }
    {
        basalt::ValueBuffer<512> d1151;
        d1151.map().key("type").string("chunk").key("from").integer(100).key("to").integer(200).end();
        char o1152[64];
        basalt::worlds::datafix_call("missing", d1151.data(), d1151.finish(), o1152, 64);
    }
    {
        basalt::ValueBuffer<512> d1161;
        d1161.map().key("id").integer(16).end();
        char o1162[128];
        basalt::worlds::datafix_call("legacy_state", d1161.data(), d1161.finish(), o1162, 128);
    }
    {
        basalt::ValueBuffer<512> d1171;
        d1171.map().key("id").integer(16).end();
        char o1172[128];
        basalt::worlds::datafix_call("modern_state", d1171.data(), d1171.finish(), o1172, 128);
    }
    {
        basalt::ValueBuffer<512> d1181;
        static const char blob1181[] = {10, 0, 0, 0};
        d1181.map().key("type").string("chunk").key("nbt").bytes(blob1181, 4).key("from").integer(100).key("to").integer(200).end();
        char o1182[128];
        basalt::worlds::datafix_call("upgrade", d1181.data(), d1181.finish(), o1182, 128);
    }
    {
        basalt::ValueBuffer<512> d1191;
        d1191.map().end();
        char o1192[64];
        basalt::worlds::datafix_call("nope", d1191.data(), d1191.finish(), o1192, 64);
    }
    {
        basalt::worlds::world_at(w, 3.5, 3.5, later);
    }
    static_cast<void>(w2);
    static_cast<void>(rq);
    static_cast<void>(nz);
}

} // namespace

void register_all() { basalt::command("worlds", "", tour); }

} // namespace worlds
