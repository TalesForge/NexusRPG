package com.talesforge.nexusrpg.api.team;

import com.talesforge.nexusrpg.api.buff.BuffInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Team. The participant is the UUID of the entity (player OR mob). The mob may be unloaded, so by UUID
 * the entity may not be found — always check for null.
 * Modify via {@code TeamService}; public mutators are for internal implementation only.
 */
public final class RpgTeam {
    public static final Codec<RpgTeam> CODEC = RecordCodecBuilder.create(i -> i.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(RpgTeam::id),
            Codec.STRING.fieldOf("name").forGetter(RpgTeam::name),
            UUIDUtil.CODEC.listOf().optionalFieldOf("members", List.of()).forGetter(t -> List.copyOf(t.members)),
            UUIDUtil.CODEC.optionalFieldOf("leader").forGetter(t -> Optional.ofNullable(t.leader)),
            BuffInstance.CODEC.listOf().optionalFieldOf("buffs", List.of()).forGetter(t -> List.copyOf(t.buffs))
    ).apply(i, (id, name, members, leader, buffs) ->
            new RpgTeam(id, name, new LinkedHashSet<>(members), leader.orElse(null), new ArrayList<>(buffs))));

    private final UUID id;
    private final String name;
    private final Set<UUID> members;
    private @Nullable UUID leader;
    private final List<BuffInstance> buffs;

    @ApiStatus.Internal
    public RpgTeam(UUID id, String name) {
        this(id, name, new LinkedHashSet<>(), null, new ArrayList<>());
    }

    private RpgTeam(UUID id, String name, Set<UUID> members, @Nullable UUID leader, List<BuffInstance> buffs) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.leader = leader;
        this.buffs = buffs;
    }

    public UUID id() { return id; }
    public String name() { return name; }
    public Set<UUID> members() { return Collections.unmodifiableSet(members); }
    public Optional<UUID> leader() { return Optional.ofNullable(leader); }
    /** Team buffs. Effective buffs for a participant = personal + these. */
    public List<BuffInstance> buffs() { return Collections.unmodifiableList(buffs); }

    @ApiStatus.Internal public boolean addMember(UUID u) { return members.add(u); }
    @ApiStatus.Internal public boolean removeMember(UUID u) { return members.remove(u); }
    @ApiStatus.Internal public void setLeader(@Nullable UUID u) { this.leader = u; }
    @ApiStatus.Internal public void setBuffs(List<BuffInstance> b) { buffs.clear(); buffs.addAll(b); }
}
