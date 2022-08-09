package best.boba.bobawhitelist;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.Optional;
import java.util.UUID;

public class KickCommand {
    private final ProxyServer proxy;

    public KickCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    public void register() {
        LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder
                .<CommandSource>literal("vkick")
                .requires(sender -> sender.hasPermission("bobawhitelist.kick"))
                .then(RequiredArgumentBuilder
                        .<CommandSource, String>argument("player", StringArgumentType.string())
                        .executes(c -> {
                            CommandSource sender = c.getSource();
                            String targetArg = c.getArgument("player", String.class);
//                            Optional<Player> optionalPlayer = proxy.getPlayer(target).orElse(proxy.getPlayer(UUID.fromString(target))).orElse(null);
                            Player target = proxy.getPlayer(targetArg).orElse(null);
                            if (target == null) {
                                return 0;
                            }

                            // TODO
                            return 1;
                        })
                ).build();
    }
}
