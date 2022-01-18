package best.boba.bobawhitelist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UsernameCache {
    private final Path cacheFile;
    private final HashMap<UUID, String> uuidMap;
    private final HashMap<String, UUID> usernameMap;

    private static final Type jsonType = new TypeToken<HashMap<UUID, String>>(){}.getType();

    public UsernameCache(Path cacheFile) throws IOException {
        this.cacheFile = cacheFile;
        this.usernameMap = new HashMap<>();

        if (Files.notExists(cacheFile)) {
            this.uuidMap = new HashMap<>();
        } else {
            this.uuidMap = loadJSON();
            for (Map.Entry<UUID, String> entry : this.uuidMap.entrySet()) {
                this.uuidMap.put(entry.getKey(), entry.getValue());
                this.usernameMap.put(entry.getValue(), entry.getKey());
            }
        }
    }


    private void saveJSON() throws IOException {
        try (Writer writer = new FileWriter(this.cacheFile.toString())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this.uuidMap, writer);
        }
    }

    private HashMap<UUID, String> loadJSON() throws IOException {
        try (Reader reader = new FileReader(this.cacheFile.toString())) {
            Gson gson = new Gson();
            HashMap<UUID, String> json = gson.fromJson(reader, jsonType);
            if (json == null) {
                return new HashMap<>();
            }
            return json;
        }
    }


    public String getUsername(UUID uuid) {
        if (uuidMap.containsKey(uuid)) {
            return this.uuidMap.get(uuid);
        }

        WhitelistPlayer whitelistPlayer = Utils.getOnlinePlayer(uuid);
        if (whitelistPlayer == null) {
            return null;
        }
        add(whitelistPlayer.getUUID(), whitelistPlayer.getUsername());
        return whitelistPlayer.getUsername();
    }

    public UUID getUUID(String username) {
        if (usernameMap.containsKey(username)) {
            return this.usernameMap.get(username);
        }

        WhitelistPlayer whitelistPlayer = Utils.getOnlinePlayer(username);
        if (whitelistPlayer == null) {
            return null;
        }
        add(whitelistPlayer.getUUID(), whitelistPlayer.getUsername());
        return whitelistPlayer.getUUID();
    }


    public void add(UUID uuid, String username) {
        this.uuidMap.put(uuid, username);
        this.usernameMap.put(username, uuid);
        try {
            saveJSON();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void remove(UUID uuid) {
        String username = this.uuidMap.get(uuid);
        this.uuidMap.remove(uuid);
        this.usernameMap.remove(username);
        try {
            saveJSON();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void remove(String username) {
        UUID uuid = this.usernameMap.get(username);
        this.usernameMap.remove(username);
        this.uuidMap.remove(uuid);
        try {
            saveJSON();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateUsername(UUID uuid, String newUsername) {
        remove(uuid);
        add(uuid, newUsername);
    }
}
