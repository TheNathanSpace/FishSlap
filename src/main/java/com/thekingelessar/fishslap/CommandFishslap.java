package com.thekingelessar.fishslap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandFishslap implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args == null || args.length == 0) return false;
        
        switch (args[0])
        {
            case "setspawn":
                if (sender.hasPermission("spintowin.setspawn"))
                {
                    try
                    {
                        FishSlap.instance.loadConfig();
                        
                        FishSlap.config.set("spawn_x", args[1]);
                        FishSlap.config.set("spawn_y", args[2]);
                        FishSlap.config.set("spawn_z", args[3]);
                        
                        if (args.length > 4)
                        {
                            try
                            {
                                FishSlap.config.set("yaw", args[4]);
                                FishSlap.config.set("pitch", args[5]);
                                
                                sender.sendMessage(FishSlap.hubPrefix + String.format("Set new spawn: %s %s %s %s %s", args[1], args[2], args[3], args[4], args[5]));
                                
                                return true;
                            }
                            catch (Exception exception)
                            {
                                return false;
                            }
                        }
                        
                        FishSlap.instance.saveConfig();
                        
                        sender.sendMessage(FishSlap.hubPrefix + String.format("Set new spawn: %s %s %s", args[1], args[2], args[3]));
                    }
                    catch (Exception exception)
                    {
                        return false;
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }
                return true;
            
            case "reload":
                if (sender.hasPermission("fishslap.reload"))
                {
                    FishSlap.instance.loadConfig();
                    sender.sendMessage(FishSlap.hubPrefix + "Reloaded config!");
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }
                return true;
            
            case "level":
                if (sender.hasPermission("fishslap.level"))
                {
                    FishSlap.instance.loadConfig();
                    FishSlap.config.set("min_y_level", args[1]);
                    FishSlap.instance.saveConfig();
                    sender.sendMessage(FishSlap.hubPrefix + String.format("Set new minimum y-level: %s", args[1]));
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }
                return true;
            
            default:
                return false;
        }
    }
}