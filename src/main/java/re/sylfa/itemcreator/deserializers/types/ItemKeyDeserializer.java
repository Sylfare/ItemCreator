package re.sylfa.itemcreator.deserializers.types;

import net.kyori.adventure.key.Key;
import re.sylfa.itemcreator.items.ItemKey;
import re.sylfa.itemcreator.util.Parsers;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Optional;

public class ItemKeyDeserializer extends StdDeserializer<ItemKey> {
    public ItemKeyDeserializer() { super(ItemKey.class);}

    @Override
    public ItemKey deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        if(node == null || (!node.isString() && !node.isObject())) {
            return null;
        }

        Optional<Key> key;
        Optional<Integer> amount;
        if(node.isObject()) {
            key = Parsers.getKeyNodeValue(node, "id");
            amount = Parsers.getIntNodeValue(node, "amount");
        } else if (node.isString() && !node.asString().isBlank()){
            key = Optional.of(Key.key(node.asString()));
            amount = Optional.of(1);
        } else {
            return null;
        }
        if(key.isEmpty()) {
            return null;
        }

        return new ItemKey(key.get(), amount.orElse(1));
    }
}
