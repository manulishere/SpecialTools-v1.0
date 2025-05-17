package com.manul.specialtools;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class SpecialTools extends JavaPlugin {

    private FileConfiguration config;
    private FileConfiguration langConfig;
    private String language;
    private SpecialPickAxe specialPickAxe;
    private SpecialShovel specialShovel;
    private SpecialAxe specialAxe;

    @Override
    public void onEnable() {
        // ANCII текст при запуске плагина
        // Цвета
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String GREEN = "\u001B[32m";
        final String YELLOW = "\u001B[33m";
        final String CYAN = "\u001B[36m";
        final String WHITE = "\u001B[37m";

        //Текст
        getLogger().info(WHITE + "" + RESET);
        getLogger().info(CYAN + "===================================" + RESET);
        getLogger().info(RED + " Special" + WHITE + "Tools " + GREEN + "Enabled " + RESET);
        getLogger().info(YELLOW + " Version" + CYAN + " 1.0 " + RESET);
        getLogger().info(YELLOW + " PaperMC" + CYAN + " 1.21+ " + RESET);
        getLogger().info(CYAN + "===================================" + RESET);
        getLogger().info(WHITE + "" + RESET);

        // Сохраняем дефолтный config.yml, если его нет
        saveDefaultConfig();
        config = getConfig();

        // Читаем язык из конфига
        language = config.getString("language", "en_US");

        // Загружаем языковой файл
        loadLanguageFiles();

        // Регистрируем слушателей событий
        getServer().getPluginManager().registerEvents(new SpecialPickAxe(this), this);
        getServer().getPluginManager().registerEvents(new SpecialShovel(this), this);
        getServer().getPluginManager().registerEvents(new SpecialAxe(this), this);

        // Регистрируем команды и таб-комплитер
        PluginCommands commands = new PluginCommands(this);
        getCommand("specialtools").setExecutor(commands);
        getCommand("specialtools").setTabCompleter(commands);

        getLogger().info("SpecialTools enabled. Language: " + language);
        reloadPluginConfigAndListeners();
    }

    public void reloadPluginConfigAndListeners() {
        reloadConfig();
        config = getConfig();

        language = config.getString("language", "en_US");
        loadLanguageFiles();

    // Если слушатели уже созданы, отменяем их регистрацию
        if (specialPickAxe != null) {
        HandlerList.unregisterAll(specialPickAxe);
    }
        if (specialShovel != null) {
        HandlerList.unregisterAll(specialShovel);
    }
        if (specialAxe != null) {
        HandlerList.unregisterAll(specialAxe);
    }

    // Создаем новые экземпляры слушателей с актуальными параметрами
    specialPickAxe = new SpecialPickAxe(this);
    specialShovel = new SpecialShovel(this);
    specialAxe = new SpecialAxe(this);

    // Регистрируем слушателей заново
    getServer().getPluginManager().registerEvents(specialPickAxe, this);
    getServer().getPluginManager().registerEvents(specialShovel, this);
    getServer().getPluginManager().registerEvents(specialAxe, this);
}

    /**
     * Загружает языковой файл из папки Languages/<language>.yml.
     * Если файла нет, копирует дефолтные языковые файлы из ресурсов.
     */
    private void loadLanguageFiles() {
        File langFolder = new File(getDataFolder(), "Languages");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
            // Копируем дефолтные языковые файлы из ресурсов
            saveResource("Languages/ru_RU.yml", false);
            saveResource("Languages/en_US.yml", false);
        }

        File langFile = new File(langFolder, language + ".yml");
        if (!langFile.exists()) {
            getLogger().warning("Language file " + language + ".yml not found! Using en_US.yml");
            langFile = new File(langFolder, "en_US.yml");
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    /**
     * Получить строку из языкового файла по ключу.
     * Если ключ не найден, возвращает сам ключ.
     * Заменяет символы & на § для цветовых кодов Minecraft.
     *
     * @param key ключ в языковом файле
     * @return переведённая строка с цветами
     */
    public String getLang(String key) {
        if (langConfig == null) return key;
        return langConfig.getString(key, key).replace('&', '§');
    }

    /**
     * Получить список строк (например, лор) из языкового файла по ключу.
     * Если ключ не найден, возвращает список с одним элементом - самим ключом.
     * Заменяет символы & на § для цветовых кодов Minecraft.
     *
     * @param key ключ в языковом файле
     * @return список переведённых строк с цветами
     */
    public List<String> getLangList(String key) {
        if (langConfig == null) return List.of(key);
        List<String> list = langConfig.getStringList(key);
        return list.stream().map(s -> s.replace('&', '§')).toList();
    }

    /**
     * Получить конфигурацию плагина.
     *
     * @return объект FileConfiguration с настройками
     */
    public FileConfiguration getPluginConfig() {
        return config;
    }

    /**
     * Проверить, включена ли проверка прав в конфиге.
     *
     * @return true, если проверка прав включена
     */
    public boolean hasPermissions() {
        return config.getBoolean("permissions", true);
    }

    @Override
    public void onDisable() {
        // ANCII текст при выключении плагина
        // Цвета
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String YELLOW = "\u001B[33m";
        final String CYAN = "\u001B[36m";
        final String WHITE = "\u001B[37m";

        //Текст
        getLogger().info(WHITE + "" + RESET);
        getLogger().info(CYAN + "===================================" + RESET);
        getLogger().info(RED + " Special" + WHITE + "Tools " + RED + "Disabled " + RESET);
        getLogger().info(YELLOW + " Version" + CYAN + " 1.0 " + RESET);
        getLogger().info(YELLOW + " PaperMC" + CYAN + " 1.21+ " + RESET);
        getLogger().info(CYAN + "===================================" + RESET);
        getLogger().info(WHITE + "" + RESET);
    }
}
