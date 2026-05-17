package re.sylfa.itemcreator.deserializers.types;

import org.bukkit.inventory.ItemStack;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.items.ItemKey;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

public class ItemDeserializer extends StdDeserializer<ItemStack> {
    public ItemDeserializer() { super(ItemStack.class); }

    @Override
    public ItemStack deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        if(node == null || !node.isObject()) {
            return null;
        }

        ItemKey itemKey = ItemCreator.getMapper().convertValue(node, ItemKey.class);
        if(itemKey != null) {
            return itemKey.asItemStack();
        } else {
            return null;
        }
    }
}
