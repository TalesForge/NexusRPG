package com.talesforge.nexusrpg.api.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/** The class is added/removed (reversible). */
public class ClassChangeEvent extends Event implements ICancellableEvent {
    private final LivingEntity entity;
    private final ResourceLocation rpgClass;
    private final boolean added;

    public ClassChangeEvent(LivingEntity entity, ResourceLocation rpgClass, boolean added) {
        this.entity = entity;
        this.rpgClass = rpgClass;
        this.added = added;
    }

    public LivingEntity getEntity() { return entity; }
    public ResourceLocation getRpgClass() { return rpgClass; }
    public boolean isAdded() { return added; }
}
