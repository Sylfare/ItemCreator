package re.sylfa.itemcreator.deserializers.types;

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
        return NamespacedKey.fromString(p.getValueAsString());
    }
}
