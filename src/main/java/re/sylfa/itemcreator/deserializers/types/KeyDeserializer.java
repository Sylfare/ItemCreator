package re.sylfa.itemcreator.deserializers.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.kyori.adventure.key.Key;

import java.io.IOException;

public class KeyDeserializer extends StdDeserializer<Key> {

    public KeyDeserializer() { super(Key.class); }

    @Override
    public Key deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return Key.key(p.getValueAsString());
    }
}
