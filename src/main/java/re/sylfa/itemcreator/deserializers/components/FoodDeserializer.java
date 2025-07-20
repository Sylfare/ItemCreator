package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.FoodComponent;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;

public class FoodDeserializer extends StdDeserializer<FoodComponent> {

    public FoodDeserializer() { super(FoodComponent.class); }

    @Override
    public FoodComponent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if(node == null) {
            return null;
        }

        FoodComponent component = new ItemStack(Material.STONE).getItemMeta().getFood();
        Parsers.getBooleanNodeValue(node, "canAlwaysEat").ifPresent(component::setCanAlwaysEat);
        Parsers.getNodeIntValue(node, "nutrition").ifPresent(component::setNutrition);
        Parsers.getFloatNodeValue(node, "saturation").ifPresent(component::setSaturation);
        return component;
    }
}
