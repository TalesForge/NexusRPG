package com.talesforge.nexusrpg.internal.service;

import java.util.UUID;
import com.talesforge.nexusrpg.api.NexusRPGApi;
import com.talesforge.nexusrpg.api.NexusRPGRegistries;
import com.talesforge.nexusrpg.api.buff.BuffInstance;
import com.talesforge.nexusrpg.api.buff.BuffService;
import com.talesforge.nexusrpg.api.buff.BuffType;
import com.talesforge.nexusrpg.api.event.BuffApplyEvent;
import com.talesforge.nexusrpg.api.profile.RpgProfile;
import com.talesforge.nexusrpg.internal.data.ProfileStore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public final class BuffServiceImpl implements BuffService {
    @Override
    public List<BuffInstance> personal(LivingEntity e) { return ProfileStore.get(e).buffs(); }

    @Override
    public List<BuffInstance> effective(LivingEntity e) {
        Guard.server(e);
        List<BuffInstance> out = new ArrayList<>(personal(e));
        NexusRPGApi.teams().teamOf(e).ifPresent(t -> out.addAll(t.buffs()));
        return out;
    }

    @Override
    public boolean has(LivingEntity e, ResourceLocation type) {
        List<BuffInstance> list = e.level().isClientSide() ? personal(e) : effective(e);
        return list.stream().anyMatch(b -> b.type().equals(type));
    }

    @Override
    public boolean apply(LivingEntity e, BuffInstance buff) {
        Guard.server(e);
        BuffType type = NexusRPGRegistries.BUFF_TYPES.get(buff.type());
        if (type == null) return false;
        if (NeoForge.EVENT_BUS.post(new BuffApplyEvent(e, buff)).isCanceled()) return false;

        RpgProfile p = ProfileStore.get(e);
        boolean isNew = p.buffs().stream().noneMatch(b -> b.sameSlot(buff));
        ProfileStore.set(e, p.withBuffs(type.merge(p.buffs(), buff)));
        if (isNew) type.onApply(e, buff);
        return true;
    }

    @Override
    public int removeFromSource(LivingEntity e, UUID source) {
        Guard.server(e);
        RpgProfile p = ProfileStore.get(e);
        List<BuffInstance> removed = p.buffs().stream()
                .filter(b -> b.source().map(source::equals).orElse(false)).toList();
        if (removed.isEmpty()) return 0;
        List<BuffInstance> next = new ArrayList<>(p.buffs());
        next.removeAll(removed);
        ProfileStore.set(e, p.withBuffs(next));
        for (BuffInstance b : removed) {
            BuffType t = NexusRPGRegistries.BUFF_TYPES.get(b.type());
            if (t != null) t.onRemove(e, b);
        }
        return removed.size();
    }

    @Override
    public boolean remove(LivingEntity e, ResourceLocation typeId) {
        Guard.server(e);
        RpgProfile p = ProfileStore.get(e);
        BuffInstance found = p.buffs().stream().filter(b -> b.type().equals(typeId)).findFirst().orElse(null);
        if (found == null) return false;
        List<BuffInstance> next = new ArrayList<>(p.buffs());
        next.remove(found);
        ProfileStore.set(e, p.withBuffs(next));
        BuffType type = NexusRPGRegistries.BUFF_TYPES.get(typeId);
        if (type != null) type.onRemove(e, found);
        return true;
    }

    /** Each tick is called for the entity with the profile (server). */
    public static void tickEntity(LivingEntity e) {
        RpgProfile p = ProfileStore.get(e);

        // Personal buffs. We synchronize structural changes, simple counting — no.
        if (!p.buffs().isEmpty()) {
            List<BuffInstance> next = new ArrayList<>(p.buffs().size());
            boolean structural = false;
            for (BuffInstance b : p.buffs()) {
                BuffType t = NexusRPGRegistries.BUFF_TYPES.get(b.type());
                if (t == null) { structural = true; continue; }  // The mod with this buff has been removed.
                t.onTick(e, b);
                BuffInstance nb = b.tick();
                if (nb.remainingTicks() == 0) { t.onRemove(e, b); structural = true; }
                else next.add(nb);
            }
            // NB: if onTick changed the profile itself, this entry will overwrite it — this is acceptable for the skeleton.
            RpgProfile updated = p.withBuffs(next);
            if (structural) ProfileStore.set(e, updated); else ProfileStore.setQuiet(e, updated);
        }

        // Team buffs: only onTick is available here; the time limit is calculated by TeamServiceImpl.tickTeamBuffs.
        if (p.teamId().isPresent()) {
            NexusRPGApi.teams().teamOf(e).ifPresent(team -> {
                for (BuffInstance b : team.buffs()) {
                    BuffType t = NexusRPGRegistries.BUFF_TYPES.get(b.type());
                    if (t != null) t.onTick(e, b);
                }
            });
        }
    }
}
