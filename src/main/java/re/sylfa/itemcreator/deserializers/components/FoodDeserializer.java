package re.sylfa.itemcreator.deserializers.components;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.FoodComponent;
import re.sylfa.itemcreator.util.Parsers;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

public class FoodDeserializer extends StdDeserializer<FoodComponent> {

    public FoodDeserializer() { super(FoodComponent.class); }

    @Override
    public FoodComponent deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        if(node == null) {
            return null;
        }

        FoodComponent component = new ItemStack(Material.STONE).getItemMeta().getFood();
        Parsers.getBooleanNodeValue(node, "canAlwaysEat").ifPresent(component::setCanAlwaysEat);
        Parsers.getIntNodeValue(node, "nutrition").ifPresent(component::setNutrition);
        Parsers.getFloatNodeValue(node, "saturation").ifPresent(component::setSaturation);
        return component;
    }
}
