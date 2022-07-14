package com.shadowdev.unbmc.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.shadowdev.unbmc.UnbMC;

import io.papermc.paper.event.player.AsyncChatEvent;

public class ChatListener implements Listener {
    public HashMap<Player, Long> lastMessage = new HashMap<Player, Long>();
    public UnbMC plugin;
    public int cooldown;

    public ChatListener(UnbMC plugin) {
        this.plugin = plugin;
        this.cooldown = plugin.getConfig().getInt("chat-money.cooldown");
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Player player = event.getPlayer();
            if (lastMessage.containsKey(player)) {
                this.plugin.debug(lastMessage.get(player).toString());
                if (System.currentTimeMillis() > lastMessage.get(player) + (cooldown * 1000)) {
                    this.plugin.debug("Cooldown is over");
                    doChatMoney(player);
                }
            } else {
                this.plugin.debug("No last message");
                doChatMoney(player);
            }
        });
    }

    public void doChatMoney(Player player) {
        this.plugin.debug("Doing chat money");
        int min = this.plugin.getConfig().getInt("chat-money.min");
        int max = this.plugin.getConfig().getInt("chat-money.max");

        lastMessage.put(player, System.currentTimeMillis());
        // random number between min and max
        int random = ((int) (Math.random() * (max - min))) + min;

        this.plugin.requestHandler.addCash(player, random);
    }
}