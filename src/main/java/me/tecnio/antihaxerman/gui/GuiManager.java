package me.tecnio.antihaxerman.gui;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.listener.bukkit.BukkitEventManager;
import me.tecnio.antihaxerman.manager.CheckManager;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class GuiManager implements Listener {

    public Inventory MainMenu = Bukkit.createInventory(null, 3 * 9, ChatColor.GOLD + "Home");

    public HashMap<Integer, Inventory> Checks = new HashMap<>();
    public HashMap<Integer, Inventory> SussyPlayers = new HashMap<>();
    private AntiHaxerman plugin = AntiHaxerman.INSTANCE;

    public GuiManager() {
        ItemStack cheaters = createItem(Material.BOOK_AND_QUILL, 1, "&cPotential cheaters");
        ItemStack logs = createItem(Material.BEACON, 1, "&cLogs");
        ItemStack flame = createItem(Material.BLAZE_POWDER, 1, Config.GUIPREFIX + "&8(&cBy 5170&8)");
        ItemMeta meta = flame.getItemMeta();
        meta.setLore(Collections.singletonList(ColorUtil.translate("&c" + AntiHaxerman.INSTANCE.getUpdateChecker().getCurrentVersion())));
        flame.setItemMeta(meta);
        ItemStack checks = createItem(Material.ENDER_PEARL, 1, "&cChecks");
        ItemStack exit = createItem(Material.SLIME_BALL, 1, "&cExit GUI");
        ItemStack glasswhite = createItem(Material.STAINED_GLASS_PANE, 1, " ");
        ItemStack glassgray = createItem(Material.STAINED_GLASS_PANE, 1, " ");
        glassgray.setDurability((short) 7);

        for(int i = 0; i < 27; i = i + 2) {
            MainMenu.setItem(i, glasswhite);
        }
        for(int i = 1; i < 27; i = i + 2) {
            MainMenu.setItem(i, glassgray);
        }
        MainMenu.setItem(9, cheaters);
        MainMenu.setItem(11, logs);
        MainMenu.setItem(13, flame);
        MainMenu.setItem(15, checks);
        MainMenu.setItem(17, exit);
    }

    public void openMainMenu(Player player) {
        player.openInventory(MainMenu);
    }

    public ItemStack createItem(Material material, int amount, String name, String... lore) {
        ItemStack thing = new ItemStack(material, amount);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        thingm.setLore(Arrays.asList(lore));
        thing.setItemMeta(thingm);
        return thing;
    }

    public ItemStack createGlass(Material material, int ChatColorID, int amount, String name, String... lore) {
        ItemStack thing = new ItemStack(material, amount, (short) ChatColorID);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        thingm.setLore(Arrays.asList(lore));
        thing.setItemMeta(thingm);
        return thing;
    }


    public void openChecks(Player player, String type, int pageSelect) {
        int get = 0;
        List<Check> checksCategory = new ArrayList<>();
        for(Check check : CheckManager.allChecks) {
            try {
                check.setMaxVl(Config.MAX_VIOLATIONS.get(check.getClass().getSimpleName()));
            } catch(NullPointerException exception) {
                check.setMaxVl(10);
            }
            if(check.getCheckType() == Check.CheckType.valueOf(type)) {
                checksCategory.add(check);
            }
        }
        int totalChecks = checksCategory.size();
        int maxP = (int) Math.ceil((double) totalChecks / (double) 27);
        Checks.clear();
        for (int page = 1; page < maxP + 1; page++) {
            Inventory inv = Bukkit.createInventory(null, 3 * 9, ColorUtil.translate("&cCHECKS [" + checksCategory.get(0).getCheckType().toString() + "] " + "[" + page + "]"));
            int slot = 0;
            for (int items = 1; items < 27; items++) {
                if (get >= totalChecks) {
                    break;
                }
                Check check = checksCategory.get(get);
                String[] listname = check.getFullName().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
                if(slot == 19 || slot == 25 || slot == 0 || slot == 8 || slot == 9 || slot == 17 || slot == 18 || slot == 26) {
                    slot++;
                }
                else {
                        if (AntiHaxerman.INSTANCE.getYaml().getBoolean("checks." + check.getCheckType().toString().toLowerCase() + "." + check.getCheckInfo().name().toLowerCase() + "." + check.getCheckInfo().type().toLowerCase() + ".enabled")) {
                            ItemStack c;
                            try {
                                c = createGlass(Material.STAINED_GLASS_PANE, 5, 1, ChatColor.GREEN + listname[0] + " " + listname[1] + " " + listname[2]);
                            } catch(Exception ex) {
                                c = createGlass(Material.STAINED_GLASS_PANE, 5, 1, ChatColor.GREEN + listname[0] + " " + listname[1]);
                            }
                            ItemMeta im = c.getItemMeta();
                            im.setLore(Collections.singletonList(ColorUtil.translate("&7Max VL: &c" + check.getMaxVl())));
                            c.setItemMeta(im);
                            inv.setItem(slot, c);
                        } else {
                            ItemStack c;
                            try {
                                c = createGlass(Material.STAINED_GLASS_PANE, 14, 1, ChatColor.RED + listname[0] + " " + listname[1] + " " + listname[2]);
                            } catch(Exception ex) {
                                c = createGlass(Material.STAINED_GLASS_PANE, 14, 1, ChatColor.RED + listname[0] + " " + listname[1]);
                            }
                            ItemMeta im = c.getItemMeta();
                            im.setLore(Collections.singletonList(ColorUtil.translate("&7Max VL: &c" + check.getMaxVl())));
                            c.setItemMeta(im);
                            inv.setItem(slot, c);
                        }
                    slot++;
                    get++;
                }
            }
            for (int i = slot; i < 25; i++) {
                if(i == 19 || i == 25 || i == 0 || i == 8 || i == 9 || i == 17 || i == 18) {
                    i++;
                }
                ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 15, 1, ChatColor.GRAY + "N/A");
                inv.setItem(i, c);
            }

            ItemStack previous = createItem(Material.TORCH, 1, ChatColor.RED + "Previous Page");
            inv.setItem(19, previous);
            ItemStack next = createItem(Material.REDSTONE_TORCH_ON, 1, ChatColor.RED + "Next Page");
            inv.setItem(25, next);
            ItemStack back = createItem(Material.SIGN,1,ChatColor.RED + "Back");
            inv.setItem(9, back);
            ItemStack slimeball = createItem(Material.SLIME_BALL,1,ChatColor.RED + "Exit GUI");
            inv.setItem(17, slimeball);
            Checks.put(page, inv);
        }
        player.openInventory(Checks.get(pageSelect));
    }

    public void openSusPlayers(Player player, int pageSelect) {
        int get = 0;
        List<Player> susPlayers = PlayerDataManager.getInstance().suspectedPlayers;
        int totalChecks = susPlayers.size();
        int maxP = (int) Math.ceil((double) totalChecks / (double) 27);
        SussyPlayers.clear();
        for (int page = 1; page < maxP + 1; page++) {
            Inventory inv = Bukkit.createInventory(null, 3 * 9, ColorUtil.translate("&cSussyPlayers " + "[" + page + "]"));
            ItemStack glasswhite = createItem(Material.STAINED_GLASS_PANE, 1, " ");
            ItemStack glassgray = createItem(Material.STAINED_GLASS_PANE, 1, " ");
            glassgray.setDurability((short) 7);

            for(int i = 0; i < 27; i = i + 2) {
                inv.setItem(i, glasswhite);
            }
            for(int i = 1; i < 27; i = i + 2) {
                inv.setItem(i, glassgray);
            }
            int slot = 0;
            for (int items = 1; items < 27; items++) {
                if (get >= totalChecks) {
                    break;
                }
                Player playerSelected = susPlayers.get(get);
                if(slot >= 10 && slot <= 16) {
                    ItemStack c = createItem(Material.SKULL_ITEM, 1, "&c" + playerSelected.getName());
                    c.setDurability((byte) 3);
                    SkullMeta im = (SkullMeta) c.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add(ColorUtil.translate("&7Flagged:"));
                    for (final Check check : PlayerDataManager.getInstance().getPlayerData(playerSelected).getChecks()) {
                        if (check.getVl() > 0) {
                            lore.add(ColorUtil.translate("&7 - &b" + check.getFullName() + " &8(&cx" + check.getVl() + "&8)"));
                        }
                    }
                    im.setLore(lore);
                    im.setOwner(playerSelected.getName());
                    c.setItemMeta(im);
                    inv.setItem(slot, c);
                    slot++;
                    get++;
                }
                else {
                    slot++;
                }
            }
            for (int i = slot; i < 25; i++) {
                if(i >= 10 && i <= 16) {
                    ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 15, 1, ChatColor.GRAY + "N/A");
                    inv.setItem(i, c);
                }
                else if(slot > 16) {
                    break;
                }
                else {
                    i++;
                }
            }

            ItemStack previous = createItem(Material.TORCH, 1, ChatColor.RED + "Previous Page");
            inv.setItem(19, previous);
            ItemStack next = createItem(Material.REDSTONE_TORCH_ON, 1, ChatColor.RED + "Next Page");
            inv.setItem(25, next);
            ItemStack back = createItem(Material.SIGN,1,ChatColor.RED + "Back");
            inv.setItem(9, back);
            ItemStack slimeball = createItem(Material.SLIME_BALL,1,ChatColor.RED + "Exit GUI");
            inv.setItem(17, slimeball);
            SussyPlayers.put(page, inv);
        }
        if(SussyPlayers.get(pageSelect) == null) {
            player.sendMessage(ChatColor.RED + "No Sussy Players found!");
            return;
        }
        player.openInventory(SussyPlayers.get(pageSelect));
    }

    public void openChecksMainMenu(Player p) {
        Inventory maininv = Bukkit.createInventory(null, 3 * 9, ColorUtil.translate("&cChecks Menu"));
        ItemStack combat = createItem(Material.DIAMOND_SWORD, 1, "&cCombat");
        ItemStack movement = createItem(Material.DIAMOND_BOOTS, 1, "&cMovement");
        ItemStack player = createItem(Material.SKULL_ITEM, 1, "&cPlayer");
        player.setDurability((byte) 3);
        ItemStack exit = createItem(Material.SLIME_BALL, 1, "&cExit GUI");
        ItemStack back = createItem(Material.SIGN, 1, "&cBack");
        ItemStack glasswhite = createItem(Material.STAINED_GLASS_PANE, 1, " ");
        ItemStack glassgray = createItem(Material.STAINED_GLASS_PANE, 1, " ");
        glassgray.setDurability((short) 7);
        for(int i = 0; i < 27; i = i + 2) {
            maininv.setItem(i, glasswhite);
        }
        for(int i = 1; i < 27; i = i + 2) {
            maininv.setItem(i, glassgray);
        }

        maininv.setItem(9, back);
        maininv.setItem(11, combat);
        maininv.setItem(13, movement);
        maininv.setItem(15, player);
        maininv.setItem(17, exit);
        p.openInventory(maininv);
    }

    public void openLogMenu(Player p) {
        Inventory maininv = Bukkit.createInventory(null, 3 * 9, ColorUtil.translate("&cLogs"));
        ItemStack recent = createItem(Material.LEASH, 1, "&cMost Recent");
        ItemStack player = createItem(Material.SKULL_ITEM, 1, "&cSearch");
        ItemStack purge = createItem(Material.LAVA_BUCKET, 1, "&cPurge Logs");
        player.setDurability((byte) 3);
        ItemStack exit = createItem(Material.SLIME_BALL, 1, "&cExit GUI");
        ItemStack back = createItem(Material.SIGN, 1, "&cBack");
        ItemStack glasswhite = createItem(Material.STAINED_GLASS_PANE, 1, " ");
        ItemStack glassgray = createItem(Material.STAINED_GLASS_PANE, 1, " ");
        glassgray.setDurability((short) 7);
        for(int i = 0; i < 27; i = i + 2) {
            maininv.setItem(i, glasswhite);
        }
        for(int i = 1; i < 27; i = i + 2) {
            maininv.setItem(i, glassgray);
        }
        maininv.setItem(9, back);
        maininv.setItem(11, purge);
        maininv.setItem(13, recent);
        maininv.setItem(15, player);
        maininv.setItem(17, exit);
        p.openInventory(maininv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getName().equals(ChatColor.GOLD + "Home")) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (!e.getCurrentItem().hasItemMeta()) {
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ColorUtil.translate("&cChecks"))) {
                openChecksMainMenu(player);
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ColorUtil.translate("&cExit GUI"))) {
                player.closeInventory();
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ColorUtil.translate("&cPotential cheaters"))) {
                openSusPlayers(player, 1);
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ColorUtil.translate("&cLogs"))) {
                openLogMenu(player);
            }
        } else if (e.getInventory().getName().startsWith(ChatColor.RED + "CHECKS ")) {
            Player player = (Player) e.getWhoClicked();
            String[] spl = e.getInventory().getName().split("\\[");
            int page = Integer.parseInt(spl[2].replaceAll("]", ""));
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            };
            if (CheckManager.allChecks.stream().anyMatch(check -> check.getFullName().equals(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().replaceAll(" ", ""))))) {
                String check_name = e.getCurrentItem().getItemMeta().getDisplayName().replaceAll(" ", "");
                for (Check check : CheckManager.allChecks) {
                    if (check.getFullName().equals(ChatColor.stripColor(check_name))) {
                        if (AntiHaxerman.INSTANCE.getYaml().getBoolean("checks." + check.getCheckType().toString().toLowerCase() + "." + check.getCheckInfo().name().toLowerCase() + "." + check.getCheckInfo().type().toLowerCase() + ".enabled")) {
                            AntiHaxerman.INSTANCE.getYaml().set("checks." + check.getCheckType().toString().toLowerCase() + "." + check.getCheckInfo().name().toLowerCase() + "." + check.getCheckInfo().type().toLowerCase() + ".enabled", false);
                            AntiHaxerman.INSTANCE.reloadConfig();
                            for(PlayerData data : PlayerDataManager.getInstance().getAllData()) {
                                data.getChecks().stream().filter(check1 -> check1.getFullName().equals(check.getFullName())).findFirst().get().setEnabled(false);
                            }
                            Config.updateConfig();
                            openChecks(player, check.getCheckType().toString(), page);
                            return;
                        }
                        AntiHaxerman.INSTANCE.getYaml().set("checks." + check.getCheckType().toString().toLowerCase() + "." + check.getCheckInfo().name().toLowerCase() + "." + check.getCheckInfo().type().toLowerCase() + ".enabled", true);
                        AntiHaxerman.INSTANCE.reloadConfig();
                        for(PlayerData data : PlayerDataManager.getInstance().getAllData()) {
                            data.getChecks().stream().filter(check1 -> check1.getFullName().equals(check.getFullName())).findFirst().get().setEnabled(true);
                        }
                        Config.updateConfig();
                        openChecks(player, check.getCheckType().toString(), page);
                        return;
                    }
                }
            }
            else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("Back")) {
                openChecksMainMenu(player);
            }
            else if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("Exit GUI")) {
                player.closeInventory();
            }
            else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("Previous Page")) {
                if (!Checks.containsKey(page - 1)) {
                    player.sendMessage(ChatColor.RED + "No previous page!");
                    return;
                }
                openChecks(player, spl[1].replaceAll("]", "").replaceAll(" ", ""), page - 1);
            }
            else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("Next Page")) {
                if (!Checks.containsKey(page + 1)) {
                    player.sendMessage(ChatColor.RED + "No next page!");
                    return;
                }
                openChecks(player, spl[1].replaceAll("]", "").replaceAll(" ", ""), page + 1);
            }
        }
        else if(e.getInventory().getName().equals(ColorUtil.translate("&cChecks Menu"))) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);
            if(e.getCurrentItem().getType() == Material.DIAMOND_BOOTS) {
                openChecks(player, Check.CheckType.MOVEMENT.toString(), 1);
            }
            else if(e.getCurrentItem().getType() == Material.DIAMOND_SWORD) {
                openChecks(player, Check.CheckType.COMBAT.toString(), 1);
            }
            else if(e.getCurrentItem().getType() == Material.SKULL_ITEM) {
                openChecks(player, Check.CheckType.PLAYER.toString(), 1);
            }
            else if(e.getCurrentItem().getType() == Material.SIGN) {
                openMainMenu(player);
            }
            else if(e.getCurrentItem().getType() == Material.SLIME_BALL) {
                player.closeInventory();
            }
        }
        else if(e.getInventory().getName().equals(ColorUtil.translate("&cLogs"))) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);
            if(e.getCurrentItem().getType() == Material.LAVA_BUCKET) {
                if(BukkitEventManager.wannadelet.contains(player)) {
                    player.sendMessage(ChatColor.GREEN + "Please respond to the action started before, or cancel by typing \"cancel\".");
                    return;
                }
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ARE YOU SURE U WANT TO DO THIS?");
                player.sendMessage(ChatColor.GRAY + "This action is not reversible!");
                player.sendMessage(ChatColor.GREEN + "Please say YES or \"cancel\" to cancel the delete action.");
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 10, 1);
                BukkitEventManager.wannadelet.add(player);
            }
            else if(e.getCurrentItem().getType() == Material.SKULL_ITEM) {
                player.sendMessage(ChatColor.RED + "Not implemented yet.");
            }
            else if(e.getCurrentItem().getType() == Material.LEASH) {
                player.sendMessage(ChatColor.RED + "Not implemented yet.");
            }
            else if(e.getCurrentItem().getType() == Material.SIGN) {
                openMainMenu(player);
            }
            else if(e.getCurrentItem().getType() == Material.SLIME_BALL) {
                player.closeInventory();
            }
        }
        else if(e.getInventory().getName().startsWith(ColorUtil.translate("&cSussyPlayers"))) {
            Player player = (Player) e.getWhoClicked();
            String[] spl = e.getInventory().getName().split("\\[");
            int page = Integer.parseInt(spl[1].replaceAll("]", ""));
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            };
            switch (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())) {
                case "Back":
                    openMainMenu(player);
                    break;
                case "Exit GUI":
                    player.closeInventory();
                    break;
                case "Previous Page":
                    if (!SussyPlayers.containsKey(page - 1)) {
                        player.sendMessage(ChatColor.RED + "No previous page!");
                        return;
                    }
                    openSusPlayers(player, page - 1);
                    break;
                case "Next Page":
                    if (!SussyPlayers.containsKey(page + 1)) {
                        player.sendMessage(ChatColor.RED + "No next page!");
                        return;
                    }
                    openSusPlayers(player, page + 1);
                    break;
            }
            if(e.getCurrentItem().getType() == Material.SKULL_ITEM) {
                if (AntiHaxerman.INSTANCE.getPlugin().getServer().getPluginCommand("vanish") != null) {
                    Bukkit.dispatchCommand(player, "vanish");
                    SkullMeta skullmeta = (SkullMeta) e.getCurrentItem().getItemMeta();
                    player.sendMessage(ChatColor.RED + "Detected vanish plugin, dispatching vanish command and teleporting to " + skullmeta.getOwner());
                    Bukkit.dispatchCommand(player, "teleport " + skullmeta.getOwner());
                }
                else {
                    SkullMeta skullmeta = (SkullMeta) e.getCurrentItem().getItemMeta();
                    player.sendMessage(ChatColor.RED + "Teleporting to " + skullmeta.getOwner());
                    Bukkit.dispatchCommand(player, "teleport " + skullmeta.getOwner());
                }
            }
        }
    }

    public String c(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
