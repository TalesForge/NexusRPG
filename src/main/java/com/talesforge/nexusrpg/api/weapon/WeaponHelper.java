package com.talesforge.nexusrpg.api.weapon;

import com.talesforge.nexusrpg.NexusRPG;
import com.talesforge.nexusrpg.api.NexusRPGApi;
import com.talesforge.nexusrpg.api.rpgclass.RpgClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.Optional;

public final class WeaponHelper {
    /**
     * Data map of items: {@code data/<ns>/data_maps/item/weapon_type.json}
     * (in our case {@code data/nexusrpg/data_maps/item/weapon_type.json}).
     * The value is the id {@link WeaponType}.
     */
    public static final DataMapType<Item, ResourceLocation> ITEM_WEAPON_TYPE =
            DataMapType.builder(NexusRPG.id("weapon_type"), Registries.ITEM, ResourceLocation.CODEC)
                    .synced(ResourceLocation.CODEC, false)
                    .build();

    public static Optional<ResourceLocation> typeOf(ItemStack stack) {
        return Optional.ofNullable(stack.getItemHolder().getData(ITEM_WEAPON_TYPE));
    }

    /**
     * Can an entity use an item according to class restrictions?
     * It doesn’t automatically prohibit anything — the decision (to cancel an attack, to remove an item) remains with the mod.
     */
    public static boolean canWield(LivingEntity entity, ItemStack stack) {
        Optional<ResourceLocation> type = typeOf(stack);
        if (type.isEmpty()) return true;

        boolean restricted = false;
        for (ResourceLocation classId : NexusRPGApi.profiles().classes(entity)) {
            Optional<RpgClass> c = NexusRPGApi.rpgClass(entity.level().registryAccess(), classId);
            if (c.isEmpty() || c.get().weaponTypes().isEmpty()) return true;  // Class without restrictions
            restricted = true;
            if (c.get().weaponTypes().contains(type.get())) return true;
        }
        return !restricted;
    }

    private WeaponHelper() {}
}
