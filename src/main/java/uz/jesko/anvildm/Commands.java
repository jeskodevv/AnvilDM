package uz.jesko.anvildm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uz.jesko.anvildm.utils.ColorUtils;
import uz.jesko.anvildm.utils.ConsoleMSG;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String commandName = command.getName().toLowerCase();
        
        switch (commandName) {
            case "donate":
            case "donat":
                return handleDonateCommand(sender, args);

            default:
                return false;
        }
    }
    
    //command handler
    private boolean handleDonateCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtils.colorize("&cBuyruqdan faqatgina o'yinchilar foydalana oladi!"));
            return true;
        }
        
        Player player = (Player) sender;
        
        //permission checker
        if (!player.hasPermission("anvildm.use")) {
            player.sendMessage(ColorUtils.colorize("&cSizda ushubu buyruq uchun ruxsat yo'q!"));
            return true;
        }
        
        //subcommand handler
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "reload":
                    return handleReload(player);
                    
                case "tasdiqlash":
                case "confirm":
                case "yes":
                    return handleConfirm(player);
                    
                case "bekorqilish":
                case "cancel":
                case "no":
                    return handleCancel(player);
                    
                case "help":
                case "?":
                    return handleHelp(player);
                    
                default:
                    break;
            }
        }

        openDonateMenu(player);
        return true;
    }
    
    //reload
    private boolean handleReload(Player player) {
        if (!player.hasPermission("anvildm.admin")) {
            player.sendMessage(ColorUtils.colorize("&cSizda ushbu buyruqdan foydalanish uchun ruxsat yo'q!"));
            return true;
        }
        
        try {
            AnvilDM.getInstance().reloadConfiguration();
            player.sendMessage(ColorUtils.colorize("&aKonfiguratsiya qayta yuklandi!"));
            ConsoleMSG.playerReloadedConfig(player.getName());
        } catch (Exception e) {
            player.sendMessage(ColorUtils.colorize("&cQayta yuklashda xatolik: " + e.getMessage()));
            ConsoleMSG.reloadError(player.getName(), e.getMessage());
        }
        
        return true;
    }
    
    //confirm
    private boolean handleConfirm(Player player) {
        DonateMenu.DonateItem item = AnvilDM.getInstance().getPendingPurchase(player.getUniqueId());
        
        if (item == null) {
            player.sendMessage(ColorUtils.colorize("&cHech narsa xarid qilmayabsiz, avval /donate buyrug'idan foydalaning."));
            return true;
        }

        //prevent double-purchasing
        AnvilDM.getInstance().clearPendingPurchase(player.getUniqueId());
        
        //buy
        AnvilDM.getInstance().completePurchase(player, item);
        
        return true;
    }
    
    //cancel
    private boolean handleCancel(Player player) {
        DonateMenu.DonateItem item = AnvilDM.getInstance().getPendingPurchase(player.getUniqueId());
        
        if (item == null) {
            player.sendMessage(ColorUtils.colorize("&cBekor qilish uchun hech hnarsa yo'q."));
            return true;
        }
        
        // Clear it
        AnvilDM.getInstance().clearPendingPurchase(player.getUniqueId());
        player.sendMessage(ColorUtils.colorize("&7Xarid bekor qilindi."));
        
        return true;
    }
    
    //help
    private boolean handleHelp(Player player) {
        player.sendMessage(ColorUtils.colorize("&3&lAnvilDM buyruqlari:"));
        player.sendMessage(ColorUtils.colorize("&7/donate &f- donat menyuni ochish"));
        player.sendMessage(ColorUtils.colorize("&7/donate tasdiqlash &f- xaridni tasdiqlash"));
        player.sendMessage(ColorUtils.colorize("&7/donate bekorqilish &f- xaridni bekor qilish"));
        
        if (player.hasPermission("anvildm.admin")) {
            player.sendMessage(ColorUtils.colorize("&7/donate reload &f- konfiguratsiyani qayta yuklash"));
        }
        
        return true;
    }

    private void openDonateMenu(Player player) {
        try {
            DonateMenu menu = new DonateMenu(player);
            menu.open();
        } catch (Exception e) {
            player.sendMessage(ColorUtils.colorize("&cMenyuni ochishda xatolik! Admin bilan bog'laning."));
            ConsoleMSG.menuError(player.getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}