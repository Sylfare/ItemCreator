package re.sylfa.itemcreator.deserializers.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.inventory.ItemStack;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.items.ItemKey;

import java.io.IOException;

public class ItemDeserializer extends StdDeserializer<ItemStack> {
    public ItemDeserializer() { super(ItemStack.class); }

    @Override
    public ItemStack deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
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
