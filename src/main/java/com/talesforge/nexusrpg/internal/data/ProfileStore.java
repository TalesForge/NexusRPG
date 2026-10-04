package com.talesforge.nexusrpg.internal.data;

import com.talesforge.nexusrpg.api.profile.RpgProfile;
import com.talesforge.nexusrpg.internal.network.ProfileSync;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.UnaryOperator;

/** The only place where the attachment profile is read/written. */
public final class ProfileStore {
    public static RpgProfile get(LivingEntity e) {
        return e.hasData(RpgAttachments.PROFILE) ? e.getData(RpgAttachments.PROFILE) : RpgProfile.EMPTY;
    }

    /** Record and synchronize with clients. */
    public static void set(LivingEntity e, RpgProfile p) {
        setQuiet(e, p);
        if (!e.level().isClientSide()) ProfileSync.broadcast(e);
    }

    /** Record WITHOUT synchronization (frame‑by‑frame changes, e.g., buff countdown). */
    public static void setQuiet(LivingEntity e, RpgProfile p) {
        if (p.isEmpty()) {
            if (e.hasData(RpgAttachments.PROFILE)) e.removeData(RpgAttachments.PROFILE);
        } else {
            e.setData(RpgAttachments.PROFILE, p);
        }
    }

    public static void update(LivingEntity e, UnaryOperator<RpgProfile> f) {
        set(e, f.apply(get(e)));
    }

    private ProfileStore() {}
}
