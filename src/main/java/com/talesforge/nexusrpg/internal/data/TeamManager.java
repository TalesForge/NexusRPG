package com.talesforge.nexusrpg.internal.data;

import com.talesforge.nexusrpg.NexusRPG;
import com.talesforge.nexusrpg.api.buff.BuffInstance;
import com.talesforge.nexusrpg.api.team.RpgTeam;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

/** Global command storage (one per server, located in the Overworld data). Source of truth for commands. */
public final class TeamManager extends SavedData {
    private static final String NAME = "nexusrpg_teams";
    private final Map<UUID, RpgTeam> teams = new LinkedHashMap<>();

    public static TeamManager get(MinecraftServer server) {
        // DataFixTypes = null: modded data does not have vanilla datafixers. If this crashes on your NeoForge build,
        // substitute DataFixTypes.SAVED_DATA_COMMAND_STORAGE.
        return server.overworld().getDataStorage()
                .computeIfAbsent(new Factory<>(TeamManager::new, TeamManager::load, null), NAME);
    }

    public Optional<RpgTeam> get(UUID id) { return Optional.ofNullable(teams.get(id)); }

    public Optional<RpgTeam> byName(String name) {
        return teams.values().stream().filter(t -> t.name().equalsIgnoreCase(name)).findFirst();
    }

    public Collection<RpgTeam> all() { return Collections.unmodifiableCollection(teams.values()); }

    public RpgTeam create(String name) {
        RpgTeam t = new RpgTeam(UUID.randomUUID(), name);
        teams.put(t.id(), t);
        setDirty();
        return t;
    }

    public void disband(UUID id) {
        if (teams.remove(id) != null) setDirty();
    }

    public void addMember(UUID teamId, UUID member) {
        RpgTeam t = teams.get(teamId);
        if (t == null) return;
        t.addMember(member);
        if (t.leader().isEmpty()) t.setLeader(member);
        setDirty();
    }

    /** Removes a participant; transfers leadership; deletes an empty command. */
    public void removeMember(UUID teamId, UUID member) {
        RpgTeam t = teams.get(teamId);
        if (t == null) return;
        t.removeMember(member);
        if (t.members().isEmpty()) {
            teams.remove(teamId);
        } else if (t.leader().map(member::equals).orElse(true)) {
            t.setLeader(t.members().iterator().next());
        }
        setDirty();
    }

    public void setLeader(UUID teamId, UUID member) {
        RpgTeam t = teams.get(teamId);
        if (t != null && t.members().contains(member)) { t.setLeader(member); setDirty(); }
    }

    public void setBuffs(UUID teamId, List<BuffInstance> buffs) {
        RpgTeam t = teams.get(teamId);
        if (t == null) return;
        t.setBuffs(buffs);
        setDirty();
    }

    // ===== persistence =====
    public static TeamManager load(CompoundTag tag, HolderLookup.Provider registries) {
        TeamManager m = new TeamManager();
        if (tag.contains("teams")) {
            RpgTeam.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("teams"))
                    .resultOrPartial(err -> NexusRPG.LOGGER.error("Failed to load teams: {}", err))
                    .ifPresent(list -> list.forEach(t -> m.teams.put(t.id(), t)));
        }
        return m;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        RpgTeam.CODEC.listOf().encodeStart(NbtOps.INSTANCE, List.copyOf(teams.values()))
                .resultOrPartial(err -> NexusRPG.LOGGER.error("Failed to save teams: {}", err))
                .ifPresent(t -> tag.put("teams", t));
        return tag;
    }
}
