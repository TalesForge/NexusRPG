package com.talesforge.nexusrpg.api.faction;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum Relation implements StringRepresentable {
    ALLY("ally"), NEUTRAL("neutral"), ENEMY("enemy");

    public static final Codec<Relation> CODEC = StringRepresentable.fromEnum(Relation::values);
    private final String name;

    Relation(String name) { this.name = name; }

    @Override
    public String getSerializedName() { return name; }
}
