package best.boba.bobawhitelist;

import com.velocitypowered.api.proxy.ProxyServer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Config {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final Path whitelistFile;
    private Whitelist whitelist;

    private final LuckPerms luckPerms;

    public Config(ProxyServer server, Logger logger, Path dataDirectory) throws IOException {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.whitelistFile = Paths.get(this.dataDirectory.toString(), "whitelist.json");

        this.luckPerms = LuckPermsProvider.get();

        this.whitelist = new Whitelist(this.whitelistFile);
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public Path getWhitelistFile() {
        return whitelistFile;
    }

    public Whitelist getWhitelist() {
        return this.whitelist;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public void reloadWhitelist() throws IOException {
        this.whitelist = new Whitelist(this.whitelistFile);
    }
}
