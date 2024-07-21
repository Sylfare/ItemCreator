package re.sylfa.itemcreator.commands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

import java.util.List;

import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
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
            player.getInventory().addItem(ctx.getArgument("itemKey", CustomItem.class).asItemStack());
        }
        return SINGLE_SUCCESS;
    }

    
    
}
