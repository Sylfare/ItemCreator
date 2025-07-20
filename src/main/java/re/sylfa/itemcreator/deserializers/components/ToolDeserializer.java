package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.ToolComponent;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;

public class ToolDeserializer extends StdDeserializer<ToolComponent> {

    public ToolDeserializer() {
        super(ToolComponent.class);
    }

    @Override
    public ToolComponent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if(node == null) {
            return null;
        }
        ToolComponent toolComponent = new ItemStack(Material.STONE).getItemMeta().getTool();
        Parsers.getNodeIntValue(node, "damagePerBlock").ifPresent(toolComponent::setDamagePerBlock);
        Parsers.getFloatNodeValue(node, "defaultMiningSpeed").ifPresent(toolComponent::setDefaultMiningSpeed);
        Parsers.getToolRulesArrayNodeValue(node, "rules")
            .ifPresent(rulesList -> rulesList.forEach(ruleValue -> toolComponent.addRule(ruleValue.block(), ruleValue.speed(), ruleValue.correctForDrops())));


        return toolComponent;
    }
}
