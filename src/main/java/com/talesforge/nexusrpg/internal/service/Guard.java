package com.talesforge.nexusrpg.internal.service;

import net.minecraft.world.entity.Entity;

final class Guard {
    static void server(Entity e) {
        if (e.level().isClientSide()) throw new IllegalStateException("NexusRPG: this operation is server-side only");
    }

    private Guard() {}
}
