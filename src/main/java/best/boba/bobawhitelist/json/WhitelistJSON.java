package best.boba.bobawhitelist.json;

import best.boba.bobawhitelist.WhitelistPlayer;

import java.util.List;

public class WhitelistJSON {
    private final List<WhitelistPlayer> whitelistPlayerList;
    public WhitelistJSON(List<WhitelistPlayer> whitelistPlayerList) {
        this.whitelistPlayerList = whitelistPlayerList;
    }

    public List<WhitelistPlayer> getWhitelistPlayerList() {
        return whitelistPlayerList;
    }
}
