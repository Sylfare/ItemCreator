package re.sylfa.itemcreator.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public interface Command {
    public LiteralCommandNode<CommandSourceStack> command();

    
    public default List<String> aliases() {
        return List.of();
    };
}
