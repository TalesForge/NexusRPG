package com.talesforge.nexusrpg.api.buff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.UUID;

/**
 * Buff instance. Immutable.
 *
 * @param remainingTicks {@link #PERMANENT} (-1) = permanent
 * @param source         who issued it (optional)
 */
public record BuffInstance(ResourceLocation type, int level, int remainingTicks, Optional<UUID> source) {
    public static final int PERMANENT = -1;

    public static final Codec<BuffInstance> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("type").forGetter(BuffInstance::type),
            Codec.INT.optionalFieldOf("level", 1).forGetter(BuffInstance::level),
            Codec.INT.optionalFieldOf("remaining", PERMANENT).forGetter(BuffInstance::remainingTicks),
            UUIDUtil.CODEC.optionalFieldOf("source").forGetter(BuffInstance::source)
    ).apply(i, BuffInstance::new));

    public static BuffInstance of(ResourceLocation type, int level, int ticks) {
        return new BuffInstance(type, level, ticks, Optional.empty());
    }

    /** Same buff type from the same source: such instances are merged by the stack policy; different sources coexist. */
    public boolean sameSlot(BuffInstance other) {
        return type.equals(other.type) && source.equals(other.source);
    }

    public BuffInstance withLevel(int level) { return new BuffInstance(type, level, remainingTicks, source); }
    public BuffInstance withRemaining(int ticks) { return new BuffInstance(type, level, ticks, source); }

    /** One tick: the perpetual ones do not change; 0 = expired. */
    public BuffInstance tick() {
        return remainingTicks < 0 ? this : withRemaining(remainingTicks - 1);
    }
}
