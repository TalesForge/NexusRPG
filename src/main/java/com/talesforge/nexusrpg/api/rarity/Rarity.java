package com.talesforge.nexusrpg.api.rarity;

import com.talesforge.nexusrpg.NexusRPG;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

/** Rarity. Datapack registry {@code nexusrpg:rarity}. You need to refer to it using {@link ResourceLocation}. */
public record Rarity(TextColor color, int weight, int order) {
    public static final ResourceLocation COMMON = NexusRPG.id("common");

    public static final Codec<Rarity> CODEC = RecordCodecBuilder.create(i -> i.group(
            TextColor.CODEC.fieldOf("color").forGetter(Rarity::color),
            Codec.INT.optionalFieldOf("weight", 100).forGetter(Rarity::weight),
            Codec.INT.optionalFieldOf("order", 0).forGetter(Rarity::order)
    ).apply(i, Rarity::new));
}
