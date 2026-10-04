package com.talesforge.nexusrpg.api.faction;

import com.talesforge.nexusrpg.api.rarity.Rarity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/** Faction. Datapack registry {@code nexusrpg:faction}. The relationship is one‑way: "how this faction relates to another". */
public record Faction(TextColor color,
                      ResourceLocation rarity,
                      Relation defaultRelation,
                      Map<ResourceLocation, Relation> relations) {
    public static final Codec<Faction> CODEC = RecordCodecBuilder.create(i -> i.group(
            TextColor.CODEC.optionalFieldOf("color", TextColor.fromRgb(0xFFFFFF)).forGetter(Faction::color),
            ResourceLocation.CODEC.optionalFieldOf("rarity", Rarity.COMMON).forGetter(Faction::rarity),
            Relation.CODEC.optionalFieldOf("default_relation", Relation.NEUTRAL).forGetter(Faction::defaultRelation),
            Codec.unboundedMap(ResourceLocation.CODEC, Relation.CODEC)
                    .optionalFieldOf("relations", Map.of()).forGetter(Faction::relations)
    ).apply(i, Faction::new));

    public Relation relationTo(ResourceLocation otherFaction) {
        return relations.getOrDefault(otherFaction, defaultRelation);
    }
}
