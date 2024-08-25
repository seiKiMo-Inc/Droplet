package moe.seikimo.droplet.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import moe.seikimo.droplet.block.BlockPalette;

import static moe.seikimo.droplet.commands.Arguments.*;

public interface BlockCommand {
    static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                literal("block")
                        .then(literal("bedrock")
                                .then(integer("id")
                                        .executes(BlockCommand::bedrock))
                                .executes(c -> {
                                    c.getSource().sendMessage("Usage: /block bedrock <id>");
                                    return 1;
                                }))
                        .then(integer("x")
                                .then(integer("y")
                                        .then(integer("z")
                                                .executes(BlockCommand::execute))))
                        .executes(c -> {
                            c.getSource().sendMessage("Usage: /block <x> <y> <z>");
                            return 1;
                        })
        );
    }

    static int execute(CommandContext<CommandSource> context) {
        var server = context.getSource().asServer();
        var world = server.getDefaultWorld();

        var x = context.getArgument("x", Integer.class);
        var y = context.getArgument("y", Integer.class);
        var z = context.getArgument("z", Integer.class);

        var block = world.getBlockAt(x, y, z);
        context.getSource().sendMessage(STR."Block: \{block.toString()}");

        return 1;
    }

    static int bedrock(CommandContext<CommandSource> context) {
        var blockId = context.getArgument("id", Integer.class);
        var definition = BlockPalette.getBedrockRegistry().getDefinition(blockId);

        context.getSource().sendMessage("Block definition for %s: %s"
                .formatted(blockId, definition.toString()));

        return 1;
    }
}
