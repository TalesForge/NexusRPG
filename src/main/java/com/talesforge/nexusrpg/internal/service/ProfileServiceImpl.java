package com.talesforge.nexusrpg.internal.service;

import com.talesforge.nexusrpg.api.NexusRPGApi;
import com.talesforge.nexusrpg.api.event.ClassChangeEvent;
import com.talesforge.nexusrpg.api.event.FactionChangeEvent;
import com.talesforge.nexusrpg.api.faction.Faction;
import com.talesforge.nexusrpg.api.faction.Relation;
import com.talesforge.nexusrpg.api.profile.ProfileService;
import com.talesforge.nexusrpg.api.profile.RpgProfile;
import com.talesforge.nexusrpg.internal.data.ProfileStore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ProfileServiceImpl implements ProfileService {
    @Override public RpgProfile get(LivingEntity e) { return ProfileStore.get(e); }
    @Override public Optional<ResourceLocation> faction(LivingEntity e) { return get(e).faction(); }
    @Override public List<ResourceLocation> classes(LivingEntity e) { return get(e).classes(); }
    @Override public boolean hasClass(LivingEntity e, ResourceLocation c) { return get(e).classes().contains(c); }

    @Override
    public boolean setFaction(LivingEntity e, @Nullable ResourceLocation faction) {
        Guard.server(e);
        if (faction != null && NexusRPGApi.faction(e.level().registryAccess(), faction).isEmpty()) return false;
        ResourceLocation old = faction(e).orElse(null);
        if (NeoForge.EVENT_BUS.post(new FactionChangeEvent(e, old, faction)).isCanceled()) return false;
        ProfileStore.update(e, p -> p.withFaction(faction));
        return true;
    }

    @Override
    public boolean addClass(LivingEntity e, ResourceLocation c) {
        Guard.server(e);
        if (NexusRPGApi.rpgClass(e.level().registryAccess(), c).isEmpty() || hasClass(e, c)) return false;
        if (NeoForge.EVENT_BUS.post(new ClassChangeEvent(e, c, true)).isCanceled()) return false;
        ProfileStore.update(e, p -> {
            List<ResourceLocation> l = new ArrayList<>(p.classes());
            l.add(c);
            return p.withClasses(l);
        });
        return true;
    }

    @Override
    public boolean removeClass(LivingEntity e, ResourceLocation c) {
        Guard.server(e);
        if (!hasClass(e, c)) return false;
        if (NeoForge.EVENT_BUS.post(new ClassChangeEvent(e, c, false)).isCanceled()) return false;
        ProfileStore.update(e, p -> {
            List<ResourceLocation> l = new ArrayList<>(p.classes());
            l.remove(c);
            return p.withClasses(l);
        });
        return true;
    }

    @Override
    public Relation relation(LivingEntity a, LivingEntity b) {
        RpgProfile pa = get(a), pb = get(b);
        if (pa.teamId().isPresent() && pa.teamId().equals(pb.teamId())) return Relation.ALLY;
        if (pa.faction().isEmpty() || pb.faction().isEmpty()) return Relation.NEUTRAL;
        ResourceLocation fa = pa.faction().get(), fb = pb.faction().get();
        if (fa.equals(fb)) return Relation.ALLY;
        return NexusRPGApi.faction(a.level().registryAccess(), fa)
                .map((Faction f) -> f.relationTo(fb))
                .orElse(Relation.NEUTRAL);
    }
}
