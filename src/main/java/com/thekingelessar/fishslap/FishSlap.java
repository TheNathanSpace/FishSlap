package com.thekingelessar.fishslap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FishSlap extends JavaPlugin
{
    
    static ConsoleCommandSender console = null;
    static FileConfiguration config = null;
    static FileConfiguration playerDataFile = null;
    static final String hubPrefix = ChatColor.DARK_PURPLE.toString() + "[FishSlap] " + ChatColor.RESET.toString();
    
    static public FishSlap instance;
    
    @Override
    public void onEnable()
    {
        instance = this;
        console = Bukkit.getServer().getConsoleSender();
        
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        loadConfig();
        doCustomConfig();
    
        this.getCommand("fishslap").setExecutor(new CommandFishslap());
    
        ConfigurationSection playerSection = playerDataFile.getConfigurationSection("players");
        if (playerSection != null)
        {
            Map<String, Object> playerDataListRaw = playerDataFile.getConfigurationSection("players").getValues(false);
            
            try
            {
                Map<UUID, Integer> playerDataList = new HashMap<UUID, Integer>();
                
                for (Map.Entry<String, Object> entry : playerDataListRaw.entrySet())
                {
                    playerDataList.put(UUID.fromString(entry.getKey()), (Integer) entry.getValue());
                }
                
                for (Map.Entry<UUID, Integer> singlePlayerData : playerDataList.entrySet())
                {
                    PlayerListener.playerKillsDict.put(singlePlayerData.getKey(), singlePlayerData.getValue());
                }
                console.sendMessage(hubPrefix + "Loaded player data!");
                
                
            }
            catch (Exception e)
            {
                console.sendMessage(hubPrefix + "Player data invalid! Backing up old data and overwriting.");
                File saveFile = new File("plugins/Hub/player_data_backup_" + (System.currentTimeMillis() / 1000L) + ".yml");
                if (!saveFile.exists())
                {
                    try
                    {
                        saveFile.createNewFile();
                        FileWriter write = new FileWriter(saveFile);
                        write.write(playerDataListRaw.toString());
                    }
                    catch (IOException ioException)
                    {
                        console.sendMessage(hubPrefix + "Couldn't back up old data. Here it is:");
                        console.sendMessage(playerDataListRaw.toString());
                    }
                }
            }
        }
        else
        {
            console.sendMessage(hubPrefix + "No player data to load! First time?");
        }
        
        super.onEnable();
        
    }
    
    @Override
    public void onDisable()
    {
        console.sendMessage(hubPrefix + "Disabling FishSlap. Saving player data.");
        
        playerDataFile.createSection("players", PlayerListener.playerKillsDict);
        try
        {
            playerDataFile.save(new File(getDataFolder(), "player_data.yml"));
        }
        catch (IOException ioException)
        {
            console.sendMessage(hubPrefix + "Error saving data. Could not be saved.");
            ioException.printStackTrace();
        }
        super.onDisable();
    }
    
    private void doCustomConfig()
    {
        File playerDataFileObject = new File(getDataFolder(), "player_data.yml");
        playerDataFile = YamlConfiguration.loadConfiguration(playerDataFileObject);
        
        if (!playerDataFileObject.exists())
        {
            playerDataFileObject.getParentFile().mkdirs();
            saveResource("player_data.yml", false);
        }
        
        playerDataFile = new YamlConfiguration();
        try
        {
            playerDataFile.load(playerDataFileObject);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }
    
    public void loadConfig() {
        this.saveDefaultConfig();
        config = this.getConfig();
    }
}
