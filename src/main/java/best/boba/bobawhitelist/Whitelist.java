package best.boba.bobawhitelist;

import java.util.*;

public class Whitelist {
    private final List<WhitelistPlayer> whitelistPlayerList;
    private final HashMap<String, WhitelistPlayer> whitelistUsernameMap;
    private final HashMap<UUID, WhitelistPlayer> whitelistUUIDMap;

    public Whitelist() {
        this.whitelistPlayerList = new ArrayList<>();
        this.whitelistUUIDMap = new HashMap<>();
        this.whitelistUsernameMap = new HashMap<>();
    }

    public WhitelistPlayer get(UUID uuid) {
        return this.whitelistUUIDMap.get(uuid);
    }

    public WhitelistPlayer get(String username) {
        return this.whitelistUsernameMap.get(username);
    }

    public Set<UUID> getUUIDs() {
        return this.whitelistUUIDMap.keySet();
    }

    public Set<String> getUsernames() {
        return this.whitelistUsernameMap.keySet();
    }

    public List<WhitelistPlayer> getList() {
        return this.whitelistPlayerList;
    }


    public boolean has(WhitelistPlayer whitelistPlayer) {
        return this.whitelistPlayerList.contains(whitelistPlayer);
    }

    public boolean has(UUID uuid) {
        return this.whitelistUUIDMap.containsKey(uuid);
    }

    public boolean has(String username) {
        return this.whitelistUsernameMap.containsKey(username);
    }


    public void add(WhitelistPlayer whitelistPlayer) {
        this.whitelistPlayerList.add(whitelistPlayer);
        this.whitelistUsernameMap.put(whitelistPlayer.getUsername(), whitelistPlayer);
        this.whitelistUUIDMap.put(whitelistPlayer.getUUID(), whitelistPlayer);
    }

    public void add(UUID uuid, String username) {
        WhitelistPlayer whitelistPlayer = new WhitelistPlayer(uuid, username);
        this.whitelistPlayerList.add(whitelistPlayer);
        this.whitelistUsernameMap.put(username, whitelistPlayer);
        this.whitelistUUIDMap.put(uuid, whitelistPlayer);
    }


    public void remove(UUID uuid) {
        WhitelistPlayer whitelistPlayer = this.whitelistUUIDMap.get(uuid);
        this.whitelistPlayerList.remove(whitelistPlayer);
        this.whitelistUUIDMap.remove(uuid);
        this.whitelistUsernameMap.remove(whitelistPlayer.getUsername());
    }

    public void remove(String username) {
        WhitelistPlayer whitelistPlayer = this.whitelistUsernameMap.get(username);
        this.whitelistPlayerList.remove(whitelistPlayer);
        this.whitelistUsernameMap.remove(username);
        this.whitelistUUIDMap.remove(whitelistPlayer.getUUID());
    }
}
