package re.sylfa.itemcreator.deserializers.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;
import java.util.Optional;

public class ItemDeserializer extends StdDeserializer<ItemStack> {
    public ItemDeserializer() { super(ItemStack.class); }

    @Override
    public ItemStack deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode treeNode = p.readValueAsTree();
        Optional<Key> key;
        Optional<Integer> amount;
        if(treeNode.isObject()) {
            key = Parsers.getNodeKeyValue(treeNode, "id");
            amount = Parsers.getNodeIntValue(treeNode, "amount");
        } else if (treeNode.isTextual() && !treeNode.asText().isBlank()){
            key = Optional.of(Key.key(treeNode.asText()));
            amount = Optional.of(1);
        } else {
            return null;
        }
        if(key.isEmpty()) {
            return null;
        }
        ItemStack parsedItem = ItemCreator.getItemRegistry().parse(key.get());
        if(parsedItem != null) {
            return parsedItem.asQuantity(amount.orElse(1));
        } else {
            return null;
        }
    }
}
