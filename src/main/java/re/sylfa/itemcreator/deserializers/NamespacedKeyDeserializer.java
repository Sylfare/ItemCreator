package re.sylfa.itemcreator.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class NamespacedKeyDeserializer extends StdDeserializer<NamespacedKey> {
    public NamespacedKeyDeserializer() {
        super(NamespacedKey.class);
    }

    @Override
    public @Nullable NamespacedKey deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String parsedString = p.getValueAsString();
        return NamespacedKey.fromString(parsedString);
    }
}
