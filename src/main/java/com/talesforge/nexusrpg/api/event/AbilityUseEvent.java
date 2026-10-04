package com.talesforge.nexusrpg.api.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/** Before using the skill (cancelable). */
public class AbilityUseEvent extends Event implements ICancellableEvent {
    private final LivingEntity caster;
    private final ResourceLocation ability;
    private final @Nullable LivingEntity target;

    public AbilityUseEvent(LivingEntity caster, ResourceLocation ability, @Nullable LivingEntity target) {
        this.caster = caster;
        this.ability = ability;
        this.target = target;
    }

    public LivingEntity getCaster() { return caster; }
    public ResourceLocation getAbility() { return ability; }
    public @Nullable LivingEntity getTarget() { return target; }
}
