package com.talesforge.nexusrpg.internal.network;

import com.talesforge.nexusrpg.internal.data.ProfileStore;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientProfileHandler {
    public static void handle(ProfileSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Entity e = ctx.player().level().getEntity(payload.entityId());
            if (e instanceof LivingEntity living) ProfileStore.setQuiet(living, payload.profile());
        });
    }

    private ClientProfileHandler() {}
}
