package com.talesforge.nexusrpg.internal;

import com.talesforge.nexusrpg.NexusRPG;
import com.talesforge.nexusrpg.api.NexusRPGApi;
import com.talesforge.nexusrpg.internal.command.NexusRPGCommand;
import com.talesforge.nexusrpg.internal.data.RpgAttachments;
import com.talesforge.nexusrpg.internal.data.ProfileStore;
import com.talesforge.nexusrpg.internal.data.TeamManager;
import com.talesforge.nexusrpg.internal.network.ProfileSync;
import com.talesforge.nexusrpg.internal.service.BuffServiceImpl;
import com.talesforge.nexusrpg.internal.service.TeamServiceImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Events of the game bus. */
@EventBusSubscriber(modid = NexusRPG.MOD_ID)
public final class RpgGameEvents {

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        NexusRPGCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity le
                && !le.level().isClientSide()
                && le.hasData(RpgAttachments.PROFILE)) {
            BuffServiceImpl.tickEntity(le);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        TeamServiceImpl.tickTeamBuffs(event.getServer());
    }

    /**
     * The mob is dead — it leaves the team. Players remain in the team after death.
     * TODO: the final removal/despawn of the mob (not unloading the chunk!) should also remove it from the team.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity e = event.getEntity();
        if (!(e instanceof Player) && !e.level().isClientSide() && e.hasData(RpgAttachments.PROFILE)) {
            NexusRPGApi.teams().leave(e);
        }
    }

    // ===== synchronization =====
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        // The team could have been disbanded while the player was offline.
        ProfileStore.get(sp).teamId().ifPresent(id -> {
            boolean valid = TeamManager.get(sp.server).get(id).map(t -> t.members().contains(sp.getUUID())).orElse(false);
            if (!valid) ProfileStore.update(sp, p -> p.withTeam(null));
        });
        ProfileSync.sendTo(sp, sp);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) ProfileSync.sendTo(sp, sp);
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) ProfileSync.sendTo(sp, sp);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer viewer
                && event.getTarget() instanceof LivingEntity target
                && target.hasData(RpgAttachments.PROFILE)) {
            ProfileSync.sendTo(viewer, target);
        }
    }

    private RpgGameEvents() {}
}
