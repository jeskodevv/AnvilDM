package uz.jesko.anvildm;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import uz.jesko.anvildm.utils.ColorUtils;
import uz.jesko.anvildm.utils.ConsoleMSG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AnvilDM extends JavaPlugin {

    @Getter
    private static AnvilDM instance;

    private Config config;
    public Config getFuckingErrorsConfig() {
        return config;
    }

    @Getter
    public Economy economy;

    private final Map<UUID, DonateMenu.DonateItem> pendingPurchases = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        ConsoleMSG.enable();

        saveDefaultConfig();
        config = new Config(this);

        try {
            config.loadConfig();
        } catch (Exception e) {
            ConsoleMSG.configError(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        economy = new Economy();
        String ecoPlugin = config.getEconomyPlugin();

        if (ecoPlugin == null || ecoPlugin.isEmpty()) {
            ConsoleMSG.error("Economy plugin konfigda ko'rsatilmagan!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        economy.setup(ecoPlugin);
        boolean economySetup = economy.isEconomyAvailable();
        ConsoleMSG.pluginEnabled(economySetup);

        if (!economySetup) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            getCommand("donate").setExecutor(new Commands());
        } catch (Exception e) {
            ConsoleMSG.error("Buyruqni ro'yxatdan o'tkazishda xato: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Show cool disable message
        ConsoleMSG.disable();
    }

    public void reloadConfiguration() {
        try {
            config.loadConfig();
            String ecoPlugin = config.getEconomyPlugin();

            if (ecoPlugin == null || ecoPlugin.isEmpty()) {
                ConsoleMSG.error("Economy plugin konfigda ko'rsatilmagan!");
                return;
            }

            economy.setup(ecoPlugin);
            ConsoleMSG.configReloaded();
        } catch (Exception e) {
            ConsoleMSG.error("Konfiguratsiyani qayta yuklashda xato: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setPendingPurchase(UUID playerUuid, DonateMenu.DonateItem item) {
        pendingPurchases.put(playerUuid, item);
    }

    public DonateMenu.DonateItem getPendingPurchase(UUID playerUuid) {
        return pendingPurchases.get(playerUuid);
    }

    public void clearPendingPurchase(UUID playerUuid) {
        pendingPurchases.remove(playerUuid);
    }

    public void completePurchase(Player player, DonateMenu.DonateItem item) {

        if (economy.isEnabled() && !economy.hasEnoughMoney(player, item.getPrice())) {
            List<String> messages = config.getMessageList("buy-nomoney");
            for (String message : messages) {
                player.sendMessage(ColorUtils.colorize(message));
            }
            return;
        }

        if (economy.isEnabled() && !economy.takeMoney(player, item.getPrice())) {
            player.sendMessage(ColorUtils.colorize("&cCouldn't take your money! Contact admin."));
            return;
        }

        String command = "lp user " + player.getName() + " parent add " + item.getGroup();

        Bukkit.getScheduler().runTask(this, () -> {
            boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            
            if (success) {

                String buyMessage = config.getBuy()
                        .replace("{price}", String.valueOf((int) item.getPrice()))
                        .replace("{currency}", config.getCurrency())
                        .replace("{donate}", ColorUtils.stripColor(item.getName()));
                
                player.sendMessage(ColorUtils.colorize(buyMessage));

                broadcastPurchase(player, item);
                
                ConsoleMSG.playerBought(player.getName(), ColorUtils.stripColor(item.getName()), item.getPrice(), config.getCurrency());
            } else {
                player.sendMessage(ColorUtils.colorize("&cRankni berib bolmadi, adminga murojat qiling."));
                ConsoleMSG.failedLuckPermsCommand(command);
            }
        });
    }

    private void broadcastPurchase(Player player, DonateMenu.DonateItem item) {
        List<String> broadcastMessages = config.getBroadcast();
        
        for (String message : broadcastMessages) {
            String processedMessage = message
                    .replace("{player}", player.getName())
                    .replace("{donate}", ColorUtils.stripColor(item.getName()));
            
            Bukkit.broadcastMessage(ColorUtils.colorize(processedMessage));
        }
    }
}