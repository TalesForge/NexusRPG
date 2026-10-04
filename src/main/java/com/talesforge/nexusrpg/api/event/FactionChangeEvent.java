package com.talesforge.nexusrpg.api.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/** Faction change (cancellable). NeoForge.EVENT_BUS, server only. */
public class FactionChangeEvent extends Event implements ICancellableEvent {
    private final LivingEntity entity;
    private final @Nullable ResourceLocation oldFaction;
    private final @Nullable ResourceLocation newFaction;

    public FactionChangeEvent(LivingEntity entity, @Nullable ResourceLocation oldFaction, @Nullable ResourceLocation newFaction) {
        this.entity = entity;
        this.oldFaction = oldFaction;
        this.newFaction = newFaction;
    }

    public LivingEntity getEntity() { return entity; }
    public @Nullable ResourceLocation getOldFaction() { return oldFaction; }
    public @Nullable ResourceLocation getNewFaction() { return newFaction; }
}
