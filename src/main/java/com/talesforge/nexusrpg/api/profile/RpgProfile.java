package com.talesforge.nexusrpg.api.profile;

import com.talesforge.nexusrpg.api.buff.BuffInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The RPG profile of any {@code LivingEntity} (player, mod mob, vanilla mob). Immutable.
 * Stored as a data attachment, so it is saved together with the entity.
 * There’s no need to change it directly — use the services from {@code NexusRPGApi}; they send events and synchronize clients.
 * <p>
 * <b>Stored vs effective.</b> The record held in the attachment contains only what was explicitly set.
 * While {@link #customized()} is false, {@code faction} and {@code classes} are NOT stored values: the entity
 * uses the defaults of its type ({@link DefaultProfile}). {@code ProfileService.get(...)} returns the
 * <i>effective</i> profile (defaults already applied) — read from it, not from the attachment.
 *
 * @param customized false = faction/classes come from {@link DefaultProfile}; true = the stored faction/classes
 *                   are authoritative (even if empty, e.g. a mob whose faction was deliberately cleared).
 */
public record RpgProfile(Optional<ResourceLocation> faction,
                         List<ResourceLocation> classes,
                         Optional<UUID> teamId,
                         List<BuffInstance> buffs,
                         Map<ResourceLocation, Long> cooldowns,
                         boolean customized) {

    public static final RpgProfile EMPTY = new RpgProfile(Optional.empty(), List.of(), Optional.empty(), List.of(), Map.of(), false);

    public static final Codec<RpgProfile> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.optionalFieldOf("faction").forGetter(RpgProfile::faction),
            ResourceLocation.CODEC.listOf().optionalFieldOf("classes", List.of()).forGetter(RpgProfile::classes),
            UUIDUtil.CODEC.optionalFieldOf("team").forGetter(RpgProfile::teamId),
            BuffInstance.CODEC.listOf().optionalFieldOf("buffs", List.of()).forGetter(RpgProfile::buffs),
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.LONG).optionalFieldOf("cooldowns", Map.of()).forGetter(RpgProfile::cooldowns),
            Codec.BOOL.optionalFieldOf("customized", false).forGetter(RpgProfile::customized)
    ).apply(i, RpgProfile::new));

    /** True if there is nothing worth storing (the attachment can then be removed from the entity). */
    public boolean isEmpty() {
        return !customized && faction.isEmpty() && classes.isEmpty() && teamId.isEmpty() && buffs.isEmpty() && cooldowns.isEmpty();
    }

    public RpgProfile withFaction(@Nullable ResourceLocation f) {
        return new RpgProfile(Optional.ofNullable(f), classes, teamId, buffs, cooldowns, customized);
    }

    public RpgProfile withClasses(List<ResourceLocation> c) {
        return new RpgProfile(faction, List.copyOf(c), teamId, buffs, cooldowns, customized);
    }

    public RpgProfile withTeam(@Nullable UUID t) {
        return new RpgProfile(faction, classes, Optional.ofNullable(t), buffs, cooldowns, customized);
    }

    public RpgProfile withBuffs(List<BuffInstance> b) {
        return new RpgProfile(faction, classes, teamId, List.copyOf(b), cooldowns, customized);
    }

    public RpgProfile withCustomized(boolean c) {
        return new RpgProfile(faction, classes, teamId, buffs, cooldowns, c);
    }

    /** Sets the cooldown until the game time {@code untilGameTime}; at the same time, it clears the expired ones. */
    public RpgProfile withCooldown(ResourceLocation ability, long untilGameTime, long now) {
        Map<ResourceLocation, Long> m = new HashMap<>();
        cooldowns.forEach((k, v) -> { if (v > now) m.put(k, v); });
        m.put(ability, untilGameTime);
        return new RpgProfile(faction, classes, teamId, buffs, Map.copyOf(m), customized);
    }
}
