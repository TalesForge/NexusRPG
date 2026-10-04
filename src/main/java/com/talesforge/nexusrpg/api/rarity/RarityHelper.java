package com.talesforge.nexusrpg.api.rarity;

import com.talesforge.nexusrpg.api.NexusRPGApi;
import com.talesforge.nexusrpg.internal.data.RpgComponents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class RarityHelper {
    /** Item rarity (data component {@code nexusrpg:rarity}); by default {@link Rarity#COMMON}. */
    public static ResourceLocation of(ItemStack stack) {
        return stack.getOrDefault(RpgComponents.RARITY.get(), Rarity.COMMON);
    }

    public static void set(ItemStack stack, ResourceLocation rarity) {
        stack.set(RpgComponents.RARITY.get(), rarity);
    }

    public static Optional<Rarity> get(RegistryAccess access, ResourceLocation id) {
        return NexusRPGApi.rarity(access, id);
    }

    /** Localized, colored name. Key: {@code rarity.<namespace>.<path>}. */
    public static MutableComponent displayName(RegistryAccess access, ResourceLocation id) {
        MutableComponent c = Component.translatable("rarity." + id.getNamespace() + "." + id.getPath());
        get(access, id).ifPresent(r -> c.withStyle(Style.EMPTY.withColor(r.color())));
        return c;
    }

    private RarityHelper() {}
}
