package com.talesforge.nexusrpg.api.ability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface AbilityService {
    /** The skill is known to the entity (provided by its classes). */
    boolean knows(LivingEntity entity, ResourceLocation ability);

    /** The remaining cooldown in ticks (0 = ready). */
    long cooldownRemaining(LivingEntity entity, ResourceLocation ability);

    /** Only the server. */
    AbilityResult use(LivingEntity caster, ResourceLocation ability, @Nullable LivingEntity target);
}
