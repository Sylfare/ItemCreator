package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.papermc.paper.datacomponent.item.Enchantable;
import org.checkerframework.checker.nullness.qual.Nullable;
import re.sylfa.itemcreator.util.Log;

import java.io.IOException;

public class EnchantableDeserializer extends StdDeserializer<Enchantable> {
    private static final int NOT_FOUND = -1;
    public EnchantableDeserializer() {
        super(Enchantable.class);
    }

    @Override @Nullable
    public Enchantable deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.readValueAsTree();
        if(node == null) {
            return null;
        }

        boolean enabled = node.get("enabled").asBoolean(false);
        if(!enabled) {
            return null;
        }

        int value = node.get("value").asInt(NOT_FOUND);
        if(value <= 0) {
            Log.error("%s is not valid for enchantable value");
            return null;
        }

        return Enchantable.enchantable(value);
    }
}
