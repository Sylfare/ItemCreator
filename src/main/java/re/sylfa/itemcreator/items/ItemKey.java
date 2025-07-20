package re.sylfa.itemcreator.items;

import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import re.sylfa.itemcreator.ItemCreator;

public record ItemKey(Key key, int amount) {
    public ItemStack asItemStack() {
        ItemStack parsedItem = ItemCreator.getItemRegistry().parse(key);
        if(parsedItem != null) {
            return parsedItem.asQuantity(amount);
        } else {
            return null;
        }
    }
}
