package re.sylfa.itemcreator.deserializers.components;

import org.bukkit.Material;
import org.bukkit.inventory.meta.components.ToolComponent;
import re.sylfa.itemcreator.util.Parsers;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Optional;

public class ToolRuleDeserializer extends StdDeserializer<ToolRuleDeserializer.RuleValues> {
    public ToolRuleDeserializer() { super(ToolComponent.ToolRule.class); }

    @Override
    public RuleValues deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        Optional<Material> blockMaterial = Parsers.getMaterialNodeValue(node, "block");
        Optional<Float> speed = Parsers.getFloatNodeValue(node, "speed");
        Optional<Boolean> correctForDrops = Parsers.getBooleanNodeValue(node, "correctForDrops");
        if(blockMaterial.isEmpty() || speed.isEmpty()) {
            return null;
        }
        return new RuleValues(
            blockMaterial.get(),
            speed.get(),
            correctForDrops.orElse(false)
        );
    }

    public record RuleValues(Material block, float speed, boolean correctForDrops) {}
}
