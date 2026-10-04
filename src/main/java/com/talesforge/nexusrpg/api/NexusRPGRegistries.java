package com.talesforge.nexusrpg.api;

import com.talesforge.nexusrpg.NexusRPG;
import com.talesforge.nexusrpg.api.ability.Ability;
import com.talesforge.nexusrpg.api.buff.BuffType;
import com.talesforge.nexusrpg.api.faction.Faction;
import com.talesforge.nexusrpg.api.rarity.Rarity;
import com.talesforge.nexusrpg.api.rpgclass.ClassType;
import com.talesforge.nexusrpg.api.rpgclass.RpgClass;
import com.talesforge.nexusrpg.api.weapon.WeaponType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * All NexusRPG registries. A single point through which dependent mods "see" the same data.
 *
 * <ul>
 *   <li><b>Common registries</b> (code): {@link #ABILITIES}, {@link #BUFF_TYPES} - registered via
 *       {@code DeferredRegister.create(NexusRPGRegistries.ABILITIES, "mymod")}.</li>
 *   <li><b>Datapack registries</b> (JSON): rarity, class_type, weapon_type, faction, class -
 *       {@code data/<ns>/nexusrpg/<registry>/<name>.json}. Read via {@code RegistryAccess}
 *       (see {@link NexusRPGApi}).</li>
 * </ul>
 */
public final class NexusRPGRegistries {
    // ===== Keys =====
    public static final ResourceKey<Registry<Ability>> ABILITY_KEY = key("ability");
    public static final ResourceKey<Registry<BuffType>> BUFF_TYPE_KEY = key("buff_type");

    public static final ResourceKey<Registry<Rarity>> RARITY_KEY = key("rarity");
    public static final ResourceKey<Registry<ClassType>> CLASS_TYPE_KEY = key("class_type");
    public static final ResourceKey<Registry<WeaponType>> WEAPON_TYPE_KEY = key("weapon_type");
    public static final ResourceKey<Registry<Faction>> FACTION_KEY = key("faction");
    public static final ResourceKey<Registry<RpgClass>> CLASS_KEY = key("class");

    // ===== Regular (synchronizable) registries =====
    public static final Registry<Ability> ABILITIES = new RegistryBuilder<>(ABILITY_KEY).sync(true).create();
    public static final Registry<BuffType> BUFF_TYPES = new RegistryBuilder<>(BUFF_TYPE_KEY).sync(true).create();

    private static <T> ResourceKey<Registry<T>> key(String path) {
        return ResourceKey.createRegistryKey(NexusRPG.id(path));
    }

    private NexusRPGRegistries() {}
}
