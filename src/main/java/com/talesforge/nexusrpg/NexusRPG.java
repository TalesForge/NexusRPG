package com.talesforge.nexusrpg;

import com.talesforge.nexusrpg.config.Config;
import com.talesforge.nexusrpg.internal.data.RpgAttachments;
import com.talesforge.nexusrpg.internal.data.RpgComponents;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(NexusRPG.MOD_ID)
public class NexusRPG {
    public static final String MOD_ID = "nexusrpg";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NexusRPG(IEventBus modEventBus, ModContainer modContainer) {
        RpgAttachments.ATTACHMENTS.register(modEventBus);
        RpgComponents.COMPONENTS.register(modEventBus);
        // Registers, packages, and data maps are registered in internal.RpgModEvents.

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("NexusRPG started!");
    }
}
