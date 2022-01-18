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
    private final List<UUID> whitelist;

    private static final Type jsonType = new TypeToken<List<UUID>>(){}.getType();

    public Whitelist(Path whitelistFile) throws IOException {
        this.whitelistFile = whitelistFile;

        if (Files.notExists(whitelistFile)) {
            this.whitelist = new ArrayList<>();
        } else {
            this.whitelist = loadJSON();
        }
        saveJSON();
    }

    private void saveJSON() throws IOException {
        try (Writer writer = new FileWriter(this.whitelistFile.toString())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this.whitelist, writer);
        }
    }

    private List<UUID> loadJSON() throws IOException {
        try (Reader reader = new FileReader(this.whitelistFile.toString())) {
            Gson gson = new Gson();
            List<UUID> json = gson.fromJson(reader, jsonType);
            if (json == null) {
                return new ArrayList<>();
            }
            return json;
        }
    }

    public List<UUID> getUUIDs() {
        return this.whitelist;
    }


    public boolean has(UUID uuid) {
        return this.whitelist.contains(uuid);
    }


    public void add(UUID uuid) throws IOException, DuplicatePlayerException {
        if (has(uuid)) {
            throw new DuplicatePlayerException();
        }

        this.whitelist.add(uuid);
        saveJSON();
    }


    public void remove(UUID uuid) throws IOException {
        this.whitelist.remove(uuid);
        saveJSON();
    }
}
