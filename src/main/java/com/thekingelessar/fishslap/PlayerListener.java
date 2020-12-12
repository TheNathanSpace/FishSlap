package com.thekingelessar.fishslap;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.thekingelessar.fishslap.FishSlap.hubPrefix;

public class PlayerListener implements Listener
{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        updateScoreboard(player);
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();
        
        ItemStack fishStack = new ItemStack(Material.RAW_FISH, 1);
        fishStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10);
        ItemMeta fishMeta = fishStack.getItemMeta();
        fishMeta.setDisplayName(ChatColor.DARK_PURPLE + "fish");
        fishMeta.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.DARK_PURPLE.toString() + "slap"));
        fishMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        fishStack.setItemMeta(fishMeta);
        
        boolean alreadyFish = false;
        for (int inventorySlot = 0; inventorySlot < contents.length; inventorySlot++)
        {
            if (contents[inventorySlot] != null)
            {
                if (contents[inventorySlot].equals(fishStack))
                {
                    alreadyFish = true;
                }
            }
        }
        
        if (!alreadyFish)
        {
            inventory.setItem(0, fishStack);
        }
    }
    
    public static Map<UUID, UUID> playerDamageDict = new HashMap<>();
    public static Map<UUID, Integer> playerKillsDict = new HashMap<>();
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(!event.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING)) {
            event.setDamage(0);
        }
    }
    
    @EventHandler
    public void onPlayerGotHit(EntityDamageByEntityEvent event)
    {
        Entity attacker = event.getDamager();
        Entity target = event.getEntity();
        if (target instanceof Player && attacker instanceof Player)
        {
            playerDamageDict.put(((Player) target).getUniqueId(), ((Player) attacker).getUniqueId());
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Location newLocation = event.getTo();
        double voidY = newLocation.getY();
        
        if (voidY < Double.parseDouble(FishSlap.config.get("min_y_level").toString()))
        {
            FishSlap.console.sendMessage(hubPrefix + "Teleporting: " + Double.parseDouble(FishSlap.config.get("min_y_level").toString()));
    
            Player player = event.getPlayer();
            
            try
            {
                double x = Double.parseDouble(FishSlap.config.get("spawn_x").toString());
                double y = Double.parseDouble(FishSlap.config.get("spawn_y").toString());
                double z = Double.parseDouble(FishSlap.config.get("spawn_z").toString());
    
                Double yaw = (Double) Double.parseDouble(FishSlap.config.get("yaw").toString());
                Double pitch = (Double) Double.parseDouble(FishSlap.config.get("pitch").toString());
                player.teleport(new Location(newLocation.getWorld(), x, y, z, yaw.floatValue(), pitch.floatValue()));
                UUID attacker = playerDamageDict.get(((Player) player).getUniqueId());
                if (attacker != null)
                {
                    Integer kills = 1;
                    if (playerKillsDict.get(attacker) != null)
                    {
                        kills += playerKillsDict.get(attacker);
                    }
        
                    playerKillsDict.put(attacker, kills);
                    Player attackerPlayer = Bukkit.getPlayer(attacker);
                    attackerPlayer.playSound(attackerPlayer.getLocation(), Sound.ORB_PICKUP, 0.8F, 1.0F);
                    playerDamageDict.put(player.getUniqueId(), null);
        
                    for (Player serverPlayer : Bukkit.getServer().getOnlinePlayers())
                    {
                        updateScoreboard(serverPlayer);
                    }
                }
    
            } catch (Exception exception) {
                FishSlap.console.sendMessage(hubPrefix + "Error reading config file. They should be numbers!");
            }
        }
    }
    
    public static HashMap<UUID, Integer> sortByValue(HashMap<UUID, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<UUID, Integer>> list = new LinkedList<Map.Entry<UUID, Integer>>(hm.entrySet());
        
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<UUID, Integer>>()
        {
            public int compare(Map.Entry<UUID, Integer> o1,
                               Map.Entry<UUID, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        
        // put data from sorted list to hashmap
        HashMap<UUID, Integer> temp = new LinkedHashMap<UUID, Integer>();
        for (Map.Entry<UUID, Integer> aa : list)
        {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
    
    public static void updateScoreboard(Player player)
    {
        ScoreHelper helper = ScoreHelper.createScore(player);
        
        Map<UUID, Integer> sortedMap = sortByValue((HashMap<UUID, Integer>) playerKillsDict);
        int slot = 6;
        for (Map.Entry<UUID, Integer> entry : sortedMap.entrySet())
        {
            String playerName = null;
            try
            {
                playerName = Bukkit.getPlayer(entry.getKey()).getDisplayName();
            }
            catch (NullPointerException nullPointerException)
            {
                playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            }
            
            helper.setSlot(slot, ChatColor.DARK_PURPLE.toString() + entry.getValue() + ": " + ChatColor.WHITE.toString() + playerName);
            slot--;
            
            if (slot <= 1) break;
        }
        
        helper.setTitle("&5&lFish Slaps");
        helper.setSlot(7, "&7&m-----------------");
        helper.setSlot(1, "&7&m-----------------");
    }
}
