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

public class SpecialAxe implements Listener {

    private final SpecialTools plugin;
    private final Material toolMaterial;
    private final boolean glowing;
    private final String toolName;
    private final Set<Material> validBlocks;
    private final NamespacedKey key;

    public SpecialAxe(SpecialTools plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "special_axe");
        this.toolMaterial = Material.valueOf(plugin.getPluginConfig().getString("axe_type", "DIAMOND_AXE").toUpperCase());
        this.glowing = plugin.getPluginConfig().getBoolean("axe_glowing", true);
        this.toolName = plugin.getLang("axe_name");

        validBlocks = new HashSet<>();
        // Все виды деревьев | All LOG Types
        validBlocks.add(Material.OAK_LOG);
        validBlocks.add(Material.STRIPPED_OAK_LOG);
        validBlocks.add(Material.SPRUCE_LOG);
        validBlocks.add(Material.STRIPPED_SPRUCE_LOG);
        validBlocks.add(Material.BIRCH_LOG);
        validBlocks.add(Material.STRIPPED_BIRCH_LOG);
        validBlocks.add(Material.JUNGLE_LOG);
        validBlocks.add(Material.STRIPPED_JUNGLE_LOG);
        validBlocks.add(Material.ACACIA_LOG);
        validBlocks.add(Material.STRIPPED_ACACIA_LOG);
        validBlocks.add(Material.DARK_OAK_LOG);
        validBlocks.add(Material.STRIPPED_DARK_OAK_LOG);
        validBlocks.add(Material.MANGROVE_LOG);
        validBlocks.add(Material.STRIPPED_MANGROVE_LOG);
        validBlocks.add(Material.CHERRY_LOG);
        validBlocks.add(Material.STRIPPED_CHERRY_LOG);
        validBlocks.add(Material.PALE_OAK_LOG);
        validBlocks.add(Material.STRIPPED_PALE_OAK_LOG);

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        SpecialAxe axe = new SpecialAxe(plugin);
        if (!axe.isSpecialAxe(item)) {
        return; // Если это не специальная кирка — игнорируем
    }

        // permissions logic
        if (plugin.hasPermissions() && !player.hasPermission("specialtools.axe.use")) {
            player.sendMessage(plugin.getLang("permission_error"));
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        if (!validBlocks.contains(block.getType())) return;

        for (int y = -10; y <= 10; y++) {
            Block target = block.getRelative(0, y, 0);
            if (validBlocks.contains(target.getType())) {
                target.breakNaturally(item);
            }
        }
    }

    public ItemStack createAxe() {
        ItemStack axe = new ItemStack(toolMaterial);
        ItemMeta meta = axe.getItemMeta();
        meta.setDisplayName(toolName);

            // Используем getLangList для получения списка строк лора
        List<String> lore = plugin.getLangList("axe_lore");
        meta.setLore(lore);

        if (glowing) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.AQUA_AFFINITY, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }

        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        axe.setItemMeta(meta);
        return axe;
    }
    public boolean isSpecialAxe(ItemStack item) {
        if (item == null || item.getType() != toolMaterial) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
}
}
