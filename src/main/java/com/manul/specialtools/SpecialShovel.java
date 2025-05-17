package com.manul.specialtools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SpecialShovel implements Listener {

    private final SpecialTools plugin;
    private final Material toolMaterial;
    private final boolean glowing;
    private final String toolName;
    private final Set<Material> validBlocks;
    private final NamespacedKey key;

    public SpecialShovel(SpecialTools plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "special_shovel");
        this.toolMaterial = Material.valueOf(plugin.getPluginConfig().getString("shovel_type", "DIAMOND_SHOVEL").toUpperCase());
        this.glowing = plugin.getPluginConfig().getBoolean("shovel_glowing", true);
        this.toolName = plugin.getLang("shovel_name");

        validBlocks = new HashSet<>();
        // Внешние блоки
        validBlocks.add(Material.GRASS_BLOCK);
        validBlocks.add(Material.PODZOL);
        validBlocks.add(Material.MYCELIUM);
        validBlocks.add(Material.DIRT_PATH);
        validBlocks.add(Material.SAND);
        validBlocks.add(Material.RED_SAND);
        validBlocks.add(Material.SNOW_BLOCK);
        // Чуть ниже внешних
        validBlocks.add(Material.DIRT);
        validBlocks.add(Material.COARSE_DIRT);
        validBlocks.add(Material.ROOTED_DIRT);
        validBlocks.add(Material.MOSS_BLOCK);
        // Блоки, которы чаще всего под землей
        validBlocks.add(Material.GRAVEL);
        validBlocks.add(Material.CLAY);
        validBlocks.add(Material.MUD);
        validBlocks.add(Material.PALE_MOSS_BLOCK);

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        SpecialShovel shovel = new SpecialShovel(plugin);
        if (!shovel.isSpecialShovel(item)) {
        return; // Если это не специальная кирка — игнорируем
    }
        // permission logic
        if (plugin.hasPermissions() && !player.hasPermission("specialtools.shovel.use")) {
            player.sendMessage(plugin.getLang("permission_error"));
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        if (!validBlocks.contains(block.getType())) return;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block target = block.getRelative(x, y, z);
                    if (validBlocks.contains(target.getType())) {
                        target.breakNaturally(item);
                    }
                }
            }
        }
    }

    public ItemStack createShovel() {
        ItemStack shovel = new ItemStack(toolMaterial);
        ItemMeta meta = shovel.getItemMeta();
        meta.setDisplayName(toolName);

            // Используем getLangList для получения списка строк лора
        List<String> lore = plugin.getLangList("shovel_lore");
        meta.setLore(lore);

        if (glowing) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.AQUA_AFFINITY, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }

        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        shovel.setItemMeta(meta);
        return shovel;
    }
    public boolean isSpecialShovel(ItemStack item) {
        if (item == null || item.getType() != toolMaterial) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
}
}
