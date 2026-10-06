package com.talesforge.nexusrpg.api.team;

/** Why an entity is leaving a team (see {@code TeamMemberLeaveEvent}). */
public enum LeaveReason {
    /** The entity left on its own (default for {@code TeamService#leave(LivingEntity)}). */
    LEFT,
    /** Removed by someone else (kicked, dismissed, released). */
    REMOVED,
    /** The whole team was disbanded. */
    DISBANDED,
    /** The entity died (mobs only: players stay in their team). */
    DEATH,
    /** A time limit ran out (e.g. an expired hire contract). */
    EXPIRED,
    /** Anything else; mods may use it for their own reasons. */
    OTHER
}
