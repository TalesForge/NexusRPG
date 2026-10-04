package com.talesforge.nexusrpg.api.ability;

import com.talesforge.nexusrpg.api.rarity.Rarity;
import net.minecraft.resources.ResourceLocation;

/** Skill/ability. Registered in {@code NexusRPGRegistries.ABILITIES}. */
public abstract class Ability {
    private final ResourceLocation rarity;
    private final int cooldownTicks;

    protected Ability(int cooldownTicks) { this(Rarity.COMMON, cooldownTicks); }

    protected Ability(ResourceLocation rarity, int cooldownTicks) {
        this.rarity = rarity;
        this.cooldownTicks = Math.max(0, cooldownTicks);
    }

    public ResourceLocation rarity() { return rarity; }
    public int cooldownTicks() { return cooldownTicks; }

    /** @return true if the skill has been triggered (in which case the cooldown is activated). */
    public abstract boolean execute(AbilityContext context);
}
