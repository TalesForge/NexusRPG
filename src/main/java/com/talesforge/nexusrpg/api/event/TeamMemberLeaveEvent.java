package com.talesforge.nexusrpg.api.event;

import com.talesforge.nexusrpg.api.team.RpgTeam;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;

/** The entity is leaving the team (notification, cannot be canceled). */
public class TeamMemberLeaveEvent extends Event {
    private final RpgTeam team;
    private final LivingEntity entity;

    public TeamMemberLeaveEvent(RpgTeam team, LivingEntity entity) {
        this.team = team;
        this.entity = entity;
    }

    public RpgTeam getTeam() { return team; }
    public LivingEntity getEntity() { return entity; }
}
