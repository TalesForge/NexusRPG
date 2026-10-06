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
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

/**
 * Checks every cross-reference in the data once the server has loaded (a missing id would otherwise
 * be silently ignored). It only logs warnings and never changes anything.
 */
public final class RpgValidator {
    private int problems = 0;

    public static void validate(MinecraftServer server) {
        RpgValidator v = new RpgValidator();
        v.run(server.registryAccess());
        if (v.problems == 0) NexusRPG.LOGGER.info("NexusRPG data check: all references are valid.");
        else NexusRPG.LOGGER.warn("NexusRPG data check: {} broken reference(s), see the warnings above.", v.problems);
    }

    private void run(RegistryAccess access) {
        Registry<Rarity> rarities = access.registryOrThrow(NexusRPGRegistries.RARITY_KEY);
        Registry<ClassType> classTypes = access.registryOrThrow(NexusRPGRegistries.CLASS_TYPE_KEY);
        Registry<WeaponType> weaponTypes = access.registryOrThrow(NexusRPGRegistries.WEAPON_TYPE_KEY);
        Registry<Faction> factions = access.registryOrThrow(NexusRPGRegistries.FACTION_KEY);
        Registry<RpgClass> classes = access.registryOrThrow(NexusRPGRegistries.CLASS_KEY);

        for (var e : classes.entrySet()) {
            ResourceLocation id = e.getKey().location();
            RpgClass c = e.getValue();
            require(classTypes, c.type(), "class " + id + ": type");
            require(rarities, c.rarity(), "class " + id + ": rarity");
            c.weaponTypes().forEach(w -> require(weaponTypes, w, "class " + id + ": weapon type"));
            c.abilities().forEach(a -> {
                if (!NexusRPGRegistries.ABILITIES.containsKey(a)) warn("class " + id + ": ability " + a + " is not registered");
            });
        }

        for (var e : factions.entrySet()) {
            ResourceLocation id = e.getKey().location();
            Faction f = e.getValue();
            require(rarities, f.rarity(), "faction " + id + ": rarity");
            f.relations().keySet().forEach(other -> require(factions, other, "faction " + id + ": relation target"));
        }

        BuiltInRegistries.ENTITY_TYPE.getDataMap(DefaultProfile.ENTITY_DEFAULT_PROFILE).forEach((key, def) -> {
            String where = "default_profile " + key.location();
            def.faction().ifPresent(f -> require(factions, f, where + ": faction"));
            def.classes().forEach(c -> require(classes, c, where + ": class"));
        });

        BuiltInRegistries.ITEM.getDataMap(WeaponHelper.ITEM_WEAPON_TYPE).forEach((key, type) ->
                require(weaponTypes, type, "weapon_type data map " + key.location() + ": weapon type"));
    }

    private <T> void require(Registry<T> registry, ResourceLocation id, String where) {
        if (!registry.containsKey(id)) warn(where + " -> " + id + " does not exist in " + registry.key().location());
    }

    private void warn(String message) {
        problems++;
        NexusRPG.LOGGER.warn("[NexusRPG data] {}", message);
    }
}
