package com.talesforge.nexusrpg.api.buff;

/** What to do if you already have a buff of the same type. */
public enum StackPolicy {
    /** Leave a longer period and a higher level. */
    REFRESH,
    /** Replace with a new one. */
    REPLACE,
    /** Add up the levels (up to maxLevel), the duration is longer. */
    STACK,
    /** Ignore the new one. */
    IGNORE
}
