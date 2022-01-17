package best.boba.bobawhitelist;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "bobawhitelist",
        name = "bobawhitelist",
        version = "1.0",
        url = "https://github.com/bobacraft/bobawhitelist",
        authors = {"bbaovanc"},
        description = "Network-wide whitelist plugin for Velocity")
public class bobawhitelist {
    private final ProxyServer server;
    private final Logger logger;
    //private final Path dataDirectory;
    private final Config config;

    @Inject
    public bobawhitelist(ProxyServer server, Logger logger) {
    //public bobawhitelist(ProxyServer server, Logger logger, Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        //this.dataDirectory = dataDirectory;

        this.config = new Config();
    }

    public void initialize() {
        EventManager eventManager = server.getEventManager();
        CommandManager commandManager = server.getCommandManager();

        eventManager.register(this, new ListenerPostLogin());
        WhitelistCommand.createBrigadierCommand(this.server, this.config, commandManager);
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
