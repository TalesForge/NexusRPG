package com.talesforge.nexusrpg.internal.data;

import com.talesforge.nexusrpg.NexusRPG;
import com.talesforge.nexusrpg.api.profile.RpgProfile;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class RpgAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES,  NexusRPG.MOD_ID);

    /**
     * IMPORTANT: getData() with a default value ATTACHES it to the entity. Therefore, we use
     * {@link ProfileStore} everywhere, which checks hasData() and doesn’t clutter the NBT of each entity.
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<RpgProfile>> PROFILE =
            ATTACHMENTS.register("profile", () -> AttachmentType.builder(() -> RpgProfile.EMPTY)
                    .serialize(RpgProfile.CODEC)
                    .copyOnDeath()
                    .build());

    private RpgAttachments() {}
}
