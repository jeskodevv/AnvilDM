package uz.jesko.anvildm.utils;

import org.bukkit.Bukkit;

public class ConsoleMSG {

    public static void color(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.colorize(message));
    }

    public static void enable() {
        color("&r");
        color("&3 █████╗ ███╗   ██╗██╗   ██╗██╗██╗     ██████╗ ███╗   ███╗");
        color("&3██╔══██╗████╗  ██║██║   ██║██║██║     ██╔══██╗████╗ ████║");
        color("&3███████║██╔██╗ ██║██║   ██║██║██║     ██║  ██║██╔████╔██║");
        color("&3██╔══██║██║╚██╗██║╚██╗ ██╔╝██║██║     ██║  ██║██║╚██╔╝██║");
        color("&3██║  ██║██║ ╚████║ ╚████╔╝ ██║███████╗██████╔╝██║ ╚═╝ ██║");
        color("&3╚═╝  ╚═╝╚═╝  ╚═══╝  ╚═══╝  ╚═╝╚══════╝╚═════╝ ╚═╝     ╚═╝");
        color("&fInfo: Donate Menyu uchun plugin");
        color("&fStatus: &x&0&0&f&f&0&0yondi");
        color("&fVersiya: &a${version}");
        color("&r");
        color("&7by jeskodev");
        color("&7join &3t.me/anvildev");
        color("&r");
    }

    public static void disable() {
        color("&r");
        color("&3 █████╗ ███╗   ██╗██╗   ██╗██╗██╗     ██████╗ ███╗   ███╗");
        color("&3██╔══██╗████╗  ██║██║   ██║██║██║     ██╔══██╗████╗ ████║");
        color("&3███████║██╔██╗ ██║██║   ██║██║██║     ██║  ██║██╔████╔██║");
        color("&3██╔══██║██║╚██╗██║╚██╗ ██╔╝██║██║     ██║  ██║██║╚██╔╝██║");
        color("&3██║  ██║██║ ╚████║ ╚████╔╝ ██║███████╗██████╔╝██║ ╚═╝ ██║");
        color("&3╚═╝  ╚═╝╚═╝  ╚═══╝  ╚═══╝  ╚═╝╚══════╝╚═════╝ ╚═╝     ╚═╝");
        color("&fInfo: Donate Menyu uchun plugin");
        color("&fStatus: &x&f&f&0&0&0&0o'chdi");
        color("&fVersiya: &a${version}");
        color("&r");
        color("&7by jeskodev");
        color("&7join &3t.me/anvildev");
        color("&r");
    }

    public static void pluginEnabled(boolean economyEnabled) {
        color("&aAnvilDM yondi! Plugin: " + (economyEnabled ? "&2ON" : "&cOFF"));
    }

    public static void configLoaded(String currency, String economyPlugin) {
        color("&aKonfiguratsiya yuklandi! Valyuta: &e" + currency + "&a, Plugin: &e" + economyPlugin);
    }

    public static void configError(String error) {
        color("&cKonfiguratsiyani yuklab bo'lmadi: " + error);
    }

    public static void itemsLoaded(int decorativeItems, int donateItems) {
        color("&aLoaded " + decorativeItems + " decorative items and " + donateItems + " donate items");
    }

    public static void messagesLoaded() {
        color("&aXabarlar muvaffaqiyatli yuklandi!");
    }

    public static void messagesNotFound() {
        color("&cXabarlar qismi topilmadi, default xabarlardan foydalanilyabdi!");
    }

    public static void menuConfigNotFound() {
        color("&cMenyu konfiguratsiyasi topilmadi!");
    }

    public static void configReloaded() {
        color("&aKonfiguratsiya qayta yuklandi!");
    }

    public static void playerReloadedConfig(String playerName) {
        color("&a" + playerName + " konfigni qayta yukladi");
    }

    public static void reloadError(String playerName, String error) {
        color("&c" + playerName +" nomli o'yinchidan qayta yuklashda xatolik: " + error);
    }

    public static void economyEssentials() {
        color("&aPlugin: EssentialsX ishlatilyabdi");
    }

    public static void economyPlayerPoints() {
        color("&aPlugin: PlayerPoints ishlatilyabdi");
    }

    public static void economyDisabled() {
        color("&7Plugin: O'chirilgan (faqatgina ko'rgazma uchun menyu)");
    }

    public static void essentialsNotFound() {
        color("&cPlugin: EssentialsX topilmadi!");
    }

    public static void playerPointsNotFound() {
        color("&cPlugin: PlayerPoints topilmadi!");
    }

    public static void failedToTakeMoney(String playerName, String error) {
        color("&c" + playerName + "nomli o'yinchidan pul olishda xatolik: " + error);
    }

    public static void playerBought(String playerName, String itemName, double price, String currency) {
        color("&3" + playerName + " &7hozirgina " + itemName + " &7ni &3" + price + " " + currency + " &7ga sotib oldi");
    }

    public static void failedLuckPermsCommand(String command) {
        color("&cLuckPerms buyrug'ini ishlatishda xatolik: " + command);
    }

    public static void menuError(String playerName, String error) {
        color("&c" + playerName + " nomli o'yinchida menyu bilan bog'liq xatolik: " + error);
    }

    public static void error(String message) {
        color("&c" + message);
    }

    public static void warning(String message) {
        color("&e" + message);
    }

    public static void info(String message) {
        color("&f" + message);
    }

    public static void success(String message) {
        color("&a" + message);
    }
}