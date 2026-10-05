package com.talesforge.nexusrpg.internal;

import com.talesforge.nexusrpg.NexusRPG;
import com.talesforge.nexusrpg.api.NexusRPGRegistries;
import com.talesforge.nexusrpg.api.faction.Faction;
import com.talesforge.nexusrpg.api.profile.DefaultProfile;
import com.talesforge.nexusrpg.api.rarity.Rarity;
import com.talesforge.nexusrpg.api.rpgclass.ClassType;
import com.talesforge.nexusrpg.api.rpgclass.RpgClass;
import com.talesforge.nexusrpg.api.weapon.WeaponHelper;
import com.talesforge.nexusrpg.api.weapon.WeaponType;
import com.talesforge.nexusrpg.internal.network.ClientProfileHandler;
import com.talesforge.nexusrpg.internal.network.ProfileSyncPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

/** Mod bus events: registries, packages, data maps. */
@EventBusSubscriber(modid = NexusRPG.MOD_ID)
public final class RpgModEvents {

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        event.register(NexusRPGRegistries.ABILITIES);
        event.register(NexusRPGRegistries.BUFF_TYPES);
    }

    @SubscribeEvent
    public static void onDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        // The third argument is the network codec: the datapack data is synchronized to the client.
        event.dataPackRegistry(NexusRPGRegistries.RARITY_KEY, Rarity.CODEC, Rarity.CODEC);
        event.dataPackRegistry(NexusRPGRegistries.CLASS_TYPE_KEY, ClassType.CODEC, ClassType.CODEC);
        event.dataPackRegistry(NexusRPGRegistries.WEAPON_TYPE_KEY, WeaponType.CODEC, WeaponType.CODEC);
        event.dataPackRegistry(NexusRPGRegistries.FACTION_KEY, Faction.CODEC, Faction.CODEC);
        event.dataPackRegistry(NexusRPGRegistries.CLASS_KEY, RpgClass.CODEC, RpgClass.CODEC);
    }

    @SubscribeEvent
    public static void onPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ProfileSyncPayload.TYPE, ProfileSyncPayload.STREAM_CODEC, ClientProfileHandler::handle);
    }

    @SubscribeEvent
    public static void onDataMaps(RegisterDataMapTypesEvent event) {
        event.register(WeaponHelper.ITEM_WEAPON_TYPE);
        event.register(DefaultProfile.ENTITY_DEFAULT_PROFILE);
    }

    private RpgModEvents() {}
}
