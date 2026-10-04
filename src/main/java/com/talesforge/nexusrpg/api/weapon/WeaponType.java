package com.talesforge.nexusrpg.api.weapon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Weapon type. Data pack registry {@code nexusrpg:weapon_type}. Items are linked via the data map. */
public record WeaponType(int hands, boolean ranged) {
    public static final Codec<WeaponType> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("hands", 1).forGetter(WeaponType::hands),
            Codec.BOOL.optionalFieldOf("ranged", false).forGetter(WeaponType::ranged)
    ).apply(i, WeaponType::new));
}
