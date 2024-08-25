package moe.seikimo.droplet.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

public interface Arguments {
    static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    static RequiredArgumentBuilder<CommandSource, Integer> integer(String name) {
        return RequiredArgumentBuilder.argument(name, IntegerArgumentType.integer());
    }
}
