package com.talesforge.nexusrpg.api.event;

import com.talesforge.nexusrpg.api.buff.BuffInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/** The personal buff is granted to the entity (cancelable). */
public class BuffApplyEvent extends Event implements ICancellableEvent {
    private final LivingEntity entity;
    private final BuffInstance buff;

    public BuffApplyEvent(LivingEntity entity, BuffInstance buff) {
        this.entity = entity;
        this.buff = buff;
    }

    public LivingEntity getEntity() { return entity; }
    public BuffInstance getBuff() { return buff; }
}
