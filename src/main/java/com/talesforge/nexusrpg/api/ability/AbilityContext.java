package com.talesforge.nexusrpg.api.ability;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public record AbilityContext(ServerLevel level, LivingEntity caster, @Nullable LivingEntity target) {}
