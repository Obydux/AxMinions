package com.artillexstudios.axminions.integrations.implementation.block;

import com.artillexstudios.axminions.integrations.Integration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public final class BlockIntegration extends Integration<BlockIntegrable> {

    public BlockIntegration() {
        this.register(new DefaultBlockIntegrable());

        if (Bukkit.getPluginManager().getPlugin("KGenerators") != null) {
            this.register(new KGeneratorsIntegrable());
        }
    }

    public Collection<ItemStack> lootAndBreak(Location location, ItemStack itemStack) {
        for (BlockIntegrable integration : this.integrations()) {
            Collection<ItemStack> items = integration.lootAndBreak(location, itemStack);
            if (items == null) {
                return null;
            }

            if (items.isEmpty()) {
                continue;
            }

            return items;
        }

        return List.of();
    }
}
