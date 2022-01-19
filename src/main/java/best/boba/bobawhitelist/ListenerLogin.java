package best.boba.bobawhitelist;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public class ListenerLogin {
    private final Config config;

    public ListenerLogin(Config config) {
        this.config = config;
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!config.getWhitelist().has(uuid)) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text(
                    "You are not whitelisted!"
            ).color(NamedTextColor.RED)));
        }
    }
}
