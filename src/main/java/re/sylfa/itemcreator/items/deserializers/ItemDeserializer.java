package re.sylfa.itemcreator.items.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import re.sylfa.itemcreator.ItemCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemDeserializer extends StdDeserializer<ItemStack> {

    ObjectMapper mapper = ItemCreator.getMapper();

    public ItemDeserializer() {
        super(ItemStack.class);
    }

    @Override
    public ItemStack deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.readValueAsTree();
        Material material = parseMaterial(node);


        var itemNms = CraftItemStack.asNMSCopy(new ItemStack(material));

        // setMaxDamage(null) only resets the *patched* value, not the actual one
        itemNms.remove(DataComponents.MAX_DAMAGE);
        itemNms.remove(DataComponents.TOOL);
        itemNms.remove(DataComponents.ATTRIBUTE_MODIFIERS);
        itemNms.remove(DataComponents.FOOD);
        ItemStack item = itemNms.asBukkitCopy();


        parseItemName(node, item);
        parseLore(node, item);

        return item;
    }

    Material parseMaterial(JsonNode node) {
        return mapper.convertValue(node.get("material"), Material.class);
    }

    void parseLore(JsonNode node, ItemStack item) {
        JsonNode loreNode = node.get("lore");
        if(loreNode != null && !loreNode.isEmpty()){
            List<Component> lore = new ArrayList<>();
            if(loreNode.isArray()) {
                for(JsonNode line : loreNode) {
                    lore.add(mapper.convertValue(line, Component.class));
                }
            } else {
                lore.add(mapper.convertValue(loreNode, Component.class));
            }
            item.editMeta(ItemMeta.class, meta -> meta.lore(lore));
        }
    }

    void parseItemName(JsonNode node, ItemStack item) {
        if(node.get("itemName").isTextual()) {
            Component itemName = mapper.convertValue(node.get("itemName"), Component.class);
            item.editMeta(ItemMeta.class, meta -> meta.itemName(itemName));
        }
    }
}
