package re.sylfa.itemcreator.commands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.commands.argumentType.CustomItemArgumentType;
import re.sylfa.itemcreator.items.CustomItem;

public class ItemCreatorCommand implements Command{

    @Override
    public LiteralCommandNode<CommandSourceStack> command() {
        return literal("itemcreator")
        .requires(ctx -> ctx.getSender().hasPermission("itemcreator"))
        .executes(this::help)
        .then(literal("give")
            .then(argument("itemKey", CustomItemArgumentType.customItem())
                .executes(this::giveItem)
            )
        )
        .then(literal("reload").executes(this::reload))
        .build();
    }
    @Override
    public List<String> aliases() {
        return List.of("ic");
    }

    private int help(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().getSender().sendMessage("/itemcreator give <item>");
        return SINGLE_SUCCESS;
    }

    private int giveItem(CommandContext<CommandSourceStack> ctx) {
        if(ctx.getSource().getSender() instanceof Player player) {
            @SuppressWarnings("unchecked")
            Optional<CustomItem> item = ctx.getArgument("itemKey", Optional.class);
            if(item.isPresent()) {
                player.getInventory().addItem(item.get().asItemStack());
            } else {
                player.sendMessage(Component.text("Error: not a valid custom item.", NamedTextColor.RED));
            }
            
        }
        return SINGLE_SUCCESS;
    }

    private int reload(CommandContext<CommandSourceStack> ctx) {
        ItemCreator.getInstance().reload();
        ctx.getSource().getSender().sendMessage(Component.text("ItemCreator has been reloaded.", NamedTextColor.GREEN));
        return SINGLE_SUCCESS;
    }

    
    
}
