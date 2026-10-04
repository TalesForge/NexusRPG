package com.talesforge.nexusrpg.internal.service;

import com.talesforge.nexusrpg.api.NexusRPGRegistries;
import com.talesforge.nexusrpg.api.buff.BuffInstance;
import com.talesforge.nexusrpg.api.buff.BuffType;
import com.talesforge.nexusrpg.api.event.TeamBuffApplyEvent;
import com.talesforge.nexusrpg.api.event.TeamMemberJoinEvent;
import com.talesforge.nexusrpg.api.event.TeamMemberLeaveEvent;
import com.talesforge.nexusrpg.api.team.RpgTeam;
import com.talesforge.nexusrpg.api.team.TeamService;
import com.talesforge.nexusrpg.internal.data.ProfileStore;
import com.talesforge.nexusrpg.internal.data.TeamManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class TeamServiceImpl implements TeamService {

    @Override
    public Optional<RpgTeam> create(MinecraftServer s, String name, @Nullable LivingEntity founder) {
        TeamManager m = TeamManager.get(s);
        if (m.byName(name).isPresent()) return Optional.empty();
        RpgTeam team = m.create(name);
        if (founder != null) {
            if (!join(team.id(), founder)) { m.disband(team.id()); return Optional.empty(); }
            m.setLeader(team.id(), founder.getUUID());
        }
        return Optional.of(team);
    }

    @Override public Optional<RpgTeam> find(MinecraftServer s, UUID id) { return TeamManager.get(s).get(id); }
    @Override public Optional<RpgTeam> findByName(MinecraftServer s, String n) { return TeamManager.get(s).byName(n); }
    @Override public Collection<RpgTeam> all(MinecraftServer s) { return TeamManager.get(s).all(); }

    @Override
    public Optional<RpgTeam> teamOf(LivingEntity e) {
        MinecraftServer s = e.getServer();
        if (s == null) return Optional.empty();
        return ProfileStore.get(e).teamId().flatMap(id -> TeamManager.get(s).get(id));
    }

    @Override
    public boolean sameTeam(LivingEntity a, LivingEntity b) {
        var ta = ProfileStore.get(a).teamId();
        return ta.isPresent() && ta.equals(ProfileStore.get(b).teamId());
    }

    @Override
    public boolean join(UUID teamId, LivingEntity e) {
        Guard.server(e);
        TeamManager m = TeamManager.get(e.getServer());
        RpgTeam team = m.get(teamId).orElse(null);
        if (team == null) return false;
        if (team.members().contains(e.getUUID())) return true;
        if (NeoForge.EVENT_BUS.post(new TeamMemberJoinEvent(team, e)).isCanceled()) return false;

        leave(e);  // From the previous team (if any)
        m.addMember(teamId, e.getUUID());
        ProfileStore.update(e, p -> p.withTeam(teamId));
        for (BuffInstance b : team.buffs()) {
            BuffType t = NexusRPGRegistries.BUFF_TYPES.get(b.type());
            if (t != null) t.onApply(e, b);
        }
        return true;
    }

    @Override
    public boolean leave(LivingEntity e) {
        Guard.server(e);
        UUID teamId = ProfileStore.get(e).teamId().orElse(null);
        if (teamId == null) return false;

        TeamManager m = TeamManager.get(e.getServer());
        RpgTeam team = m.get(teamId).orElse(null);
        if (team != null) {
            NeoForge.EVENT_BUS.post(new TeamMemberLeaveEvent(team, e));
            for (BuffInstance b : team.buffs()) {
                BuffType t = NexusRPGRegistries.BUFF_TYPES.get(b.type());
                if (t != null) t.onRemove(e, b);
            }
            m.removeMember(teamId, e.getUUID());  // Passes leadership, deletes the empty command.
        }
        ProfileStore.update(e, p -> p.withTeam(null));
        return true;
    }

    @Override
    public boolean disband(MinecraftServer s, UUID teamId) {
        TeamManager m = TeamManager.get(s);
        RpgTeam team = m.get(teamId).orElse(null);
        if (team == null) return false;
        List<BuffInstance> buffs = List.copyOf(team.buffs());
        for (LivingEntity member : onlineMembers(s, teamId)) {
            for (BuffInstance b : buffs) {
                BuffType t = NexusRPGRegistries.BUFF_TYPES.get(b.type());
                if (t != null) t.onRemove(member, b);
            }
            ProfileStore.update(member, p -> p.withTeam(null));
        }
        m.disband(teamId);
        return true;
    }

    @Override
    public List<LivingEntity> onlineMembers(MinecraftServer s, UUID teamId) {
        List<LivingEntity> out = new ArrayList<>();
        TeamManager.get(s).get(teamId).ifPresent(team -> {
            for (UUID u : team.members()) {
                if (findEntity(s, u) instanceof LivingEntity le) out.add(le);
            }
        });
        return out;
    }

    @Override
    public boolean applyBuff(MinecraftServer s, UUID teamId, BuffInstance buff) {
        TeamManager m = TeamManager.get(s);
        RpgTeam team = m.get(teamId).orElse(null);
        BuffType type = NexusRPGRegistries.BUFF_TYPES.get(buff.type());
        if (team == null || type == null) return false;
        if (NeoForge.EVENT_BUS.post(new TeamBuffApplyEvent(team, buff)).isCanceled()) return false;

        boolean isNew = team.buffs().stream().noneMatch(b -> b.type().equals(buff.type()));
        m.setBuffs(teamId, type.merge(team.buffs(), buff));
        if (isNew) onlineMembers(s, teamId).forEach(le -> type.onApply(le, buff));
        return true;
    }

    /** Each server tick: countdown of the duration of team buffs. */
    public static void tickTeamBuffs(MinecraftServer s) {
        TeamManager m = TeamManager.get(s);
        TeamServiceImpl svc = (TeamServiceImpl) com.talesforge.nexusrpg.api.NexusRPGApi.teams();
        for (RpgTeam team : List.copyOf(m.all())) {
            if (team.buffs().isEmpty()) continue;
            List<BuffInstance> next = new ArrayList<>();
            List<BuffInstance> expired = new ArrayList<>();
            for (BuffInstance b : team.buffs()) {
                BuffInstance nb = b.tick();
                if (nb.remainingTicks() == 0) expired.add(b); else next.add(nb);
            }
            m.setBuffs(team.id(), next);
            if (!expired.isEmpty()) {
                for (LivingEntity member : svc.onlineMembers(s, team.id())) {
                    for (BuffInstance b : expired) {
                        BuffType t = NexusRPGRegistries.BUFF_TYPES.get(b.type());
                        if (t != null) t.onRemove(member, b);
                    }
                }
            }
        }
    }

    private static @Nullable Entity findEntity(MinecraftServer s, UUID uuid) {
        ServerPlayer p = s.getPlayerList().getPlayer(uuid);
        if (p != null) return p;
        for (ServerLevel level : s.getAllLevels()) {
            Entity e = level.getEntity(uuid);
            if (e != null) return e;
        }
        return null;
    }
}
