package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import re.sylfa.itemcreator.ItemCreator;

import java.io.IOException;
import java.util.Optional;

public class DeathProtectionDeserializer extends StdDeserializer<DeathProtection> {
    public DeathProtectionDeserializer() { super(DeathProtection.class); }

    @Override
    public DeathProtection deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
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
