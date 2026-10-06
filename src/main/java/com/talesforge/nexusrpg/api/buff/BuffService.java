package com.talesforge.nexusrpg.api.buff;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface BuffService {
    /** Personal buffs (also available in the client). */
    List<BuffInstance> personal(LivingEntity entity);

    /** Personal + team. Server only (commands are not synchronized on the client). */
    List<BuffInstance> effective(LivingEntity entity);

    boolean has(LivingEntity entity, ResourceLocation type);

    /** Give a personal buff. false - unknown type or event canceled. Server only. */
    boolean apply(LivingEntity entity, BuffInstance buff);

    boolean remove(LivingEntity entity, ResourceLocation type);

    /** Removes every personal buff that was given by {@code source}. Server only. @return how many were removed. */
    int removeFromSource(LivingEntity entity, UUID source);
}
