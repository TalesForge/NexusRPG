package com.talesforge.nexusrpg.api.event;

import com.talesforge.nexusrpg.api.team.LeaveReason;
import com.talesforge.nexusrpg.api.team.RpgTeam;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;

/** The entity is leaving the team (notification, cannot be canceled). Fired for each online member on disband too. */
public class TeamMemberLeaveEvent extends Event {
    private final RpgTeam team;
    private final LivingEntity entity;
    private final LeaveReason reason;

    public TeamMemberLeaveEvent(RpgTeam team, LivingEntity entity, LeaveReason reason) {
        this.team = team;
        this.entity = entity;
        this.reason = reason;
    }

    public RpgTeam getTeam() { return team; }
    public LivingEntity getEntity() { return entity; }
    public LeaveReason getReason() { return reason; }
}
