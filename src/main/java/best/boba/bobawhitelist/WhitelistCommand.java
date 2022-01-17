package best.boba.bobawhitelist;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;

public class WhitelistCommand {
    public static void createBrigadierCommand(CommandManager commandManager) {
        LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder
                .<CommandSource>literal("vwhitelist")
                .requires(sender -> sender.hasPermission("bobawhitelist.whitelist"))
                .executes(command -> {
                    command.getSource().sendMessage(Component.text("hello"));
                    return 1;
                })
                .build();

        ArgumentCommandNode<CommandSource, String> subcommand = RequiredArgumentBuilder
                .<CommandSource, String>argument("subcommand", StringArgumentType.word())
                .suggests((context, builder) -> builder.buildFuture())
                .build();

        rootNode.addChild(subcommand);
        commandManager.register(new BrigadierCommand(rootNode));
    }
}
