package best.boba.bobawhitelist;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.velocitypowered.api.util.UuidUtils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {
    // https://github.com/xElementzx/VelocityUuidGetter/blob/ce8d51f1897f1fbeeec9ca6d4e69c3f90fa33758/src/main/java/me/xelementzx/velocityuuidgetter/commands/GetUuidCommand.java#L48-L59
    public static @Nullable UUID getUUIDFromUsername(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            URLConnection urlConnection = url.openConnection();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                Optional<JsonObject> optionalJsonObject = parseJson(bufferedReader.lines().collect(Collectors.joining()), JsonObject.class);
                if (optionalJsonObject.isEmpty()) {
                    return null;
                }
                JsonObject jsonObject = optionalJsonObject.get();

                Optional<String> optionalUUIDString = parseJson(jsonObject.get("id"), String.class);
                if (optionalUUIDString.isEmpty()) {
                    return null;
                }
                UUID uuid = UuidUtils.fromUndashed(optionalUUIDString.get());

                return uuid;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // https://github.com/xElementzx/VelocityUuidGetter/blob/ce8d51f1897f1fbeeec9ca6d4e69c3f90fa33758/src/main/java/me/xelementzx/velocityuuidgetter/commands/GetUuidCommand.java#L61-L67
    private static <T> Optional<T> parseJson(String json, Class<T> type) {
        try {
            return parseJson(JsonParser.parseString(json), type);
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    // https://github.com/xElementzx/VelocityUuidGetter/blob/ce8d51f1897f1fbeeec9ca6d4e69c3f90fa33758/src/main/java/me/xelementzx/velocityuuidgetter/commands/GetUuidCommand.java#L69-L76
    private static <T> Optional<T> parseJson(JsonElement jsonElement, Class<T> type) {
        try {
            return Optional.of(new Gson().fromJson(jsonElement, type));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }
}
