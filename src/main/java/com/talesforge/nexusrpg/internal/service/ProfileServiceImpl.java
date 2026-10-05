package com.talesforge.nexusrpg.internal.service;

import com.talesforge.nexusrpg.api.NexusRPGApi;
import com.talesforge.nexusrpg.api.event.ClassChangeEvent;
import com.talesforge.nexusrpg.api.event.FactionChangeEvent;
import com.talesforge.nexusrpg.api.faction.Faction;
import com.talesforge.nexusrpg.api.faction.Relation;
import com.talesforge.nexusrpg.api.profile.DefaultProfile;
import com.talesforge.nexusrpg.api.profile.ProfileService;
import com.talesforge.nexusrpg.api.profile.RpgProfile;
import com.talesforge.nexusrpg.config.Config;
import com.talesforge.nexusrpg.internal.data.ProfileStore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ProfileServiceImpl implements ProfileService {

    // ========== reading (effective values) ==========

    @Override
    public RpgProfile get(LivingEntity e) {
        RpgProfile stored = ProfileStore.get(e);
        if (stored.customized()) return stored;
        DefaultProfile def = DefaultProfile.of(e.getType());
        if (def == null) return stored;
        // Defaults are applied only to the returned snapshot; nothing is written to the entity.
        return stored.withFaction(def.faction().orElse(null)).withClasses(def.classes());
    }

    @Override public Optional<ResourceLocation> faction(LivingEntity e) { return get(e).faction(); }
    @Override public List<ResourceLocation> classes(LivingEntity e) { return get(e).classes(); }
    @Override public boolean hasClass(LivingEntity e, ResourceLocation c) { return classes(e).contains(c); }
    @Override public boolean isCustomized(LivingEntity e) { return ProfileStore.get(e).customized(); }

    // ========== writing ==========

    /**
     * The first explicit change "detaches" the entity from its type's defaults: the current effective
     * faction/classes are copied into the stored profile, and from then on the stored values are authoritative.
     * Without this copy, adding one class would silently drop the default ones.
     */
    private static RpgProfile detached(RpgProfile stored, LivingEntity e) {
        if (stored.customized()) return stored;
        RpgProfile base = stored.withCustomized(true);
        DefaultProfile def = DefaultProfile.of(e.getType());
        if (def == null) return base;
        return base.withFaction(def.faction().orElse(null)).withClasses(def.classes());
    }

    @Override
    public boolean setFaction(LivingEntity e, @Nullable ResourceLocation faction) {
        Guard.server(e);
        if (faction != null && NexusRPGApi.faction(e.level().registryAccess(), faction).isEmpty()) return false;
        ResourceLocation old = faction(e).orElse(null);
        if (NeoForge.EVENT_BUS.post(new FactionChangeEvent(e, old, faction)).isCanceled()) return false;
        ProfileStore.update(e, p -> detached(p, e).withFaction(faction));
        return true;
    }

    @Override
    public boolean addClass(LivingEntity e, ResourceLocation c) {
        Guard.server(e);
        if (NexusRPGApi.rpgClass(e.level().registryAccess(), c).isEmpty() || hasClass(e, c)) return false;
        if (classes(e).size() >= Config.MAX_CLASSES_PER_ENTITY.get()) return false;
        if (NeoForge.EVENT_BUS.post(new ClassChangeEvent(e, c, true)).isCanceled()) return false;
        ProfileStore.update(e, p -> {
            RpgProfile d = detached(p, e);
            List<ResourceLocation> l = new ArrayList<>(d.classes());
            l.add(c);
            return d.withClasses(l);
        });
        return true;
    }

    @Override
    public boolean removeClass(LivingEntity e, ResourceLocation c) {
        Guard.server(e);
        if (!hasClass(e, c)) return false;
        if (NeoForge.EVENT_BUS.post(new ClassChangeEvent(e, c, false)).isCanceled()) return false;
        ProfileStore.update(e, p -> {
            RpgProfile d = detached(p, e);
            List<ResourceLocation> l = new ArrayList<>(d.classes());
            l.remove(c);
            return d.withClasses(l);
        });
        return true;
    }

    /** NB: does not fire change events (it is a maintenance operation, not a gameplay change). */
    @Override
    public void resetToDefaults(LivingEntity e) {
        Guard.server(e);
        ProfileStore.update(e, p -> p.withCustomized(false).withFaction(null).withClasses(List.of()));
    }

    // ========== relations ==========

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
