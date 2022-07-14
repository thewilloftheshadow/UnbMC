package com.shadowdev.unbmc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import github.scarsz.discordsrv.dependencies.json.JSONObject;

import com.shadowdev.unbmc.UnbMC;

public class BalanceCommand implements CommandExecutor {

    private final UnbMC plugin;

    public BalanceCommand(UnbMC unbMC) {
        this.plugin = unbMC;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();

            String discordPlayerId = accountLinkManager.getDiscordId(player.getUniqueId());
            if (discordPlayerId == null || discordPlayerId.isEmpty()) {
                player.sendMessage(ChatColor.RED
                        + "You are not linked to a Discord account. Please use the command \"/discord link\" to link your account.");
                return true;
            }

            JSONObject data = this.plugin.requestHandler.getBalance(discordPlayerId);
            if (data.length() == 0) {
                player.sendMessage(ChatColor.RED + "An error occurred while retrieving your balance.");
                return true;
            }

            String msg = String.format("You have %s cash and %s in your bank, giving you a total of %s.",
                    this.plugin.moneyFormat(data.getInt("cash")),
                    this.plugin.moneyFormat(data.getInt("bank")),
                    this.plugin.moneyFormat(data.getInt("total")));
            player.sendMessage(ChatColor.GREEN + msg);
        } else
            this.plugin.logger.info("This command can only be run by players");

        return true;
    }
}