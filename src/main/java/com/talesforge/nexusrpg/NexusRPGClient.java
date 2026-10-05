package com.talesforge.nexusrpg;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Entry point for the client. Here, in the future: HUD buffs, list of team members, item rarity hints, etc.
 * All code that uses client classes (Minecraft, Screen...) should live only here or in the client package.
 */
@Mod(value = NexusRPG.MOD_ID, dist = Dist.CLIENT)
public class NexusRPGClient {
    public NexusRPGClient(ModContainer container) {
        // The settings screen in the mods menu
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
