package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class PotionEffectTypeDeserializer extends StdDeserializer<PotionEffectType> {
    public PotionEffectTypeDeserializer() { super(PotionEffectType.class); }

    @Override
    public PotionEffectType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if(node == null || !node.isTextual()) {
            return null;
        }
        Key key = Key.key(node.asText());
        return Registry.MOB_EFFECT.get(key);
    }
}
