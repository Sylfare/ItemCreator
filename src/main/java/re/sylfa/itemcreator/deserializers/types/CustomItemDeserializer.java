package re.sylfa.itemcreator.deserializers.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.DeathProtection;
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


        Parsers.getNodeComponentValue(node, "itemName")
            .ifPresent(itemName -> item.editMeta(ItemMeta.class, meta -> meta.itemName(itemName)));
        Parsers.getComponentArrayNodeValue(node, "lore")
            .ifPresent(loreLines -> item.editMeta(itemMeta -> itemMeta.lore(List.of(loreLines))));
        Parsers.getNodeIntValue(node, "maxStackSize")
            .ifPresent(maxStackSize -> item.editMeta(itemMeta -> itemMeta.setMaxStackSize(maxStackSize)));
        Parsers.getNodeNamespacedKeyValue(node, "itemModel")
            .ifPresent(modelKey -> item.editMeta(itemMeta -> itemMeta.setItemModel(modelKey)));
        Parsers.getNodeValue(node, "equippable", JsonNode::isObject, EquippableComponent.class)
            .ifPresent(equippable -> item.editMeta(itemMeta -> itemMeta.setEquippable(equippable)));
        Parsers.getNodeIntValue(node, "maxDamage")
            .ifPresent(maxDamage -> item.editMeta(Damageable.class, itemMeta -> itemMeta.setMaxDamage(maxDamage)));
        Parsers.getNodeValue(node, "tool", JsonNode::isObject, ToolComponent.class)
            .ifPresent(tool -> item.editMeta(itemMeta -> itemMeta.setTool(tool)));
        Parsers.getBooleanNodeValue(node, "glider")
            .ifPresent(glider -> item.editMeta(itemMeta -> itemMeta.setGlider(glider)));
        Parsers.getBooleanNodeValue(node, "enchantmentGlintOverride")
            .ifPresent(override -> item.editMeta(itemMeta -> itemMeta.setEnchantmentGlintOverride(override)));
        Parsers.getNodeValue(node, "rarity", JsonNode::isTextual, ItemRarity.class)
            .ifPresent(rarity -> item.editMeta(itemMeta -> itemMeta.setRarity(rarity)));
        Parsers.getNodeValue(node, "jukeboxPlayable", JsonNode::isTextual, JukeboxPlayableComponent.class)
            .ifPresent(song -> item.editMeta(itemMeta -> itemMeta.setJukeboxPlayable(song)));
        Parsers.getNodeIntValue(node, "amount").ifPresent(item::setAmount);
        Parsers.getNodeValue(node, "food", JsonNode::isObject, FoodComponent.class)
            .ifPresent(food -> item.editMeta(itemMeta -> itemMeta.setFood(food)));
        Parsers.getNodeValue(node, "consumable", JsonNode::isObject, Consumable.class)
            .ifPresent(consumable -> item.setData(DataComponentTypes.CONSUMABLE, consumable));
        Parsers.getNodeIntValue(node, "damage")
            .ifPresent(damage -> item.editMeta(Damageable.class, itemMeta -> itemMeta.setDamage(damage)));
        Parsers.getNodeValue(node, "deathProtection", JsonNode::isArray, DeathProtection.class)
            .ifPresent(deathProtection -> item.setData(DataComponentTypes.DEATH_PROTECTION, deathProtection));


        CustomItem.Builder builder = CustomItem.builder()
            .item(item);
        Parsers.getNodeItemKeyValue(node, "useRemainder").ifPresent(builder::useRemainder);
        return builder.build();
    }

}
