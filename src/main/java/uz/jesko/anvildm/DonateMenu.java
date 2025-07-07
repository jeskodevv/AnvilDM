package uz.jesko.anvildm;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uz.jesko.anvildm.utils.ColorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonateMenu implements Listener {
    
    private final Player player;
    private final Inventory inventory;
    private final Map<Integer, DonateItem> slotItems;
    
    public DonateMenu(Player player) {
        this.player = player;
        this.slotItems = new HashMap<>();

        Config config = AnvilDM.getInstance().getFuckingErrorsConfig();

        String title = ColorUtils.colorize(config.getMenuConfig().getTitle());
        int slots = config.getMenuConfig().getSlots();
        this.inventory = Bukkit.createInventory(null, slots, title);

        setupInventory();

        Bukkit.getPluginManager().registerEvents(this, AnvilDM.getInstance());
    }

    private void setupInventory() {
        Config config = AnvilDM.getInstance().getFuckingErrorsConfig();
        MenuConfig menuConfig = config.getMenuConfig();
        List<String> layout = menuConfig.getInventoryLayout();

        for (int row = 0; row < layout.size() && row < menuConfig.getSize(); row++) {
            String rowLayout = layout.get(row);
            String[] slots = rowLayout.split(" ");

            for (int col = 0; col < slots.length && col < 9; col++) {
                int slot = row * 9 + col;
                String itemId = slots[col].trim();
                
                if (itemId.isEmpty() || itemId.equals(" ")) continue;

                DonateItem item = findItemById(itemId, menuConfig);
                if (item != null) {
                    ItemStack itemStack = createItemStack(item);
                    inventory.setItem(slot, itemStack);
                    slotItems.put(slot, item);
                }
            }
        }
    }
    
    //find ids
    private DonateItem findItemById(String id, MenuConfig menuConfig) {
        for (DonateItem item : menuConfig.getItems().values()) {
            if (item.getId().equals(id)) {
                return item;
            }
        }

        for (DonateItem item : menuConfig.getDonates().values()) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        
        return null;
    }

    private ItemStack createItemStack(DonateItem item) {
        Material material;
        try {
            material = Material.valueOf(item.getMaterial().toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.STONE;
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        
        if (meta != null) {
            //name
            String name = ColorUtils.colorize(item.getName().replace("%player%", player.getName()));
            meta.setDisplayName(name);

            //lore
            List<String> lore = new ArrayList<>();

            for (String line : item.getLore()) {
                lore.add(ColorUtils.colorize(line.replace("%player%", player.getName())));
            }

            if (item.isPurchasable() && AnvilDM.getInstance().getEconomy().isEnabled()) {
                for (String line : item.getPriceLore()) {
                    String processedLine = line
                            .replace("{price}", String.valueOf((int) item.getPrice()))
                            .replace("{currency}", AnvilDM.getInstance().getFuckingErrorsConfig().getCurrency())
                            .replace("{balance}", getPlayerBalance());
                    lore.add(ColorUtils.colorize(processedLine));
                }
            }
            
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        
        return itemStack;
    }
    
    //balance
    private String getPlayerBalance() {
        if (!AnvilDM.getInstance().getEconomy().isEnabled()) {
            return "0";
        }
        
        double balance = AnvilDM.getInstance().getEconomy().getBalance(player);
        return AnvilDM.getInstance().getEconomy().formatBalance(balance);
    }
    
    //menu
    public void open() {
        player.openInventory(inventory);
    }
    
    //click handler
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        event.setCancelled(true); //dont take item
        
        Player clickedPlayer = (Player) event.getWhoClicked();
        if (!clickedPlayer.equals(player)) return;
        
        int slot = event.getSlot();
        DonateItem item = slotItems.get(slot);

        if (item == null || !item.isDonate()) return;
        
        handleDonateClick(item);
    }
    
    //click handler but this time its for donate items
    private void handleDonateClick(DonateItem item) {
        if (!AnvilDM.getInstance().getEconomy().isEnabled()) {
            List<String> messages = AnvilDM.getInstance().getFuckingErrorsConfig().getMessageList("buy-noplugin");
            for (String message : messages) {
                player.sendMessage(ColorUtils.colorize(message));
            }
            return;
        }

        if (!AnvilDM.getInstance().getEconomy().hasEnoughMoney(player, item.getPrice())) {
            List<String> messages = AnvilDM.getInstance().getFuckingErrorsConfig().getMessageList("buy-nomoney");
            for (String message : messages) {
                player.sendMessage(ColorUtils.colorize(message));
            }
            return;
        }

        AnvilDM.getInstance().setPendingPurchase(player.getUniqueId(), item);
        sendConfirmationMessage(item);

        player.closeInventory();
    }
    
    //confirm
    private void sendConfirmationMessage(DonateItem item) {
        Config config = AnvilDM.getInstance().getFuckingErrorsConfig();

        StringBuilder confirmText = new StringBuilder();
        for (String line : config.getBuyConfirm()) {
            String processedLine = line
                    .replace("{price}", String.valueOf((int) item.getPrice()))
                    .replace("{currency}", config.getCurrency())
                    .replace("{donate}", ColorUtils.stripColor(item.getName()));
            confirmText.append(processedLine).append("\n");
        }

        TextComponent mainMessage = new TextComponent(ColorUtils.colorize(confirmText.toString()));
        
        //yes button
        TextComponent yesButton = new TextComponent(ColorUtils.colorize(config.getBuyYesBtn()));
        yesButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/donate tasdiqlash"));
        yesButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(ColorUtils.colorize("&aSotib olish uchun bosing")).create()));

        TextComponent separator = new TextComponent(ColorUtils.colorize("      &7|      "));
        
        //no button
        TextComponent noButton = new TextComponent(ColorUtils.colorize(config.getBuyNoBtn()));
        noButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/donate bekorqilish"));
        noButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(ColorUtils.colorize("&cClick to cancel")).create()));

        mainMessage.addExtra("\n");
        mainMessage.addExtra(yesButton);
        mainMessage.addExtra(separator);
        mainMessage.addExtra(noButton);
        
        //send
        player.spigot().sendMessage(mainMessage);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);
        }
    }

    public static class DonateItem {
        private final String id;
        private final String group;
        private final String material;
        private final String name;
        private final List<String> lore;
        private final double price;
        private final List<String> priceLore;
        private final boolean isDonate;
        
        public DonateItem(String id, String group, String material, String name, 
                         List<String> lore, double price, List<String> priceLore, boolean isDonate) {
            this.id = id;
            this.group = group;
            this.material = material;
            this.name = name;
            this.lore = lore;
            this.price = price;
            this.priceLore = priceLore;
            this.isDonate = isDonate;
        }

        public String getId() { return id; }
        public String getGroup() { return group; }
        public String getMaterial() { return material; }
        public String getName() { return name; }
        public List<String> getLore() { return lore; }
        public double getPrice() { return price; }
        public List<String> getPriceLore() { return priceLore; }
        public boolean isDonate() { return isDonate; }
        public boolean isPurchasable() { return isDonate && price > 0; }
    }

    public static class MenuConfig {
        private final String title;
        private final int size;
        private final String currency;
        private final String economyPlugin;
        private final Map<String, DonateItem> items;
        private final Map<String, DonateItem> donates;
        private final List<String> inventoryLayout;
        
        public MenuConfig(String title, int size, String currency, String economyPlugin,
                         Map<String, DonateItem> items, Map<String, DonateItem> donates,
                         List<String> inventoryLayout) {
            this.title = title;
            this.size = size;
            this.currency = currency;
            this.economyPlugin = economyPlugin;
            this.items = items;
            this.donates = donates;
            this.inventoryLayout = inventoryLayout;
        }

        public String getTitle() { return title; }
        public int getSize() { return size; }
        public int getSlots() { return size * 9; }
        public String getCurrency() { return currency; }
        public String getEconomyPlugin() { return economyPlugin; }
        public Map<String, DonateItem> getItems() { return items; }
        public Map<String, DonateItem> getDonates() { return donates; }
        public List<String> getInventoryLayout() { return inventoryLayout; }
    }
}