package best.boba.bobawhitelist;

import java.util.UUID;

public class WhitelistPlayer {
    private final UUID uuid;
    private final String username;

    public WhitelistPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
