package com.shadowdev.unbmc;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.shadowdev.unbmc.commands.BalanceCommand;
import com.shadowdev.unbmc.listeners.ChatListener;

public final class UnbMC extends JavaPlugin {
    public final Logger logger = this.getLogger();
    public final RequestHandler requestHandler = new RequestHandler(this);
    public UnbMC unbMC;

    public final int currentConfig = 1;
    public String apiToken = "";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults();
        Boolean configIsValid = checkConfig();
        this.logger.info("Config is valid: " + configIsValid);
        if (configIsValid) {

            this.debug("Debug mode has been activated");

            this.apiToken = getConfig().getString("api-token");
            if (this.apiToken.isEmpty()) {
                this.logger.severe("No API token has been set. Please set one in the config.yml file.");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }

            new Metrics(this, 15762);

            getCommand("balance").setExecutor(new BalanceCommand(this));
            Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        }

    }

    @Override
    public void onDisable() {
        logger.info("UnbMC has been disabled.");
        Bukkit.getScheduler().cancelTasks(this);
    }

    public Boolean checkConfig() {
        int configVersion = this.getConfig().getInt("config-version");
        this.logger.log(Level.CONFIG, "Config version: " + configVersion);
        if (configVersion != this.currentConfig) {
            File oldConfigTo = new File(this.getDataFolder(), "config-old-" + configVersion + ".yml");
            File old = new File(this.getDataFolder(), "config.yml");
            try {
                FileUtils.moveFile(old, oldConfigTo);
                getConfig().options().copyDefaults();
                saveDefaultConfig();
                this.logger.severe("Your config is outdated. Your old config has been moved to " + oldConfigTo.getName()
                        + ", and the new version has been applied in its place.");
            } catch (Exception e) {
                File newConfig = new File(this.getDataFolder(), "config-new.yml");
                InputStream newConfigData = this.getResource("config.yml");
                try {
                    FileUtils.copyInputStreamToFile(newConfigData, newConfig);
                    this.logger.severe(
                            "Your config is outdated, but I was unable to replace your old config. Instead, the new config has been saved to "
                                    + newConfig.getName() + ".");
                } catch (Exception e1) {
                    this.logger.severe(
                            "Your config is outdated, but I could not move your old config to a backup or copy in the new config format.");
                }

            }

            this.logger.severe(
                    "The plugin will now disable, please migrate the values from your old config to the new one.");
            this.getServer().getPluginManager().disablePlugin(this);
            return false;
        } else {
            File newConfig = new File(this.getDataFolder(), "config-new.yml");
            if (newConfig.exists())
                FileUtils.deleteQuietly(newConfig);
        }
        return true;
    }

    public void debug(String message) {
        if (this.getConfig().getBoolean("debug")) {
            this.logger.info(message);
        }
    }

    public String moneyFormat(int money) {
        String symbol = getConfig().getString("symbol.character");
        String placement = getConfig().getString("symbol.placement");
        if(placement.isEmpty()) placement = "before";
        if(symbol.isEmpty()) symbol = "$";
        if (placement.equals("after")) {
            return money + symbol;
        } else {
            return symbol + money;
        }

    }

}