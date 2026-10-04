package com.talesforge.nexusrpg.api;

import com.talesforge.nexusrpg.api.ability.AbilityService;
import com.talesforge.nexusrpg.api.buff.BuffService;
import com.talesforge.nexusrpg.api.faction.Faction;
import com.talesforge.nexusrpg.api.profile.ProfileService;
import com.talesforge.nexusrpg.api.rarity.Rarity;
import com.talesforge.nexusrpg.api.rpgclass.ClassType;
import com.talesforge.nexusrpg.api.rpgclass.RpgClass;
import com.talesforge.nexusrpg.api.team.TeamService;
import com.talesforge.nexusrpg.api.weapon.WeaponType;
import com.talesforge.nexusrpg.internal.service.AbilityServiceImpl;
import com.talesforge.nexusrpg.internal.service.BuffServiceImpl;
import com.talesforge.nexusrpg.internal.service.ProfileServiceImpl;
import com.talesforge.nexusrpg.internal.service.TeamServiceImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * The only entry point for dependent mods.
 * All data (profiles, commands, buffs) lives in NexusRPG — add‑on mods do not store their own copies.
 */
public final class NexusRPGApi {
    private static final ProfileService PROFILES = new ProfileServiceImpl();
    private static final TeamService TEAMS = new TeamServiceImpl();
    private static final BuffService BUFFS = new BuffServiceImpl();
    private static final AbilityService ABILITIES = new AbilityServiceImpl();

    public static ProfileService profiles() { return PROFILES; }
    public static TeamService teams() { return TEAMS; }
    public static BuffService buffs() { return BUFFS; }
    public static AbilityService abilities() { return ABILITIES; }

    // ===== Access to datapack registries (RegistryAccess is required: level.registryAccess()) =====
    public static <T> Optional<T> lookup(RegistryAccess access, ResourceKey<Registry<T>> key, ResourceLocation id) {
        return access.registry(key).map(r -> r.get(id));
    }

    public static Optional<Rarity> rarity(RegistryAccess a, ResourceLocation id) { return lookup(a, NexusRPGRegistries.RARITY_KEY, id); }
    public static Optional<ClassType> classType(RegistryAccess a, ResourceLocation id) { return lookup(a, NexusRPGRegistries.CLASS_TYPE_KEY, id); }
    public static Optional<RpgClass> rpgClass(RegistryAccess a, ResourceLocation id) { return lookup(a, NexusRPGRegistries.CLASS_KEY, id); }
    public static Optional<WeaponType> weaponType(RegistryAccess a, ResourceLocation id) { return lookup(a, NexusRPGRegistries.WEAPON_TYPE_KEY, id); }
    public static Optional<Faction> faction(RegistryAccess a, ResourceLocation id) { return lookup(a, NexusRPGRegistries.FACTION_KEY, id); }

    private NexusRPGApi() {}
}
