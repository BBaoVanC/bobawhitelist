package best.boba.bobawhitelist;

import com.velocitypowered.api.proxy.ProxyServer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Config {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final Path whitelistFile;
    private final Path usernameCacheFile;
    private Whitelist whitelist;
    private UsernameCache usernameCache;

    public Config(ProxyServer server, Logger logger, Path dataDirectory) throws IOException {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.whitelistFile = Paths.get(this.dataDirectory.toString(), "whitelist.json");
        this.usernameCacheFile = Paths.get(this.dataDirectory.toString(), "username-cache.json");

        this.whitelist = new Whitelist(this.whitelistFile);
        this.usernameCache = new UsernameCache(this.usernameCacheFile);
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

    public UsernameCache getUsernameCache() {
        return this.usernameCache;
    }

    public void reloadWhitelist() throws IOException {
        this.whitelist = new Whitelist(this.whitelistFile);
        this.usernameCache = new UsernameCache(this.usernameCacheFile);
    }
}
