package com.talesforge.nexusrpg.api.profile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.talesforge.nexusrpg.NexusRPG;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Faction and classes that every entity of a given type has "by default", until they are explicitly changed.
 * Nothing is written to the entity: the values are looked up from this data map on every read,
 * so it works for new AND already existing entities, and edits to the JSON apply to everyone.
 * <p>
 * File: {@code data/nexusrpg/data_maps/entity_type/default_profile.json} (other mods add to the same path).
 */
public record DefaultProfile(Optional<ResourceLocation> faction, List<ResourceLocation> classes) {
    public static final Codec<DefaultProfile> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.optionalFieldOf("faction").forGetter(DefaultProfile::faction),
            ResourceLocation.CODEC.listOf().optionalFieldOf("classes", List.of()).forGetter(DefaultProfile::classes)
    ).apply(i, DefaultProfile::new));

    /** Synced to clients (not mandatory), so the client resolves the same defaults as the server. */
    public static final DataMapType<EntityType<?>, DefaultProfile> ENTITY_DEFAULT_PROFILE =
            DataMapType.builder(NexusRPG.id("default_profile"), Registries.ENTITY_TYPE, CODEC)
                    .synced(CODEC, false)
                    .build();

    /** @return the default profile of the entity type, or null if none is defined. */
    public static @Nullable DefaultProfile of(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getResourceKey(type)
                .map(key -> BuiltInRegistries.ENTITY_TYPE.getData(ENTITY_DEFAULT_PROFILE, key))
                .orElse(null);
    }
}
