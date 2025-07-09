package uz.jesko.anvildm;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uz.jesko.anvildm.utils.ConsoleMSG;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Config {
    
    private final AnvilDM plugin;
    private FileConfiguration config;

    private String currency;
    private String economyPlugin;
    private DonateMenu.MenuConfig menuConfig;

    private List<String> buyConfirm;
    private String buyYesBtn;
    private String buyNoBtn;
    private String buy;
    private List<String> buyNoMoney;
    private List<String> buyNoPlugin;
    private List<String> buyExist;
    private List<String> broadcast;
    
    public Config(AnvilDM plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() throws Exception {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
            ConsoleMSG.info("Default config.yml yaratildi");
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.contains("settings.currency") || !config.contains("settings.plugin")) {
            throw new IllegalStateException("Konfiguratsiyada kerakli maydonlar yo'q");
        }

        currency = config.getString("settings.currency", "so'm");
        economyPlugin = config.getString("settings.plugin", "Essentials");

        loadMenuConfig();
        loadMessages();
    }

    /* private void loadBasicSettings() {
        currency = config.getString("settings.currency", "so'm");
        economyPlugin = config.getString("settings.plugin", "none");
    } */

    private void loadMenuConfig() {
        ConfigurationSection menuSection = config.getConfigurationSection("menu");
        if (menuSection == null) {
            ConsoleMSG.menuConfigNotFound();
            return;
        }
        
        String title = menuSection.getString("title", "&3&lDonat Menyu");
        int size = menuSection.getInt("size", 3);

        //items
        Map<String, DonateMenu.DonateItem> items = new HashMap<>();
        ConfigurationSection itemsSection = menuSection.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection != null) {
                    DonateMenu.DonateItem item = loadDonateItem(key, itemSection, false);
                    items.put(key, item);
                }
            }
        }

        //donates
        Map<String, DonateMenu.DonateItem> donates = new HashMap<>();
        ConfigurationSection donatesSection = menuSection.getConfigurationSection("donates");
        if (donatesSection != null) {
            for (String key : donatesSection.getKeys(false)) {
                ConfigurationSection donateSection = donatesSection.getConfigurationSection(key);
                if (donateSection != null) {
                    DonateMenu.DonateItem donate = loadDonateItem(key, donateSection, true);
                    donates.put(key, donate);
                }
            }
        }
        
        //inventory
        List<String> inventoryLayout = menuSection.getStringList("inventory");

        menuConfig = new DonateMenu.MenuConfig(title, size, currency, economyPlugin, items, donates, inventoryLayout);
        
        ConsoleMSG.itemsLoaded(items.size(), donates.size());
    }

    private DonateMenu.DonateItem loadDonateItem(String key, ConfigurationSection section, boolean isDonate) {
        String id = section.getString("id", key);
        String group = section.getString("group", "");
        String material = section.getString("material", "STONE");
        String name = section.getString("name", "&7" + key);
        List<String> lore = section.getStringList("lore");
        double price = section.getDouble("price", 0.0);
        List<String> priceLore = section.getStringList("price-lore");
        
        return new DonateMenu.DonateItem(id, group, material, name, lore, price, priceLore, isDonate);
    }
    
    //messages
    private void loadMessages() {
        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        
        if (messagesSection == null) {
            ConsoleMSG.messagesNotFound();
            setDefaultMessages();
            return;
        }
        
        // Load all messages
        buyConfirm = messagesSection.getStringList("buy-confirm");
        buyYesBtn = messagesSection.getString("buy-yes-btn", "&#00ff00Tasdiqlayman");
        buyNoBtn = messagesSection.getString("buy-no-btn", "&#ff0000Bekor qilish");
        buy = messagesSection.getString("buy", "&fSiz &3{price} {currency} &fevaziga {donate} &7sotib oldingiz.");
        buyNoMoney = messagesSection.getStringList("buy-nomoney");
        buyNoPlugin = messagesSection.getStringList("buy-noplugin");
        buyExist = messagesSection.getStringList("buy-exist");
        broadcast = messagesSection.getStringList("broadcast");
        
        // Set defaults if any are empty
        if (buyConfirm.isEmpty()) {
            buyConfirm = Arrays.asList("&7Rostdan ham &3{price} {currency} &7evaziga {donate} &7sotib olmoqchimisiz?");
        }
        if (buyNoMoney.isEmpty()) {
            buyNoMoney = Arrays.asList("&r", "&fBalansingizda mablag' yetarli emas", "&7Balansingizni to'ldirish uchun: &3t.me/anvildev", "&r");
        }
        if (buyNoPlugin.isEmpty()) {
            buyNoPlugin = Arrays.asList("&r", "&7Donatni faqatgina do'konimizdan sotib ololasiz", "&7Do'kon manzili: &3t.me/anvildev", "&r");
        }
        if (buyExist.isEmpty()) {
            buyExist =  Arrays.asList("&r", "&7&7  Sizda allaqachon shu yoki shundan yuqori", "&7  darajali donate bor! &x&f&f&0&0&0&0Sotib ololmaysiz!", "&r");
        }
        if (broadcast.isEmpty()) {
            broadcast = Arrays.asList("&r", "&3╔═══════════════════════╗", "&3║", "&3║  &3{player} &7hozirgina {donate} &7sotib oldi!", "&3║  &7Donate sotib olish uchun: &3/donate", "&3║", "&3╚═══════════════════════╝", "&r");
        }
        
        ConsoleMSG.messagesLoaded();
    }
    
    //default messages
    private void setDefaultMessages() {
        buyConfirm = Arrays.asList("&7Rostdan ham &3{price} {currency} &7evaziga {donate} &7sotib olmoqchimisiz?");
        buyYesBtn = "&#00ff00Tasdiqlayman";
        buyNoBtn = "&#ff0000Bekor qilish";
        buy = "&fSiz &3{price} {currency} &fevaziga {donate} &7sotib oldingiz.";
        buyNoMoney = Arrays.asList("&r", "&fBalansingizda mablag' yetarli emas", "&7Balansingizni to'ldirish uchun: &3t.me/anvildev", "&r");
        buyNoPlugin = Arrays.asList("&r", "&7Donatni faqatgina do'konimizdan sotib ololasiz", "&7Do'kon manzili: &3t.me/anvildev", "&r");
        buyExist = Arrays.asList("&r", "&7&7  Sizda allaqachon shu yoki shundan yuqori", "&7  darajali donate bor! &x&f&f&0&0&0&0Sotib ololmaysiz!", "&r");
        broadcast = Arrays.asList("&r", "&3╔═══════════════════════╗", "&3║", "&3║  &3{player} &7hozirgina {donate} &7sotib oldi!", "&3║  &7Donate sotib olish uchun: &3/donate", "&3║", "&3╚═══════════════════════╝", "&r");
    }
    
    /*
    public String getMessage(String key) {
        switch (key.toLowerCase()) {
            case "buy": return buy;
            case "buy-yes-btn": return buyYesBtn;
            case "buy-no-btn": return buyNoBtn;
            default: return "&cMessage not found: " + key;
        }
    }*/
    
    /**
     * Get a list message by name (helper method)
     */
    public List<String> getMessageList(String key) {
        switch (key.toLowerCase()) {
            case "buy-confirm": return buyConfirm;
            case "buy-nomoney": return buyNoMoney;
            case "buy-noplugin": return buyNoPlugin;
            case "buy-exist": return buyExist;
            case "broadcast": return broadcast;
            default: return Arrays.asList("&cXabarlar ro'yxati topilmadi: " + key);
        }
    }
}