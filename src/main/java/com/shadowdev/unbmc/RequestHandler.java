package com.shadowdev.unbmc;

import com.github.kevinsawicki.http.HttpRequest;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.json.JSONObject;

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
}