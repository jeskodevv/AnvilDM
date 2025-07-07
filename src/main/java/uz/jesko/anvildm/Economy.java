package uz.jesko.anvildm;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uz.jesko.anvildm.utils.ConsoleMSG;

import java.math.BigDecimal;

/**
 * Super simple economy manager class
 * Handles all economy stuff in one place - no fancy interfaces or multiple classes
 * Just simple methods that work with whatever economy plugin you have
 */
public class Economy {
    
    public String economyType;
    private Essentials essentials;
    private PlayerPointsAPI playerPointsAPI;
    
    /**
     * Setup economy based on what's configured
     * This is called when plugin starts up
     */
    public void setup(String configuredEconomy) {
        this.economyType = configuredEconomy.toLowerCase();
        
        switch (economyType) {
            case "essentials":
                if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
                    essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
                    ConsoleMSG.economyEssentials();
                } else {
                    economyType = "none";
                    ConsoleMSG.essentialsNotFound();
                }
                break;
                
            case "playerpoints":
                PlayerPoints ppPlugin = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
                if (ppPlugin != null) {
                    playerPointsAPI = ppPlugin.getAPI();
                    ConsoleMSG.economyPlayerPoints();
                } else {
                    economyType = "none";
                    ConsoleMSG.playerPointsNotFound();
                }
                break;
                
            default:
                economyType = "none";
                ConsoleMSG.economyDisabled();
                break;
        }
    }

    public boolean isEconomyAvailable() {
        return !economyType.equalsIgnoreCase("none");
    }

    public boolean isEnabled() {
        return !economyType.equals("none");
    }

    public double getBalance(Player player) {
        switch (economyType) {
            case "essentials":
                if (essentials != null) {
                    User user = essentials.getUser(player);
                    return user != null ? user.getMoney().doubleValue() : 0.0;
                }
                break;
                
            case "playerpoints":
                if (playerPointsAPI != null) {
                    return playerPointsAPI.look(player.getUniqueId());
                }
                break;
        }
        return 0.0;
    }

    public boolean hasEnoughMoney(Player player, double amount) {
        return getBalance(player) >= amount;
    }

    public boolean takeMoney(Player player, double amount) {
        switch (economyType) {
            case "essentials":
                if (essentials != null) {
                    User user = essentials.getUser(player);
                    if (user != null) {
                        try {
                            user.takeMoney(BigDecimal.valueOf(amount));
                            return true;
                        } catch (Exception e) {
                            ConsoleMSG.failedToTakeMoney(player.getName(), e.getMessage());
                        }
                    }
                }
                break;
                
            case "playerpoints":
                if (playerPointsAPI != null) {
                    return playerPointsAPI.take(player.getUniqueId(), (int) amount);
                }
                break;
        }
        return false;
    }

    public String formatBalance(double balance) {
        switch (economyType) {
            case "essentials":
                return String.format("%.2f", balance);
            case "playerpoints":
                return String.valueOf((int) balance);
            default:
                return "0";
        }
    }

    public String getCurrencyName() {
        switch (economyType) {
            case "essentials":
                return "money";
            case "playerpoints":
                return "points";
            default:
                return "currency";
        }
    }
}