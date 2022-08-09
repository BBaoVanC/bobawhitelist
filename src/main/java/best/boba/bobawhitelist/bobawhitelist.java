package best.boba.bobawhitelist;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "bobawhitelist",
        name = "bobawhitelist",
        version = "2.0.1",
        url = "https://github.com/bobacraft/bobawhitelist",
        authors = {"bbaovanc"},
        description = "Network-wide whitelist plugin for Velocity")
public class bobawhitelist {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final Config config;

    @Inject
    public bobawhitelist(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        if (!Files.exists(this.dataDirectory)) {
            try {
                Files.createDirectory(this.dataDirectory);
            }
            catch (IOException e) {
                this.logger.severe("Could not create plugin data directory: " + e.getMessage());
                this.config = null;
                return;
            }
        }

        Config config;
        try {
            config = new Config(server, logger, dataDirectory);
        }
        catch (IOException e) {
            this.logger.severe("Could not set up whitelist JSON: " + e.getMessage());
            config = null;
        }
        this.config = config;
    }

    public void initialize() {
        EventManager eventManager = server.getEventManager();
        eventManager.register(this, new ListenerLogin(this.config));

        CommandManager commandManager = server.getCommandManager();
        new WhitelistCommand(this.config).register();
        new KickCommand(this.server).register();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        initialize();
    }

    @Subscribe
    public void onProxyReload(ProxyReloadEvent event) {
        initialize();
    }
}
