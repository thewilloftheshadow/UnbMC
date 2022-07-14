package com.shadowdev.unbmc;

import org.bukkit.entity.Player;

import com.github.kevinsawicki.http.HttpRequest;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.json.JSONObject;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;

public class RequestHandler {
    private final UnbMC plugin;

    public RequestHandler(UnbMC unbMC) {
        this.plugin = unbMC;
    }

    public JSONObject getBalance(String userId) {
        return getBalance(userId, DiscordSRV.getPlugin().getMainGuild().getId());
    }

    public JSONObject getBalance(String userId, String guildId) {

        String url = String.format("https://unbelievaboat.com/api/v1/guilds/%s/users/%s", guildId, userId);

        HttpRequest request = HttpRequest.get(url).accept("application/json").header("Authorization",
                this.plugin.apiToken);
        String body = request.body();
        int code = request.code();

        if (code == 401) {
            this.plugin.logger.severe("Invalid API token. Please set one in the config.yml file.");
            return new JSONObject("");
        }

        JSONObject json = new JSONObject(body);
        return json;
    }

    public boolean addCash(Player player, int money) {
        return addCash(player, money, DiscordSRV.getPlugin().getMainGuild().getId());
    }

    public boolean addCash(Player player, int money, String guildId) {

        AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();

        String userId = accountLinkManager.getDiscordId(player.getUniqueId());
        if (userId == null || userId.isEmpty()) {
            this.plugin.debug("Player " + player.getName()
                    + " is not linked to a Discord account, so no chat money could be given.");
            return false;
        }

        JSONObject balance = getBalance(userId, guildId);

        this.plugin.debug(balance.toString());
        this.plugin.debug(String.valueOf(money));

        String newData = String.format(
                "{\"cash\": %s, \"reason:\": \"Chat money from Minecraft server for player %s\"}",
                balance.getInt("cash") + money,
                player.getName());

        String url = String.format("https://unbelievaboat.com/api/v1/guilds/%s/users/%s", guildId, userId);

        HttpRequest request = HttpRequest.put(url).accept("application/json").header("Authorization",
                this.plugin.apiToken).send(newData);
        String body = request.body();
        int code = request.code();

        this.plugin.debug(body);
        this.plugin.debug(String.valueOf(code));

        if (code == 401) {
            this.plugin.logger.severe("Invalid API token. Please set one in the config.yml file.");
            return false;
        }
        if (code == 403) {
            this.plugin.logger.severe("I do not have permission to add cash to users on Unbelievaboat!");
            return false;
        }

        JSONObject json = new JSONObject(body);
        this.plugin.debug(json.toString());
        return true;
    }
}