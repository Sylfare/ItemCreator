package re.sylfa.itemcreator.deserializers.components;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Log;
import re.sylfa.itemcreator.util.Parsers;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

public class ConsumableDeserializer extends StdDeserializer<Consumable> {
    public ConsumableDeserializer() { super(Consumable.class); }

    @Override
    public Consumable deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        if(node == null) {
            return null;
        }
        Consumable.Builder builder = Consumable.consumable();
        Parsers.getNodeValue(node, "animation", JsonNode::isString, ItemUseAnimation.class)
            .ifPresent(builder::animation);
        Parsers.getFloatNodeValue(node, "consumeSeconds").ifPresent(builder::consumeSeconds);
        Parsers.getBooleanNodeValue(node, "hasConsumeParticles").ifPresent(builder::hasConsumeParticles);
        Parsers.getKeyNodeValue(node, "sound").ifPresent(builder::sound);
        JsonNode effects = node.get("effects");
        if(effects != null) {
            if(effects.isArray()) {
                for(JsonNode effect : effects) {
                    ConsumeEffect consumeEffect = ItemCreator.getMapper().convertValue(effect, ConsumeEffect.class);
                    if(consumeEffect != null) {
                        builder.addEffect(consumeEffect);
                    }
                }
            } else {
                Log.error("Consumable effect is not valid");
            }
        }
        return builder.build();
    }
}
