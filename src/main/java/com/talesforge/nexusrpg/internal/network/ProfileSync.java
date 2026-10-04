package com.talesforge.nexusrpg.internal.network;

import com.talesforge.nexusrpg.internal.data.ProfileStore;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ProfileSync {
    /** To everyone who sees the entity (and to the entity itself, if it’s a player). */
    public static void broadcast(LivingEntity e) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(e, payload(e));
    }

    /** For a single player (upon login, respawn, or the start of tracking). */
    public static void sendTo(ServerPlayer to, LivingEntity target) {
        PacketDistributor.sendToPlayer(to, payload(target));
    }

    private static ProfileSyncPayload payload(LivingEntity e) {
        return new ProfileSyncPayload(e.getId(), ProfileStore.get(e));
    }

    private ProfileSync() {}
}
