package re.sylfa.itemcreator.deserializers.components;

import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Color;
import re.sylfa.itemcreator.util.Parsers;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Arrays;

public class CustomModelDataDeserializer extends StdDeserializer<CustomModelData> {
    public CustomModelDataDeserializer() {
        super(CustomModelData.class);
    }

    @Override
    public CustomModelData deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        CustomModelData.Builder builder = CustomModelData.customModelData();

        Parsers.getNodeValue(node, "flags", JsonNode::isArray, Boolean[].class)
            .ifPresent(flags -> builder.addFlags(Arrays.asList(flags)));
        Parsers.getNodeValue(node, "floats", JsonNode::isArray, Float[].class)
            .ifPresent(floats -> builder.addFloats(Arrays.asList(floats)));
        Parsers.getNodeValue(node, "colors", JsonNode::isArray, Color[].class)
            .ifPresent(colors -> builder.addColors(Arrays.asList(colors)));
        Parsers.getNodeValue(node, "strings", JsonNode::isArray, String[].class)
            .ifPresent(strings -> builder.addStrings(Arrays.asList(strings)));

        return builder.build();
    }
}
