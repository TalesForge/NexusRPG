package com.talesforge.nexusrpg.api.buff;

import com.talesforge.nexusrpg.api.rarity.Rarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Buff type (behavior). Registered in {@code NexusRPGRegistries.BUFF_TYPES}.
 * The same type is used for single and team buffs.
 */
public class BuffType {
    private final ResourceLocation rarity;
    private final StackPolicy stackPolicy;
    private final int maxLevel;

    public BuffType() { this(Rarity.COMMON, StackPolicy.REFRESH, 1); }

    public BuffType(ResourceLocation rarity, StackPolicy stackPolicy, int maxLevel) {
        this.rarity = rarity;
        this.stackPolicy = stackPolicy;
        this.maxLevel = Math.max(1, maxLevel);
    }

    public ResourceLocation rarity() { return rarity; }
    public StackPolicy stackPolicy() { return stackPolicy; }
    public int maxLevel() { return maxLevel; }

    /** The buff has started to affect the entity (it was granted personally, or the entity joined a team with this buff).. */
    public void onApply(LivingEntity entity, BuffInstance buff) {}
    /** Every server tick while the buff is active. */
    public void onTick(LivingEntity entity, BuffInstance buff) {}
    /** The buff has stopped working (it expired, was removed, or the entity left the team). */
    public void onRemove(LivingEntity entity, BuffInstance buff) {}

    /** Merging with existing buffs according to {@link StackPolicy}. */
    public List<BuffInstance> merge(List<BuffInstance> current, BuffInstance incoming) {
        List<BuffInstance> out = new ArrayList<>(current);
        for (int i = 0; i < out.size(); i++) {
            BuffInstance ex = out.get(i);
            if (!ex.type().equals(incoming.type())) continue;

            int ticks = (ex.remainingTicks() < 0 || incoming.remainingTicks() < 0)
                    ? BuffInstance.PERMANENT
                    : Math.max(ex.remainingTicks(), incoming.remainingTicks());
            switch (stackPolicy) {
                case IGNORE -> { return current; }
                case REPLACE -> out.set(i, incoming);
                case REFRESH -> out.set(i, ex.withLevel(Math.max(ex.level(), incoming.level())).withRemaining(ticks));
                case STACK -> out.set(i, ex.withLevel(Math.min(maxLevel, ex.level() + incoming.level())).withRemaining(ticks));
            }
            return out;
        }
        out.add(incoming);
        return out;
    }
}
