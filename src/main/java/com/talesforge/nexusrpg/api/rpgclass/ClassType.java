package com.talesforge.nexusrpg.api.rpgclass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.TextColor;

/** Class type (combat, social, etc.). Data pack registry {@code nexusrpg:class_type}. */
public record ClassType(TextColor color) {
    public static final Codec<ClassType> CODEC = RecordCodecBuilder.create(i -> i.group(
            TextColor.CODEC.optionalFieldOf("color", TextColor.fromRgb(0xFFFFFF)).forGetter(ClassType::color)
    ).apply(i, ClassType::new));
}
