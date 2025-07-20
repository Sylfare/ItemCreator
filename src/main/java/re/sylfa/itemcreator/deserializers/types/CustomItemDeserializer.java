package re.sylfa.itemcreator.deserializers.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import net.minecraft.core.component.DataComponents;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import re.sylfa.itemcreator.items.CustomItem;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;
import java.util.List;

public class CustomItemDeserializer extends StdDeserializer<CustomItem> {

    public CustomItemDeserializer() {
        super(CustomItem.class);
    }

    @Override
    public CustomItem deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.readValueAsTree();
        if(node == null) {
            return null;
        }
        Material material = Parsers.getMaterialNodeValue(node, "material")
            .orElse(Material.WOODEN_PICKAXE);

        var itemNms = CraftItemStack.asNMSCopy(new ItemStack(material));

        // setMaxDamage(null) only resets the *patched* value, not the actual one
        itemNms.remove(DataComponents.MAX_DAMAGE);
        itemNms.remove(DataComponents.TOOL);
        itemNms.remove(DataComponents.WEAPON);
        itemNms.remove(DataComponents.ATTRIBUTE_MODIFIERS);
        itemNms.remove(DataComponents.FOOD);
        itemNms.remove(DataComponents.REPAIRABLE);
        ItemStack item = itemNms.asBukkitCopy();


        parseItemName(node, item);
        parseLore(node, item);
        parseMaxStackSize(node, item);
        parseItemModel(node, item);
        parseEquippable(node, item);
        parseMaxDamage(node, item);
        parseTool(node, item);
        parseGlider(node, item);
        parseEnchantmentGlintOverride(node, item);
        parseRarity(node, item);
        parseJukeboxPlayable(node, item);
        parseAmount(node, item);
        parseFood(node, item);
        parseConsumable(node, item);


        CustomItem.Builder builder = CustomItem.builder()
            .item(item);
        Parsers.getNodeItemKeyValue(node, "useRemainder").ifPresent(builder::useRemainder);
        return builder.build();
    }

    void parseConsumable(JsonNode node, ItemStack item) {
        Parsers.getNodeValue(node, "consumable", JsonNode::isObject, Consumable.class)
            .ifPresent(consumable -> item.setData(DataComponentTypes.CONSUMABLE, consumable));
    }

    void parseFood(JsonNode node, ItemStack item) {
        Parsers.getNodeValue(node, "food", JsonNode::isObject, FoodComponent.class)
            .ifPresent(food -> item.editMeta(itemMeta -> itemMeta.setFood(food)));
    }

    void parseAmount(JsonNode node, ItemStack item) {
        Parsers.getNodeIntValue(node, "amount").ifPresent(item::setAmount);
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

    void parseEquippable(JsonNode node, ItemStack item) {
        Parsers.getNodeValue(node, "equippable", JsonNode::isObject, EquippableComponent.class)
            .ifPresent(equippable -> item.editMeta(itemMeta -> itemMeta.setEquippable(equippable)));
    }

    void parseMaxDamage(JsonNode node, ItemStack item) {
        Parsers.getNodeIntValue(node, "maxDamage")
            .ifPresent(maxDamage -> item.editMeta(Damageable.class, itemMeta -> itemMeta.setMaxDamage(maxDamage)));
    }

    void parseTool(JsonNode node, ItemStack item) {
        Parsers.getNodeValue(node, "tool", JsonNode::isObject, ToolComponent.class)
            .ifPresent(tool -> item.editMeta(itemMeta -> itemMeta.setTool(tool)));
    }

    void parseGlider(JsonNode node, ItemStack item) {
        Parsers.getBooleanNodeValue(node, "glider")
            .ifPresent(glider -> item.editMeta(itemMeta -> itemMeta.setGlider(glider)));
    }

    void parseEnchantmentGlintOverride(JsonNode node, ItemStack item) {
        Parsers.getBooleanNodeValue(node, "enchantmentGlintOverride")
            .ifPresent(override -> item.editMeta(itemMeta -> itemMeta.setEnchantmentGlintOverride(override)));
    }

    void parseRarity(JsonNode node, ItemStack item) {
        Parsers.getNodeValue(node, "rarity", JsonNode::isTextual, ItemRarity.class)
            .ifPresent(rarity -> item.editMeta(itemMeta -> itemMeta.setRarity(rarity)));
    }

    void parseJukeboxPlayable(JsonNode node, ItemStack item) {
        Parsers.getNodeValue(node, "jukeboxPlayable", JsonNode::isTextual, JukeboxPlayableComponent.class)
            .ifPresent(song -> item.editMeta(itemMeta -> itemMeta.setJukeboxPlayable(song)));
    }
}
