package re.sylfa.itemcreator.deserializers.components;

import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

public class PotionEffectTypeDeserializer extends StdDeserializer<PotionEffectType> {
    public PotionEffectTypeDeserializer() { super(PotionEffectType.class); }

    @Override
    public PotionEffectType deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        if(node == null || !node.isString()) {
            return null;
        }
        Key key = Key.key(node.asString());
        return Registry.MOB_EFFECT.get(key);
    }
}
