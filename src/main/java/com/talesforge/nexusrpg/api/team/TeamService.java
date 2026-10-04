package com.talesforge.nexusrpg.api.team;

import com.talesforge.nexusrpg.api.buff.BuffInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Player and mob commands. Everything is only on the server. */
public interface TeamService {
    /** Creates a team (the name is unique). {@code founder} immediately joins and becomes the leader. */
    Optional<RpgTeam> create(MinecraftServer server, String name, @Nullable LivingEntity founder);

    Optional<RpgTeam> find(MinecraftServer server, UUID teamId);
    Optional<RpgTeam> findByName(MinecraftServer server, String name);
    Collection<RpgTeam> all(MinecraftServer server);

    Optional<RpgTeam> teamOf(LivingEntity entity);
    boolean sameTeam(LivingEntity a, LivingEntity b);

    /** Join (automatically leaves the previous team). false - no such team or the event was cancelled. */
    boolean join(UUID teamId, LivingEntity entity);
    boolean leave(LivingEntity entity);
    boolean disband(MinecraftServer server, UUID teamId);

    /** Participants who are currently loaded in the world (players and mobs). */
    List<LivingEntity> onlineMembers(MinecraftServer server, UUID teamId);

    /** Team buff: applies to all participants while they are in the team. */
    boolean applyBuff(MinecraftServer server, UUID teamId, BuffInstance buff);
}
