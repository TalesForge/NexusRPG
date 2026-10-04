package com.talesforge.nexusrpg.internal.network;

import com.talesforge.nexusrpg.NexusRPG;
import com.talesforge.nexusrpg.api.profile.RpgProfile;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Server -> client: entity profile. (Attachments in 1.21.1 do not synchronize on their own.) */
public record ProfileSyncPayload(int entityId, RpgProfile profile) implements CustomPacketPayload {
    public static final Type<ProfileSyncPayload> TYPE = new Type<>(NexusRPG.id("profile_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProfileSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ProfileSyncPayload::entityId,
            ByteBufCodecs.fromCodec(RpgProfile.CODEC), ProfileSyncPayload::profile,
            ProfileSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
