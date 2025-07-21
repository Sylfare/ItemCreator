package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import re.sylfa.itemcreator.util.Log;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;
import java.util.Optional;

public class PotionEffectDeserializer extends StdDeserializer<PotionEffect> {
    public PotionEffectDeserializer() { super(PotionEffect.class); }

    @Override
    public PotionEffect deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if(node == null || !node.isObject()) {
            return null;
        }
        Optional<PotionEffectType> type = Parsers.getNodeValue(node, "type", JsonNode::isTextual, PotionEffectType.class);
        Optional<Integer> duration = Parsers.getNodeIntValue(node, "duration");
        Optional<Integer> amplifier = Parsers.getNodeIntValue(node, "amplifier");
        boolean ambient = Parsers.getBooleanNodeValue(node, "ambient").orElse(true);
        boolean particles = Parsers.getBooleanNodeValue(node, "particles").orElse(true);
        boolean icon = Parsers.getBooleanNodeValue(node, "icon").orElse(true);

        if(type.isEmpty()) {
            Log.error("Potion type missing");
            return null;
        }

        if(duration.isEmpty()) {
            Log.error("Potion duration missing");
            return null;
        }

        if(amplifier.isEmpty()) {
            Log.error("Potion amplifier missing");
            return null;
        }

        return new PotionEffect(type.get(), duration.get(), amplifier.get(), ambient, particles, icon);
    }
}
