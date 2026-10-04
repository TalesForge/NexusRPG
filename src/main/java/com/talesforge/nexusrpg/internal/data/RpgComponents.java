package com.talesforge.nexusrpg.internal.data;

import com.talesforge.nexusrpg.NexusRPG;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RpgComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, NexusRPG.MOD_ID);

    /** Item rarity: id from the datapack registry {@code nexusrpg:rarity}. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> RARITY =
            COMPONENTS.register("rarity", () -> DataComponentType.<ResourceLocation>builder()
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC)
                    .build());

    private RpgComponents() {}
}
