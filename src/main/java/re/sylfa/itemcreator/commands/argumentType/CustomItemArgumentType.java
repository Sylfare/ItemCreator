package re.sylfa.itemcreator.commands.argumentType;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.key.Key;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.items.CustomItem;

public class CustomItemArgumentType implements CustomArgumentType.Converted<CustomItem, Key>{

    public static CustomItemArgumentType customItem() {
        return new CustomItemArgumentType();
    }
    
    @Override
    public @NotNull ArgumentType<Key> getNativeType() {
        return ArgumentTypes.key();
    }

    @Override
    public @NotNull CustomItem convert(@NotNull Key key) throws CommandSyntaxException {
        return ItemCreator.getItemRegistry().get(key);
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context,
            @NotNull SuggestionsBuilder builder) {
        Stream<CustomItem> stream = ItemCreator.getItemRegistry().getAll().values().stream();
        if(!builder.getRemaining().isBlank()) {
            stream = stream.filter(item -> item.key().toString().contains(builder.getRemaining()));
        }

        stream.forEach(item -> builder.suggest(item.key().toString()));
        return builder.buildFuture();
    }
    
}
