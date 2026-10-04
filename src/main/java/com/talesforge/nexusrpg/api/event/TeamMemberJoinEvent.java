package com.talesforge.nexusrpg.api.event;

import com.talesforge.nexusrpg.api.team.RpgTeam;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/** The entity (player or mob) joins the team (cancelable). */
public class TeamMemberJoinEvent extends Event implements ICancellableEvent {
    private final RpgTeam team;
    private final LivingEntity entity;

    public TeamMemberJoinEvent(RpgTeam team, LivingEntity entity) {
        this.team = team;
        this.entity = entity;
    }

    public RpgTeam getTeam() { return team; }
    public LivingEntity getEntity() { return entity; }
}
