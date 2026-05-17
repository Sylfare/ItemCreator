package re.sylfa.itemcreator.deserializers.components;

import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import re.sylfa.itemcreator.ItemCreator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Optional;

public class DeathProtectionDeserializer extends StdDeserializer<DeathProtection> {
    public DeathProtectionDeserializer() { super(DeathProtection.class); }

    @Override
    public DeathProtection deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        if(node == null || !node.isArray()) {
            return null;
        }

        DeathProtection.Builder builder = DeathProtection.deathProtection();

        for(JsonNode effect : node) {
            Optional.ofNullable(ItemCreator.getMapper().convertValue(effect, ConsumeEffect.class))
                .ifPresent(builder::addEffect);
        }

        return builder.build();
    }
}
