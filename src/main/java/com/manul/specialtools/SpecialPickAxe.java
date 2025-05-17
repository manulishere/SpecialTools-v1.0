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

public class SpecialPickAxe implements Listener {

    private final SpecialTools plugin;
    private final Material toolMaterial;
    private final boolean glowing;
    private final String toolName;
    private final Set<Material> validBlocks;
    private final NamespacedKey key;

    public SpecialPickAxe(SpecialTools plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "special_pickaxe");
        this.toolMaterial = Material.valueOf(plugin.getPluginConfig().getString("pickaxe_type", "DIAMOND_PICKAXE").toUpperCase());
        this.glowing = plugin.getPluginConfig().getBoolean("pickaxe_glowing", true);
        this.toolName = plugin.getLang("pickaxe_name");

        // Обычные блоки | Default blocks
        validBlocks = new HashSet<>();
        validBlocks.add(Material.STONE);
        validBlocks.add(Material.SANDSTONE);
        validBlocks.add(Material.RED_SANDSTONE);
        validBlocks.add(Material.DEEPSLATE);
        validBlocks.add(Material.GRANITE);
        validBlocks.add(Material.DIORITE);
        validBlocks.add(Material.ANDESITE);
        validBlocks.add(Material.CALCITE);
        validBlocks.add(Material.PRISMARINE);
        validBlocks.add(Material.COBBLESTONE);
        validBlocks.add(Material.OBSIDIAN);
        validBlocks.add(Material.NETHERRACK);
        validBlocks.add(Material.BLACKSTONE);
        validBlocks.add(Material.END_STONE);
        validBlocks.add(Material.AMETHYST_BLOCK);
        validBlocks.add(Material.DEAD_TUBE_CORAL_BLOCK);
        validBlocks.add(Material.TUFF);
        // Какие то левые блоки, но пусть будут | other blocks
        validBlocks.add(Material.MOSSY_COBBLESTONE);
        validBlocks.add(Material.STONE_BRICKS);
        validBlocks.add(Material.CHISELED_STONE_BRICKS);
        validBlocks.add(Material.MOSSY_STONE_BRICKS);
        validBlocks.add(Material.DEEPSLATE_TILES);
        // Бетоны | Concrete
        validBlocks.add(Material.WHITE_CONCRETE);
        validBlocks.add(Material.LIGHT_GRAY_CONCRETE);
        validBlocks.add(Material.GRAY_CONCRETE);
        validBlocks.add(Material.BLACK_CONCRETE);
        validBlocks.add(Material.BROWN_CONCRETE);
        validBlocks.add(Material.RED_CONCRETE);
        validBlocks.add(Material.ORANGE_CONCRETE);
        validBlocks.add(Material.YELLOW_CONCRETE);
        validBlocks.add(Material.LIME_CONCRETE);
        validBlocks.add(Material.GREEN_CONCRETE);
        validBlocks.add(Material.CYAN_CONCRETE);
        validBlocks.add(Material.LIGHT_BLUE_CONCRETE);
        validBlocks.add(Material.BLUE_CONCRETE);
        validBlocks.add(Material.PURPLE_CONCRETE);
        validBlocks.add(Material.MAGENTA_CONCRETE);
        validBlocks.add(Material.PINK_CONCRETE);
        // Терракота и керамика | Terracotta
        validBlocks.add(Material.TERRACOTTA);
        validBlocks.add(Material.LIGHT_GRAY_TERRACOTTA);
        validBlocks.add(Material.GRAY_TERRACOTTA);
        validBlocks.add(Material.BLACK_TERRACOTTA);
        validBlocks.add(Material.BROWN_TERRACOTTA);
        validBlocks.add(Material.RED_TERRACOTTA);
        validBlocks.add(Material.ORANGE_TERRACOTTA);
        validBlocks.add(Material.YELLOW_TERRACOTTA);
        validBlocks.add(Material.LIME_TERRACOTTA);

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        SpecialPickAxe pickaxe = new SpecialPickAxe(plugin);
        if (!pickaxe.isSpecialPickaxe(item)) {
        return; // Если это не специальная кирка — игнорируем
    }

        if (item == null || item.getType() != toolMaterial) return;
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        if (plugin.hasPermissions() && !player.hasPermission("specialtools.pickaxe.use")) {
            player.sendMessage(plugin.getLang("permission_error"));
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        if (!validBlocks.contains(block.getType())) return;

        // Радиус 3x3x3 вокруг блока
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

    // Метод для создания предмета с названием, лором и свечением
    public ItemStack createPickaxe() {
        ItemStack pickaxe = new ItemStack(toolMaterial);
        ItemMeta meta = pickaxe.getItemMeta();
        meta.setDisplayName(toolName);
        
        // Используем getLangList для получения списка строк лора
        List<String> lore = plugin.getLangList("pickaxe_lore");
        meta.setLore(lore);

        if (glowing) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        pickaxe.setItemMeta(meta);
        return pickaxe;
    }
    public boolean isSpecialPickaxe(ItemStack item) {
        if (item == null || item.getType() != toolMaterial) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
}
}
