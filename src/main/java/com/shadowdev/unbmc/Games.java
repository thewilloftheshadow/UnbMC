package com.shadowdev.unbmc;

import org.bukkit.ChatColor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class Games {
    private final UnbMC plugin;

    public Games(UnbMC plugin) {
        this.plugin = plugin;
    }

    public void startGuessNumber() {
        if (!this.plugin.activeGame.isEmpty())
            return;
        int minTime = this.plugin.getConfig().getInt("guess-the-number.time.min");
        int maxTime = this.plugin.getConfig().getInt("guess-the-number.time.max");
        int minPay = this.plugin.getConfig().getInt("guess-the-number.payout.min");
        int maxPay = this.plugin.getConfig().getInt("guess-the-number.payout.max");
        int minNumber = this.plugin.getConfig().getInt("guess-the-number.number.min");
        int maxNumber = this.plugin.getConfig().getInt("guess-the-number.number.max");

        int time = ((int) (Math.random() * (maxTime - minTime))) + minTime;
        int pay = ((int) (Math.random() * (maxPay - minPay))) + minPay;
        int number = ((int) (Math.random() * (maxNumber - minNumber))) + minNumber;

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            this.plugin.activeGame = "guessNumber";
            this.plugin.gamePrize = pay;
            this.plugin.numberToGuess = String.valueOf(number);

            String msg = ChatColor.GREEN + "Guess the number I'm thinking of between " + ChatColor.YELLOW
                    + String.valueOf(minNumber) + ChatColor.GREEN + " and " + ChatColor.YELLOW
                    + String.valueOf(maxNumber) + ChatColor.GREEN + " to win a prize!";
            TextComponent message = Component.text(msg);
            this.plugin.getServer().broadcast(message);
        }, (20 * 60 * time));

    }
}
