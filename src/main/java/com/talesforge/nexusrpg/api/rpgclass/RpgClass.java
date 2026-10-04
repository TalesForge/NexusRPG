package com.talesforge.nexusrpg.api.rpgclass;

import com.talesforge.nexusrpg.api.rarity.Rarity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Class. Datapack-registry {@code nexusrpg:class}.
 *
 * @param type        id {@link ClassType}
 * @param rarity      id {@link Rarity}
 * @param weaponTypes what types of weapons are allowed (empty = no restrictions)
 * @param abilities   what skills the class provides
 */
public record RpgClass(ResourceLocation type,
                       ResourceLocation rarity,
                       List<ResourceLocation> weaponTypes,
                       List<ResourceLocation> abilities) {
    public static final Codec<RpgClass> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("type").forGetter(RpgClass::type),
            ResourceLocation.CODEC.optionalFieldOf("rarity", Rarity.COMMON).forGetter(RpgClass::rarity),
            ResourceLocation.CODEC.listOf().optionalFieldOf("weapon_types", List.of()).forGetter(RpgClass::weaponTypes),
            ResourceLocation.CODEC.listOf().optionalFieldOf("abilities", List.of()).forGetter(RpgClass::abilities)
    ).apply(i, RpgClass::new));
}
