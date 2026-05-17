package re.sylfa.itemcreator.deserializers.types;

import net.kyori.adventure.key.Key;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class KeyDeserializer extends StdDeserializer<Key> {

    public KeyDeserializer() { super(Key.class); }

    @Override
    public Key deserialize(JsonParser p, DeserializationContext ctxt) {
        return Key.key(p.getValueAsString());
    }
}
