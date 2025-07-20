package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;

public class ConsumableDeserializer extends StdDeserializer<Consumable> {
    public ConsumableDeserializer() { super(Consumable.class); }

    @Override
    public Consumable deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if(node == null) {
            return null;
        }
        Consumable.Builder builder = Consumable.consumable();
        Parsers.getNodeValue(node, "animation", JsonNode::isTextual, ItemUseAnimation.class)
            .ifPresent(builder::animation);
        Parsers.getFloatNodeValue(node, "consumeSeconds").ifPresent(builder::consumeSeconds);
        Parsers.getBooleanNodeValue(node, "hasConsumeParticles").ifPresent(builder::hasConsumeParticles);
        Parsers.getNodeKeyValue(node, "sound").ifPresent(builder::sound);
        return builder.build();
    }
}
