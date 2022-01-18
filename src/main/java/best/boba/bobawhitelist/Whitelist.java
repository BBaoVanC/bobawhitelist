package best.boba.bobawhitelist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Whitelist {
    private final Path whitelistFile;
    private final HashMap<UUID, String> whitelistMap;
    private final CaseInsensitiveMap<String, UUID> whitelistUsernameMap;

    private static final Type jsonType = new TypeToken<HashMap<UUID, String>>(){}.getType();

    public Whitelist(Path whitelistFile) throws IOException {
        this.whitelistFile = whitelistFile;

        this.whitelistUsernameMap = new CaseInsensitiveMap<>();

        if (Files.notExists(whitelistFile)) {
            this.whitelistMap = new HashMap<>();
        } else {
            this.whitelistMap = loadJSON();
            for (Map.Entry<UUID, String> entry : this.whitelistMap.entrySet()) {
                this.whitelistUsernameMap.put(entry.getValue(), entry.getKey());
            }
        }
        saveJSON();
    }

    private void saveJSON() throws IOException {
        try (Writer writer = new FileWriter(whitelistFile.toString())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this.whitelistMap, writer);
        }
    }

    private HashMap<UUID, String> loadJSON() throws IOException {
        try (Reader reader = new FileReader(whitelistFile.toString())) {
            Gson gson = new Gson();
            HashMap<UUID, String> json = gson.fromJson(reader, jsonType);
            if (json == null) {
                return new HashMap<>();
            }
            return json;
        }
    }

    public WhitelistPlayer get(UUID uuid) {
        return new WhitelistPlayer(uuid, this.whitelistMap.get(uuid));
    }

    public WhitelistPlayer get(String username) {
        return new WhitelistPlayer(this.whitelistUsernameMap.get(username), username);
    }

    public Set<UUID> getUUIDs() {
        return this.whitelistMap.keySet();
    }

    public Set<String> getUsernames() {
        return this.whitelistUsernameMap.keySet();
    }


    public boolean has(UUID uuid) {
        return this.whitelistMap.containsKey(uuid);
    }

    public boolean has(String username) {
        return this.whitelistUsernameMap.containsKey(username);
    }


    public void add(WhitelistPlayer whitelistPlayer) throws IOException {
        this.whitelistMap.put(whitelistPlayer.uuid(), whitelistPlayer.username());
        this.whitelistUsernameMap.put(whitelistPlayer.username(), whitelistPlayer.uuid());
        saveJSON();
    }

    public void add(UUID uuid, String username) throws IOException {
        this.whitelistMap.put(uuid, username);
        this.whitelistUsernameMap.put(username, uuid);
        saveJSON();
    }


    public void remove(UUID uuid) throws IOException {
        this.whitelistUsernameMap.remove(this.whitelistMap.get(uuid));
        this.whitelistMap.remove(uuid);
        saveJSON();
    }

    public void remove(String username) throws IOException {
        this.whitelistMap.remove(this.whitelistUsernameMap.get(username));
        this.whitelistUsernameMap.remove(username);
        saveJSON();
    }
}
