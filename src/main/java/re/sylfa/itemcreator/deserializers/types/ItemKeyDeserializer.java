package re.sylfa.itemcreator.deserializers.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.kyori.adventure.key.Key;
import re.sylfa.itemcreator.items.ItemKey;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;
import java.util.Optional;

public class ItemKeyDeserializer extends StdDeserializer<ItemKey> {
    public ItemKeyDeserializer() { super(ItemKey.class);}

    @Override
    public ItemKey deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if(node == null || (!node.isTextual() && !node.isObject())) {
            return null;
        }

        Optional<Key> key;
        Optional<Integer> amount;
        if(node.isObject()) {
            key = Parsers.getKeyNodeValue(node, "id");
            amount = Parsers.getIntNodeValue(node, "amount");
        } else if (node.isTextual() && !node.asText().isBlank()){
            key = Optional.of(Key.key(node.asText()));
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
