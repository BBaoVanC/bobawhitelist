package best.boba.bobawhitelist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Whitelist {
    private final Path whitelistFile;
    private final List<WhitelistPlayer> whitelistPlayerList;
    private final HashMap<String, WhitelistPlayer> whitelistUsernameMap;
    private final HashMap<UUID, WhitelistPlayer> whitelistUUIDMap;

    private static final Type jsonType = new TypeToken<List<WhitelistPlayer>>(){}.getType();

    public Whitelist(Path whitelistFile) throws IOException {
        this.whitelistFile = whitelistFile;

        this.whitelistUUIDMap = new HashMap<>();
        this.whitelistUsernameMap = new HashMap<>();

        if (Files.notExists(whitelistFile)) {
            this.whitelistPlayerList = new ArrayList<>();
        } else {
            this.whitelistPlayerList = loadJSON();
            for (WhitelistPlayer whitelistPlayer : this.whitelistPlayerList) {
                this.whitelistUUIDMap.put(whitelistPlayer.getUUID(), whitelistPlayer);
                this.whitelistUsernameMap.put(whitelistPlayer.getUsername(), whitelistPlayer);
            }
        }
        saveJSON();
    }

    private void saveJSON() throws IOException {
        try (Writer writer = new FileWriter(whitelistFile.toString())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this.whitelistPlayerList, writer);
        }
    }

    private List<WhitelistPlayer> loadJSON() throws IOException {
        try (Reader reader = new FileReader(whitelistFile.toString())) {
            Gson gson = new Gson();
            List<WhitelistPlayer> json = gson.fromJson(reader, jsonType);
            if (json == null) {
                return new ArrayList<>();
            }
            return json;
        }
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


    public void add(WhitelistPlayer whitelistPlayer) throws IOException, DuplicatePlayerException {
        if (has(whitelistPlayer.getUUID())) {
            throw new DuplicatePlayerException();
        }

        this.whitelistPlayerList.add(whitelistPlayer);
        this.whitelistUsernameMap.put(whitelistPlayer.getUsername(), whitelistPlayer);
        this.whitelistUUIDMap.put(whitelistPlayer.getUUID(), whitelistPlayer);
        saveJSON();
    }

    public void add(UUID uuid, String username) throws IOException, DuplicatePlayerException {
        if (has(uuid)) {
            throw new DuplicatePlayerException();
        }

        WhitelistPlayer whitelistPlayer = new WhitelistPlayer(uuid, username);
        this.whitelistPlayerList.add(whitelistPlayer);
        this.whitelistUsernameMap.put(username, whitelistPlayer);
        this.whitelistUUIDMap.put(uuid, whitelistPlayer);
        saveJSON();
    }


    public void remove(UUID uuid) throws IOException {
        WhitelistPlayer whitelistPlayer = this.whitelistUUIDMap.get(uuid);
        this.whitelistPlayerList.remove(whitelistPlayer);
        this.whitelistUUIDMap.remove(uuid);
        this.whitelistUsernameMap.remove(whitelistPlayer.getUsername());
        saveJSON();
    }

    public void remove(String username) throws IOException {
        WhitelistPlayer whitelistPlayer = this.whitelistUsernameMap.get(username);
        this.whitelistPlayerList.remove(whitelistPlayer);
        this.whitelistUsernameMap.remove(username);
        this.whitelistUUIDMap.remove(whitelistPlayer.getUUID());
        saveJSON();
    }
}
