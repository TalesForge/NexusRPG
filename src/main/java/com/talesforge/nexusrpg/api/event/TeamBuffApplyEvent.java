package com.talesforge.nexusrpg.api.event;

import com.talesforge.nexusrpg.api.buff.BuffInstance;
import com.talesforge.nexusrpg.api.team.RpgTeam;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/** The team buff is granted to the team (cancelable). */
public class TeamBuffApplyEvent extends Event implements ICancellableEvent {
    private final RpgTeam team;
    private final BuffInstance buff;

    public TeamBuffApplyEvent(RpgTeam team, BuffInstance buff) {
        this.team = team;
        this.buff = buff;
    }

    public RpgTeam getTeam() { return team; }
    public BuffInstance getBuff() { return buff; }
}
