package best.boba.bobawhitelist;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.format.NamedTextColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WhitelistCommand {
    private final Config config;
    public WhitelistCommand(Config config) {
        this.config = config;
    }

    public void register() {
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

                            try {
                                this.config.getWhitelist().add(whitelistPlayer.getUUID());
                                this.config.getUsernameCache().add(whitelistPlayer.getUUID(), whitelistPlayer.getUsername());
                                sender.sendMessage(Component.text(
                                        String.format("Added %s (%s) to the whitelist",
                                                whitelistPlayer.getUsername(), whitelistPlayer.getUUID())
                                ));
                                return 1;
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                sender.sendMessage(Component.text(
                                        "Failed to get the online UUID of " + username
                                ));
                                return 0;
                            }
                            catch (DuplicatePlayerException e) {
                                sender.sendMessage(Component.text(
                                        "That player is already whitelisted"
                                ).color(NamedTextColor.RED));
                                return 0;
                            }
                        })
                ).build();

        LiteralCommandNode<CommandSource> removeCommand = LiteralArgumentBuilder
                .<CommandSource>literal("remove")
                .then(RequiredArgumentBuilder
                        .<CommandSource, String>argument("player", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            for (String username : Utils.getUsernamesFromUUIDs(
                                    this.config.getUsernameCache(),
                                    this.config.getWhitelist().getUUIDs())) {
                                if (username.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(username);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(c -> {
                            CommandSource sender = c.getSource();
                            String username = c.getArgument("player", String.class);
                            UUID uuid = this.config.getUsernameCache().getUUID(username);
                            Whitelist whitelist = this.config.getWhitelist();
                            if (!whitelist.has(uuid)) {
                                sender.sendMessage(Component.text(
                                        "Player is not whitelisted"
                                ).color(NamedTextColor.RED));
                                return 0;
                            }

                            try {
                                this.config.getWhitelist().remove(uuid);
                                this.config.getUsernameCache().remove(uuid);
                                sender.sendMessage(Component.text(
                                        "Removed " + username + " from the whitelist"
                                ));
                                return 1;
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                sender.sendMessage(Component.text(
                                        "Failed to save whitelist.json."
                                ).color(NamedTextColor.RED));
                                return 0;
                            }
                        })
                ).build();

        LiteralCommandNode<CommandSource> listCommand = LiteralArgumentBuilder
                .<CommandSource>literal("list")
                .executes(c -> {
                    CommandSource sender = c.getSource();
                    List<UUID> whitelist = this.config.getWhitelist().getUUIDs();
                    int whitelistCount = whitelist.size();
                    List<String> whitelistedPlayers = new ArrayList<>();
                    for (UUID uuid : whitelist) {
                        whitelistedPlayers.add(this.config.getUsernameCache().getUsername(uuid));
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

        // the below command was for testing
//        LiteralCommandNode<CommandSource> changeNameCommand = LiteralArgumentBuilder
//                .<CommandSource>literal("changeName")
//                .then(RequiredArgumentBuilder
//                        .<CommandSource, String>argument("oldName", StringArgumentType.string())
//                        .then(RequiredArgumentBuilder
//                                .<CommandSource, String>argument("newName", StringArgumentType.string())
//                                .executes(context -> {
//                                    CommandSource sender = context.getSource();
//                                    String oldName = context.getArgument("oldName", String.class);
//                                    String newName = context.getArgument("newName", String.class);
//                                    UUID oldUUID = this.config.getUsernameCache().getUUID(oldName);
//                                    this.config.getUsernameCache().updateUsername(oldUUID, newName);
//                                    return 1;
//                                })
//                        )
//                ).build();

        LiteralCommandNode<CommandSource> reloadCommand = LiteralArgumentBuilder
                .<CommandSource>literal("reload")
                .executes(context -> {
                    CommandSource sender = context.getSource();
                    try {
                        this.config.reloadWhitelist();
                        sender.sendMessage(Component.text(
                                "Reloaded whitelist from disk"
                        ));
                        return 1;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        sender.sendMessage(Component.text(
                                "Error reloading whitelist from disk: " + e.getMessage()
                        ).color(NamedTextColor.RED));

                        sender.sendMessage(Component.text(
                                "See the console for more information."
                        ).color(NamedTextColor.RED));
                        return 0;
                    }
                })
                .build();

        rootNode.addChild(addCommand);
        rootNode.addChild(listCommand);
        rootNode.addChild(removeCommand);
//        rootNode.addChild(changeNameCommand);
        rootNode.addChild(reloadCommand);

        CommandManager commandManager = this.config.getServer().getCommandManager();
        BrigadierCommand brigadierCommand = new BrigadierCommand(rootNode);
        CommandMeta commandMeta = commandManager.metaBuilder(brigadierCommand)
                        .aliases("vwl").build();
        commandManager.register(commandMeta, brigadierCommand);
    }
}
