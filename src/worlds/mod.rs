use basalt_server::{values::Writer, worlds};
use basalt_server::core::Ctx;

fn kl(length: i32, capacity: usize) -> usize {
    if length > 0 && (length as usize) <= capacity {
        length as usize
    } else {
        0
    }
}

fn done(ctx: Ctx) {
    let mut out = [0u8; 16];
    worlds::world_value_result(ctx, &mut out);
}

fn later2(_: Ctx) {
    let mut w2: i32 = 0;
    {
        let mut d20000 = Writer::new();
        d20000.begin_map().key("name").string("tour").end();
        worlds::world_create(d20000.finish().unwrap_or(&[]));
    }
    {
        worlds::world_unload("nope", 0);
    }
    {
        worlds::world_unload("tour", 0);
    }
    {
        worlds::world_delete("tour");
    }
    let _ = w2;
}

fn later(_: Ctx) {
    let mut w2: i32 = 0;
    {
        let mut d10000 = Writer::new();
        d10000.begin_map().key("name").string("tour").end();
        w2 = worlds::world_create(d10000.finish().unwrap_or(&[]));
    }
    {
        worlds::world_at(w2, 0.5, 0.5, later2);
    }
    let _ = w2;
}

fn tour(_: Ctx) {
    worlds::chunk_coord(-17);
    worlds::block_distance(0, 0, 0, 3, 4, 0);
    let mut pos = [1, 2, 3];
    worlds::pos_offset(&mut pos, 1, 1, 1);
    let mut w: i32 = 0;
    let p: i32 = basalt_server::players::by_name("Steve");
    let mut w2: i32 = 0;
    let mut rq: i32 = 0;
    let mut nz: i32 = 0;
    let mut s1 = [0u8; 2048];
    let mut s1n: i32 = 0;
    {
        w = worlds::world_by_name("world");
    }
    {
        worlds::world_by_name("nope");
    }
    {
        worlds::world_default();
    }
    {
        let mut o30 = [0i32; 8];
        worlds::world_list(&mut o30);
    }
    {
        let mut o41 = [0u8; 128];
        worlds::world_name(w, &mut o41);
    }
    {
        let mut o51 = [0u8; 128];
        worlds::world_name(999, &mut o51);
    }
    {
        let mut o62 = [0u8; 128];
        worlds::world_get(w, "time", &mut o62);
    }
    {
        let mut o72 = [0u8; 128];
        worlds::world_get(w, "nope", &mut o72);
    }
    {
        let mut d82 = Writer::new();
        d82.int(1000);
        worlds::world_set(w, "day_time", d82.finish().unwrap_or(&[]));
    }
    {
        let mut d92 = Writer::new();
        d92.int(1);
        worlds::world_set(w, "nope", d92.finish().unwrap_or(&[]));
    }
    {
        let mut o102 = [0u8; 128];
        worlds::world_rule_get(w, "doDaylightCycle", &mut o102);
    }
    {
        let mut o112 = [0u8; 128];
        worlds::world_rule_get(w, "nope", &mut o112);
    }
    {
        worlds::world_rule_set(w, "doDaylightCycle", "false");
    }
    {
        rq = worlds::world_at(w, 1.5, 1.5, done);
    }
    {
        worlds::world_request_cancel(rq);
    }
    {
        worlds::world_at(w, 2.5, 2.5, done);
    }
    {
        worlds::world_block_set(w, 0, 64, 0, "minecraft:stone", 0);
    }
    {
        worlds::world_block_set(w, 0, 64, 0, "nope:block", 0);
    }
    {
        let mut o185 = [0u8; 128];
        worlds::world_block_get(w, 0, 64, 0, 0, &mut o185);
    }
    {
        worlds::world_state_get(w, 0, 64, 0, 0);
    }
    {
        worlds::world_state_set(w, 1, 64, 0, 3, 0);
    }
    {
        worlds::world_state_get(999, 0, 64, 0, 0);
    }
    {
        let mut o222 = [0i32; 2];
        worlds::world_states_read(w, &[0i32, 64i32, 0i32, 1i32, 64i32, 0i32], &mut o222, 0);
    }
    {
        worlds::world_states_write(w, &[2i32, 64i32, 0i32, 3i32, 64i32, 0i32], &[5i32, 5i32], 0);
    }
    {
        worlds::world_fill(w, 2, 64, 2, 5, 65, 5, 5, 0);
    }
    {
        worlds::world_fill(999, 0, 0, 0, 1, 1, 1, 1, 0);
    }
    {
        let mut o265 = [0i32; 4096];
        worlds::world_section_read(w, 0, 4, 0, 0, &mut o265);
    }
    {
        worlds::world_section_write(w, 0, 4, 0, &[0i32; 4096], 0);
    }
    {
        worlds::world_section_write(w, 0, 200, 0, &[0i32; 4096], 0);
    }
    {
        let mut o294 = [0i32; 8];
        worlds::world_chunk_read(w, 0, 0, 0, &mut o294);
    }
    {
        let mut o306 = [0i32; 4096];
        worlds::world_chunk_apply(w, 0, 0, &[4i32], &[0i32; 4096], 0, &mut o306);
    }
    {
        worlds::world_changes_apply(w, &[0i32, 64i32, 0i32], &[5i32], 0);
    }
    {
        worlds::world_blocks_settle(w, &[0i32, 64i32, 0i32], &[0i32], 0);
    }
    {
        worlds::world_blocks_notify(w, &[0i32, 64i32, 0i32], &[1i32, 64i32, 0i32]);
    }
    {
        worlds::world_blocks_reshape(w, &[0i32, 64i32, 0i32], 0);
    }
    {
        worlds::world_state_valid(w, 0, 64, 0, 1);
    }
    {
        worlds::world_tick_schedule(w, 0, 64, 0, 5, 0);
    }
    {
        worlds::world_tick_pending(w, 0, 64, 0);
    }
    {
        worlds::world_state_set(w, 3, 64, 0, 6, 0);
    }
    {
        worlds::world_block_entity_edit(w, 1, 3, 64, 0, &[10u8, 0u8, 0u8, 0u8]);
    }
    {
        let mut o404 = [0u8; 256];
        worlds::world_block_entity_get(w, 3, 64, 0, &mut o404);
    }
    {
        let mut o414 = [0u8; 256];
        worlds::world_block_entity_get(w, 9, 64, 9, &mut o414);
    }
    {
        let mut o423 = [0i32; 8];
        worlds::world_block_entities(w, 0, 0, &mut o423);
    }
    {
        worlds::world_data_set(w, 0, 0, 0, 0, &[10u8, 0u8, 0u8, 0u8]);
    }
    {
        let mut o445 = [0u8; 256];
        worlds::world_data_get(w, 0, 0, 0, 0, &mut o445);
    }
    {
        let mut o455 = [0u8; 256];
        worlds::world_data_get(w, 1, 9, 0, 9, &mut o455);
    }
    {
        worlds::world_chunk_status(w, 0, 0);
    }
    {
        worlds::world_chunk_status(w, 9, 9);
    }
    {
        worlds::world_chunk_load(w, 3, 3, 8);
    }
    {
        worlds::world_chunk_unload(w, 3, 3);
    }
    {
        worlds::world_chunk_request(w, 4, 4, 8, 0, done);
    }
    {
        let mut o511 = [0i32; 16];
        worlds::world_chunks_loaded(w, &mut o511);
    }
    {
        worlds::world_chunk_hold(w, "tour", &[4i32, 4i32]);
    }
    {
        let mut o532 = [0i32; 8];
        worlds::world_chunk_held(w, "tour", &mut o532);
    }
    {
        worlds::world_chunk_release(w, "tour", &[4i32, 4i32]);
    }
    {
        worlds::world_chunks_resend(w, &[0i32, 0i32]);
    }
    {
        worlds::world_stored_put(w, 7, 7, &[10u8, 0u8, 0u8, 0u8]);
    }
    {
        let mut o573 = [0u8; 256];
        worlds::world_stored_get(w, 7, 7, &mut o573);
    }
    {
        let mut o583 = [0u8; 256];
        worlds::world_stored_get(w, 8, 8, &mut o583);
    }
    {
        let mut o593 = [0i32; 8];
        worlds::world_stored_list(w, 0, 0, &mut o593);
    }
    {
        worlds::world_light_at(w, 0, 64, 0, 0);
    }
    {
        let mut o616 = [0u8; 2048];
        worlds::world_light_read(w, 0, 4, 0, 1, 1, &mut o616);
    }
    {
        worlds::world_light_write(w, 0, 4, 0, 1, &[0u8; 2048]);
    }
    {
        worlds::world_light_write(w, 0, 4, 0, 1, &[10u8, 0u8, 0u8, 0u8]);
    }
    {
        worlds::world_light_check(w, &[0i32, 0i32], done);
    }
    {
        let mut o654 = [0u8; 128];
        worlds::world_biome_get(w, 0, 64, 0, &mut o654);
    }
    {
        worlds::world_biome_set(w, 0, 64, 0, "minecraft:desert");
    }
    {
        worlds::world_biome_set(w, 0, 64, 0, "nope:biome");
    }
    {
        let mut o683 = [0i32; 8];
        worlds::world_biomes_read(w, 0, 0, &mut o683);
    }
    {
        worlds::world_biomes_write(w, 0, 0, &[1i32, 1i32, 1i32, 1i32]);
    }
    {
        worlds::world_sound(w, "minecraft:block.note_block.bell", "block", 0.5, 64.0, 0.5, 1.0, 1.0, 0);
    }
    {
        worlds::world_sound(w, "", "block", 0.5, 64.0, 0.5, 1.0, 1.0, 0);
    }
    {
        let mut d721 = Writer::new();
        d721.begin_map().key("type").string("minecraft:flame").key("x").float(0.5).key("y").float(65.0).key("z").float(0.5).key("count").int(3).end();
        worlds::world_particle(w, d721.finish().unwrap_or(&[]));
    }
    {
        let mut d731 = Writer::new();
        d731.begin_map().key("count").int(1).end();
        worlds::world_particle(w, d731.finish().unwrap_or(&[]));
    }
    {
        worlds::world_level_event(w, 1000, 0, 64, 0, 0, 0);
    }
    {
        worlds::world_block_event(w, 0, 64, 0, 1, 2);
    }
    {
        worlds::world_explode(w, 0.5, 64.0, 0.5, 0.0, 0, 0);
    }
    {
        worlds::world_lightning(w, 0.5, 64.0, 0.5);
    }
    {
        worlds::world_game_event(w, "minecraft:block_change", 0.5, 64.0, 0.5, "");
    }
    {
        worlds::world_game_event(w, "", 0.5, 64.0, 0.5, "");
    }
    {
        let mut d802 = Writer::new();
        d802.begin_list().string("minecraft:block.note_block.bell").string("block").float(0.5).float(64.0).float(0.5).float(1.0).float(1.0).int(0).end();
        let mut o803 = [0u8; 64];
        worlds::world_player_effect(p, "sound_at", d802.finish().unwrap_or(&[]), &mut o803);
    }
    {
        let mut d812 = Writer::new();
        d812.begin_list().end();
        let mut o813 = [0u8; 64];
        worlds::world_player_effect(p, "nope", d812.finish().unwrap_or(&[]), &mut o813);
    }
    {
        let mut o821 = [0u8; 512];
        worlds::world_border_get(w, &mut o821);
    }
    {
        let mut d831 = Writer::new();
        d831.begin_map().key("size").float(100.0).end();
        worlds::world_border_set(w, d831.finish().unwrap_or(&[]));
    }
    {
        let mut d841 = Writer::new();
        d841.begin_map().key("size").float(-1.0).key("seconds").float(-1.0).end();
        worlds::world_border_set(w, d841.finish().unwrap_or(&[]));
    }
    {
        let mut d852 = Writer::new();
        d852.begin_map().end();
        let mut o853 = [0u8; 1024];
        worlds::world_call(w, "ops", d852.finish().unwrap_or(&[]), &mut o853);
    }
    {
        let mut d862 = Writer::new();
        d862.begin_map().key("x").int(2).key("z").int(2).end();
        let mut o863 = [0u8; 256];
        worlds::world_call(w, "highest", d862.finish().unwrap_or(&[]), &mut o863);
    }
    {
        let mut d872 = Writer::new();
        d872.begin_map().end();
        let mut o873 = [0u8; 256];
        worlds::world_call(w, "nope", d872.finish().unwrap_or(&[]), &mut o873);
    }
    {
        s1n = worlds::structure_capture(w, 0, 64, 0, 3, 2, 3, 0, &mut s1);
    }
    {
        worlds::structure_put("tour:a", &s1[..kl(s1n, 2048)], 0);
    }
    {
        worlds::structure_put("tour:b", &[10u8, 0u8, 0u8, 0u8], 0);
    }
    {
        let mut o910 = [0u8; 256];
        worlds::structure_list(&mut o910);
    }
    {
        let mut o921 = [0u8; 256];
        worlds::structure_get("tour:a", &mut o921);
    }
    {
        let mut o931 = [0u8; 256];
        worlds::structure_get("tour:zzz", &mut o931);
    }
    {
        let mut o941 = [0u8; 128];
        worlds::structure_file("tour:a", &mut o941);
    }
    {
        let mut d951 = Writer::new();
        d951.begin_map().key("x").int(0).key("y").int(64).key("z").int(0).end();
        let mut o952 = [0u8; 1024];
        worlds::structure_scan(&s1[..kl(s1n, 2048)], d951.finish().unwrap_or(&[]), &mut o952);
    }
    {
        let mut d961 = Writer::new();
        d961.begin_map().key("world").string("world").key("x").int(8).key("y").int(64).key("z").int(8).end();
        worlds::structure_place(&s1[..kl(s1n, 2048)], d961.finish().unwrap_or(&[]));
    }
    {
        worlds::structure_remove("tour:a", 0);
    }
    {
        worlds::structure_remove("tour:a", 0);
    }
    {
        let mut d990 = Writer::new();
        d990.begin_map().key("op").string("shape").key("settings").string("minecraft:overworld").key("seed").int(0).end();
        let mut o991 = [0u8; 256];
        worlds::noise_query(d990.finish().unwrap_or(&[]), &mut o991);
    }
    {
        let mut d1000 = Writer::new();
        d1000.begin_map().key("op").string("nope").end();
        let mut o1001 = [0u8; 256];
        worlds::noise_query(d1000.finish().unwrap_or(&[]), &mut o1001);
    }
    {
        let mut d1010 = Writer::new();
        d1010.begin_map().key("id").string("minecraft:temperature").key("seed").int(0).end();
        nz = worlds::noise_create(d1010.finish().unwrap_or(&[]));
    }
    {
        let mut f1024 = 0.0f64;
        worlds::noise_sample(nz, 0.0, 0.0, 0.0, &mut f1024);
    }
    {
        let mut o1031 = [0u8; 128];
        worlds::noise_info(nz, &mut o1031);
    }
    {
        worlds::noise_release(nz);
    }
    {
        worlds::noise_release(nz);
    }
    {
        worlds::block_state_id("minecraft:stone");
    }
    {
        worlds::block_state_id("nope:block");
    }
    {
        let mut o1081 = [0u8; 128];
        worlds::block_state_text(1, &mut o1081);
    }
    {
        let mut d1091 = Writer::new();
        d1091.begin_map().end();
        let mut o1092 = [0u8; 1024];
        worlds::worlds_data("tables", d1091.finish().unwrap_or(&[]), &mut o1092);
    }
    {
        let mut d1101 = Writer::new();
        d1101.begin_map().end();
        let mut o1102 = [0u8; 512];
        worlds::worlds_data("blocks", d1101.finish().unwrap_or(&[]), &mut o1102);
    }
    {
        let mut d1111 = Writer::new();
        d1111.begin_map().end();
        let mut o1112 = [0u8; 64];
        worlds::worlds_data("nope", d1111.finish().unwrap_or(&[]), &mut o1112);
    }
    {
        let mut d1121 = Writer::new();
        d1121.begin_map().end();
        let mut o1122 = [0u8; 128];
        worlds::datafix_call("types", d1121.finish().unwrap_or(&[]), &mut o1122);
    }
    {
        let mut d1131 = Writer::new();
        d1131.begin_map().key("type").string("chunk").end();
        let mut o1132 = [0u8; 64];
        worlds::datafix_call("supports", d1131.finish().unwrap_or(&[]), &mut o1132);
    }
    {
        let mut d1141 = Writer::new();
        d1141.begin_map().key("type").string("chunk").end();
        let mut o1142 = [0u8; 256];
        worlds::datafix_call("steps", d1141.finish().unwrap_or(&[]), &mut o1142);
    }
    {
        let mut d1151 = Writer::new();
        d1151.begin_map().key("type").string("chunk").key("from").int(100).key("to").int(200).end();
        let mut o1152 = [0u8; 64];
        worlds::datafix_call("missing", d1151.finish().unwrap_or(&[]), &mut o1152);
    }
    {
        let mut d1161 = Writer::new();
        d1161.begin_map().key("id").int(16).end();
        let mut o1162 = [0u8; 128];
        worlds::datafix_call("legacy_state", d1161.finish().unwrap_or(&[]), &mut o1162);
    }
    {
        let mut d1171 = Writer::new();
        d1171.begin_map().key("id").int(16).end();
        let mut o1172 = [0u8; 128];
        worlds::datafix_call("modern_state", d1171.finish().unwrap_or(&[]), &mut o1172);
    }
    {
        let mut d1181 = Writer::new();
        d1181.begin_map().key("type").string("chunk").key("nbt").bytes(&[10u8, 0u8, 0u8, 0u8]).key("from").int(100).key("to").int(200).end();
        let mut o1182 = [0u8; 128];
        worlds::datafix_call("upgrade", d1181.finish().unwrap_or(&[]), &mut o1182);
    }
    {
        let mut d1191 = Writer::new();
        d1191.begin_map().end();
        let mut o1192 = [0u8; 64];
        worlds::datafix_call("nope", d1191.finish().unwrap_or(&[]), &mut o1192);
    }
    {
        worlds::world_at(w, 3.5, 3.5, later);
    }
    let _ = (w, p, w2, rq, nz);
}

pub fn register() {
    basalt_server::commands::register("worlds", "", tour);
}
