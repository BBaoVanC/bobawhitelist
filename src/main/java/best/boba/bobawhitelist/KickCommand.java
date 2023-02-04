package best.boba.bobawhitelist;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.format.NamedTextColor;

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
                        .suggests((context, builder) -> {
                            for (Player player : this.proxy.getAllPlayers()) {
                                builder.suggest(player.getUsername());
                            }
                            return builder.buildFuture();
                        })
                        .executes(c -> {
                            CommandSource sender = c.getSource();
                            String targetArg = c.getArgument("player", String.class);
                            Player target = proxy.getPlayer(targetArg).orElse(null);
                            if (target == null) {
                                sender.sendMessage(Component.text(
                                    String.format("Player %s was not found", targetArg)
                                ).color(NamedTextColor.RED));
                                return 0;
                            }

                            target.disconnect(Component.text("You were kicked from the network."));
                            sender.sendMessage(Component.text(
                                String.format("Kicked %s from the proxy", target.getUsername())
                            ));
                            return 1;
                        })
                ).build();

        CommandManager commandManager = this.proxy.getCommandManager();
        BrigadierCommand brigadierCommand = new BrigadierCommand(rootNode);
        CommandMeta commandMeta = commandManager.metaBuilder(brigadierCommand).build();
        commandManager.register(commandMeta, brigadierCommand);
    }
}
