package com.talesforge.nexusrpg.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** The NexusRPG server config (located in the world folder: serverconfig/nexusrpg-server.toml). */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MAX_TEAM_SIZE = BUILDER
            .comment("The maximum number of participants in one team")
            .defineInRange("maxTeamSize", 16, 1, 1024);

    public static final ModConfigSpec.BooleanValue ALLOW_MOBS_IN_TEAMS = BUILDER
            .comment("Allow mobs and NPCs to join teams.")
            .define("allowMobsInTeams", true);

    public static final ModConfigSpec.IntValue MAX_CLASSES_PER_ENTITY = BUILDER
            .comment("How many classes can a single entity have at the same time?")
            .defineInRange("maxClassesPerEntity", 3, 1, 64);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
