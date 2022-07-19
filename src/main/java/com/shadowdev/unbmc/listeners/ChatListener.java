package com.shadowdev.unbmc.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.shadowdev.unbmc.UnbMC;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

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
            checkChatMoney(player);
        });

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plugin.debug(this.plugin.numberToGuess);
            final String content = PlainTextComponentSerializer.plainText().serialize(event.message());
            this.plugin.debug(content);

            if (this.plugin.activeGame == "guessNumber") {
                Boolean equals = content.equals(this.plugin.numberToGuess);
                this.plugin.debug(equals.toString());
                if (equals) {
                    this.plugin.debug("Number correct");
                    this.plugin.activeGame = "";
                    this.plugin.numberToGuess = "";
                    Component message = Component
                            .text("You guessed the number correctly! You won " + this.plugin.gamePrize + " coins!")
                            .color(TextColor.color(0, 255, 0));
                    this.plugin.getServer().broadcast(message);
                    this.plugin.requestHandler.addCash(event.getPlayer(), this.plugin.gamePrize);
                } else {
                    this.plugin.debug("Number incorrect");
                }
            }
        });
    }

    public void checkChatMoney(Player player) {
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
    }

    public void doChatMoney(Player player) {
        this.plugin.debug("Doing chat money");
        int min = this.plugin.getConfig().getInt("chat-money.min");
        int max = this.plugin.getConfig().getInt("chat-money.max");
        lastMessage.put(player, System.currentTimeMillis());
        giveRandomMoney(player, min, max);
    }

    public void giveRandomMoney(Player player, int min, int max) {
        int random = ((int) (Math.random() * (max - min))) + min;
        this.plugin.requestHandler.addCash(player, random);
    }
}