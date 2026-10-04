package com.talesforge.nexusrpg.internal.service;

import com.talesforge.nexusrpg.api.NexusRPGApi;
import com.talesforge.nexusrpg.api.NexusRPGRegistries;
import com.talesforge.nexusrpg.api.ability.*;
import com.talesforge.nexusrpg.api.event.AbilityUseEvent;
import com.talesforge.nexusrpg.api.rpgclass.RpgClass;
import com.talesforge.nexusrpg.internal.data.ProfileStore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public final class AbilityServiceImpl implements AbilityService {
    @Override
    public boolean knows(LivingEntity e, ResourceLocation ability) {
        for (ResourceLocation classId : NexusRPGApi.profiles().classes(e)) {
            boolean has = NexusRPGApi.rpgClass(e.level().registryAccess(), classId)
                    .map((RpgClass c) -> c.abilities().contains(ability)).orElse(false);
            if (has) return true;
        }
        return false;
    }

    @Override
    public long cooldownRemaining(LivingEntity e, ResourceLocation ability) {
        long until = ProfileStore.get(e).cooldowns().getOrDefault(ability, 0L);
        return Math.max(0, until - e.level().getGameTime());
    }

    @Override
    public AbilityResult use(LivingEntity caster, ResourceLocation id, @Nullable LivingEntity target) {
        Guard.server(caster);
        Ability ability = NexusRPGRegistries.ABILITIES.get(id);
        if (ability == null) return AbilityResult.UNKNOWN_ABILITY;
        if (!knows(caster, id)) return AbilityResult.NOT_LEARNED;
        if (cooldownRemaining(caster, id) > 0) return AbilityResult.ON_COOLDOWN;
        if (NeoForge.EVENT_BUS.post(new AbilityUseEvent(caster, id, target)).isCanceled()) return AbilityResult.CANCELLED;

        if (!ability.execute(new AbilityContext((ServerLevel) caster.level(), caster, target))) return AbilityResult.FAILED;

        long now = caster.level().getGameTime();
        ProfileStore.update(caster, p -> p.withCooldown(id, now + ability.cooldownTicks(), now));
        return AbilityResult.SUCCESS;
    }
}
