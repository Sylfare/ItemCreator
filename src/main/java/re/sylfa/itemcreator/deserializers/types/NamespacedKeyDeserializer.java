package re.sylfa.itemcreator.deserializers.types;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class NamespacedKeyDeserializer extends StdDeserializer<NamespacedKey> {
    public NamespacedKeyDeserializer() {
        super(NamespacedKey.class);
    }

    @Override
    public @Nullable NamespacedKey deserialize(JsonParser p, DeserializationContext ctxt) {
        return NamespacedKey.fromString(p.getValueAsString());
    }
}
