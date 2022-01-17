package best.boba.bobawhitelist;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class WhitelistCommand {
    public static void createBrigadierCommand(ProxyServer server, Config config, CommandManager commandManager) {
        LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder
                .<CommandSource>literal("vwhitelist")
                .requires(sender -> sender.hasPermission("bobawhitelist.whitelist"))
                .build();

        LiteralCommandNode<CommandSource> addCommand = LiteralArgumentBuilder
                .<CommandSource>literal("add")
                .then(RequiredArgumentBuilder.
                        <CommandSource, String>argument("player", StringArgumentType.string())
                        .executes(c -> {
                            CommandSource sender = c.getSource();
                            String username = c.getArgument("player", String.class);
                            WhitelistPlayer whitelistPlayer = Utils.getOnlinePlayer(username);
                            if (whitelistPlayer == null) {
                                sender.sendMessage(Component.text(
                                        "Failed to get the online UUID of " + username
                                ).color(NamedTextColor.RED));
                                return 0;
                            }

                            config.getWhitelist().add(whitelistPlayer);
                            sender.sendMessage(Component.text(
                                    String.format("Added %s (%s) to the whitelist",
                                            whitelistPlayer.getUsername(), whitelistPlayer.getUUID())
                            ));
                            return 1;
                        })
                ).build();

        LiteralCommandNode<CommandSource> removeCommand = LiteralArgumentBuilder
                .<CommandSource>literal("remove")
                .then(RequiredArgumentBuilder
                        .<CommandSource, String>argument("player", StringArgumentType.string())
                        .suggests(((context, builder) -> {
                            for (String username : config.getWhitelist().getUsernames()) {
                                builder.suggest(username);
                            }
                            return builder.buildFuture();
                        }))
                        .executes(c -> {
                            CommandSource sender = c.getSource();
                            String username = c.getArgument("player", String.class);
                            Whitelist whitelist = config.getWhitelist();
                            if (!whitelist.has(username)) {
                                sender.sendMessage(Component.text(
                                        "Player is not whitelisted"
                                ).color(NamedTextColor.RED));
                                return 0;
                            }
                            config.getWhitelist().remove(username);
                            sender.sendMessage(Component.text(
                                    "Removed " + username + " from the whitelist"
                            ));
                            return 1;
                        })
                ).build();

        LiteralCommandNode<CommandSource> listCommand = LiteralArgumentBuilder
                .<CommandSource>literal("list")
                .executes(c -> {
                    CommandSource sender = c.getSource();
                    List<WhitelistPlayer> whitelist = config.getWhitelist().getList();
                    int whitelistCount = whitelist.size();
                    List<String> whitelistedPlayers = new ArrayList<>();
                    for (WhitelistPlayer whitelistPlayer : whitelist) {
                        whitelistedPlayers.add(whitelistPlayer.getUsername());
                    }

                    if (whitelistCount > 0) {
                        sender.sendMessage(Component.text(
                                String.format("There are %d whitelisted players: %s", whitelistCount, String.join(", ", whitelistedPlayers))
                        ));
                    } else {
                        sender.sendMessage(Component.text("There are no whitelisted players"));
                    }
                    return 1;
                }).build();

        rootNode.addChild(addCommand);
        rootNode.addChild(listCommand);
        rootNode.addChild(removeCommand);
        commandManager.register(new BrigadierCommand(rootNode));
    }
}
