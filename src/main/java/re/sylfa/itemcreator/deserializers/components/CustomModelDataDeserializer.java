package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Color;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;
import java.util.Arrays;

public class CustomModelDataDeserializer extends StdDeserializer<CustomModelData> {
    public CustomModelDataDeserializer() {
        super(CustomModelData.class);
    }

    @Override
    public CustomModelData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
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
