package com.talesforge.nexusrpg.api.profile;

import com.talesforge.nexusrpg.api.faction.Relation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Factions and entity classes. Modifying methods — server only, return false if rejected/canceled.
 * <p>
 * All read methods return EFFECTIVE values: if the entity was never customized, they come from the
 * defaults of its entity type ({@link DefaultProfile}), otherwise from what is stored on the entity.
 */
public interface ProfileService {
    /** Effective profile (defaults applied). Safe on both client and server. */
    RpgProfile get(LivingEntity entity);

    Optional<ResourceLocation> faction(LivingEntity entity);
    /** Sets the faction (null = no faction). From now on the entity no longer uses its type's defaults. */
    boolean setFaction(LivingEntity entity, @Nullable ResourceLocation faction);

    List<ResourceLocation> classes(LivingEntity entity);
    boolean hasClass(LivingEntity entity, ResourceLocation rpgClass);
    boolean addClass(LivingEntity entity, ResourceLocation rpgClass);
    boolean removeClass(LivingEntity entity, ResourceLocation rpgClass);

    /** True if faction/classes were explicitly changed on this entity (so its type's defaults are ignored). */
    boolean isCustomized(LivingEntity entity);

    /** Forgets explicit faction/classes: the entity goes back to the defaults of its type. Server only. */
    void resetToDefaults(LivingEntity entity);

    /** The relationship between {@code a} and {@code b}: one team = ALLY, then factions, otherwise NEUTRAL. */
    Relation relation(LivingEntity a, LivingEntity b);
}
