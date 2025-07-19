package re.sylfa.itemcreator.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.minecraft.core.component.DataComponents;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;
import java.util.List;

public class ItemDeserializer extends StdDeserializer<ItemStack> {

    public ItemDeserializer() {
        super(ItemStack.class);
    }

    @Override
    public ItemStack deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.readValueAsTree();
        Material material = Parsers.parseMaterial(node, "material")
            .orElse(Material.WOODEN_PICKAXE);

        var itemNms = CraftItemStack.asNMSCopy(new ItemStack(material));

        // setMaxDamage(null) only resets the *patched* value, not the actual one
        itemNms.remove(DataComponents.MAX_DAMAGE);
        itemNms.remove(DataComponents.TOOL);
        itemNms.remove(DataComponents.ATTRIBUTE_MODIFIERS);
        itemNms.remove(DataComponents.FOOD);
        itemNms.remove(DataComponents.REPAIRABLE);
        ItemStack item = itemNms.asBukkitCopy();


        parseItemName(node, item);
        parseLore(node, item);
        parseMaxStackSize(node, item);
        parseItemModel(node, item);

        return item;
    }


    void parseLore(JsonNode node, ItemStack item) {
        Parsers.getComponentArrayNodeValue(node, "lore")
            .ifPresent(loreLines -> item.editMeta(itemMeta -> itemMeta.lore(List.of(loreLines))));
    }

    void parseItemName(JsonNode node, ItemStack item) {
        Parsers.getNodeComponentValue(node, "itemName")
            .ifPresent(itemName -> item.editMeta(ItemMeta.class, meta -> meta.itemName(itemName)));
    }

    void parseMaxStackSize(JsonNode node, ItemStack item) {
        Parsers.getNodeIntValue(node, "maxStackSize")
            .ifPresent(maxStackSize -> item.editMeta(itemMeta -> itemMeta.setMaxStackSize(maxStackSize)));
    }

    void parseItemModel(JsonNode node, ItemStack item) {
        Parsers.getNodeNamespacedKeyValue(node, "itemModel")
            .ifPresent(modelKey -> item.editMeta(itemMeta -> itemMeta.setItemModel(modelKey)));
    }
}
