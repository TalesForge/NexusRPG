package com.talesforge.nexusrpg.api.profile;

import com.talesforge.nexusrpg.api.faction.Relation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/** Factions and entity classes. Modifying methods — server only, return false if rejected/canceled. */
public interface ProfileService {
    RpgProfile get(LivingEntity entity);

    Optional<ResourceLocation> faction(LivingEntity entity);
    boolean setFaction(LivingEntity entity, @Nullable ResourceLocation faction);

    List<ResourceLocation> classes(LivingEntity entity);
    boolean hasClass(LivingEntity entity, ResourceLocation rpgClass);
    boolean addClass(LivingEntity entity, ResourceLocation rpgClass);
    boolean removeClass(LivingEntity entity, ResourceLocation rpgClass);

    /** The relationship between {@code a} and {@code b}: one command = ALLY, then factions, otherwise NEUTRAL. */
    Relation relation(LivingEntity a, LivingEntity b);
}
