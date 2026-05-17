package re.sylfa.itemcreator.deserializers.components;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.ToolComponent;
import re.sylfa.itemcreator.util.Parsers;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

public class ToolDeserializer extends StdDeserializer<ToolComponent> {

    public ToolDeserializer() {
        super(ToolComponent.class);
    }

    @Override
    public ToolComponent deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        if(node == null) {
            return null;
        }
        ToolComponent toolComponent = new ItemStack(Material.STONE).getItemMeta().getTool();
        Parsers.getIntNodeValue(node, "damagePerBlock").ifPresent(toolComponent::setDamagePerBlock);
        Parsers.getFloatNodeValue(node, "defaultMiningSpeed").ifPresent(toolComponent::setDefaultMiningSpeed);
        Parsers.getToolRulesArrayNodeValue(node, "rules")
            .ifPresent(rulesList -> rulesList.forEach(ruleValue -> toolComponent.addRule(ruleValue.block(), ruleValue.speed(), ruleValue.correctForDrops())));


        return toolComponent;
    }
}
