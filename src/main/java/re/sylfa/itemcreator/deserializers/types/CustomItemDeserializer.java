package re.sylfa.itemcreator.deserializers.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.Enchantable;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.minecraft.core.component.DataComponents;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
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
import java.util.Objects;

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
        itemNms.remove(DataComponents.ENCHANTABLE);
        ItemStack item = itemNms.asBukkitCopy();

        Parsers.getIntNodeValue(node, "amount").ifPresent(item::setAmount);
        Parsers.getNodeValue(node, "attributes", JsonNode::isArray, ItemAttributeModifiers.class)
            .ifPresent(attributes -> item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributes));
        Parsers.getNodeValue(node, "baseColor", JsonNode::isTextual, DyeColor.class)
            .ifPresent(color -> item.setData(DataComponentTypes.BASE_COLOR, color));
        Parsers.getKeyNodeValue(node, "breakSound")
            .ifPresent(sound -> item.setData(DataComponentTypes.BREAK_SOUND, sound));
        Parsers.getNodeValue(node, "consumable", JsonNode::isObject, Consumable.class)
            .ifPresent(consumable -> item.setData(DataComponentTypes.CONSUMABLE, consumable));
        Parsers.getNodeValue(node, "customModelData", JsonNode::isObject, CustomModelData.class)
            .ifPresent(modelData -> item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, modelData));
        Parsers.getComponentNodeValue(node, "customName")
            .ifPresent(customName -> item.getItemMeta().displayName(customName));
        Parsers.getIntNodeValue(node, "damage")
            .ifPresent(damage -> item.editMeta(Damageable.class, itemMeta -> itemMeta.setDamage(damage)));
        Parsers.getNodeValue(node, "deathProtection", JsonNode::isArray, DeathProtection.class)
            .ifPresent(deathProtection -> item.setData(DataComponentTypes.DEATH_PROTECTION, deathProtection));
        Parsers.getColorNodeValue(node, "dyedColor")
            .ifPresent(color -> item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(color)));
        Parsers.getNodeValue(node, "enchantable", JsonNode::isObject, Enchantable.class)
            .ifPresent(enchantable -> item.setData(DataComponentTypes.ENCHANTABLE, enchantable));
        Parsers.getBooleanNodeValue(node, "enchantmentGlintOverride")
            .ifPresent(override -> item.editMeta(itemMeta -> itemMeta.setEnchantmentGlintOverride(override)));
        Parsers.getNodeValue(node, "equippable", JsonNode::isObject, EquippableComponent.class)
            .ifPresent(equippable -> item.editMeta(itemMeta -> itemMeta.setEquippable(equippable)));
        Parsers.getNodeValue(node, "food", JsonNode::isObject, FoodComponent.class)
            .ifPresent(food -> item.editMeta(itemMeta -> itemMeta.setFood(food)));
        Parsers.getBooleanNodeValue(node, "glider")
            .ifPresent(glider -> item.editMeta(itemMeta -> itemMeta.setGlider(glider)));
        Parsers.getNodeValue(node, "instrument", Objects::nonNull, MusicInstrument.class)
            .ifPresent(instrument -> item.setData(DataComponentTypes.INSTRUMENT, instrument));
        Parsers.getNamespacedKeyNodeValue(node, "itemModel")
            .ifPresent(modelKey -> item.editMeta(itemMeta -> itemMeta.setItemModel(modelKey)));
        Parsers.getComponentNodeValue(node, "itemName")
            .ifPresent(itemName -> item.editMeta(ItemMeta.class, meta -> meta.itemName(itemName)));
        Parsers.getNodeValue(node, "jukeboxPlayable", JsonNode::isTextual, JukeboxPlayableComponent.class)
            .ifPresent(song -> item.editMeta(itemMeta -> itemMeta.setJukeboxPlayable(song)));
        Parsers.getComponentArrayNodeValue(node, "lore")
            .ifPresent(loreLines -> item.editMeta(itemMeta -> itemMeta.lore(List.of(loreLines))));
        Parsers.getIntNodeValue(node, "maxStackSize")
            .ifPresent(maxStackSize -> item.editMeta(itemMeta -> itemMeta.setMaxStackSize(maxStackSize)));
        Parsers.getIntNodeValue(node, "maxDamage")
            .ifPresent(maxDamage -> item.editMeta(Damageable.class, itemMeta -> itemMeta.setMaxDamage(maxDamage)));
        Parsers.getKeyNodeValue(node, "noteBlockSound")
            .ifPresent(soundKey -> item.setData(DataComponentTypes.NOTE_BLOCK_SOUND, soundKey));
        Parsers.getNodeValue(node, "profile", JsonNode::isObject, ResolvableProfile.class)
            .ifPresent(profile -> item.setData(DataComponentTypes.PROFILE, profile));
        Parsers.getNodeValue(node, "rarity", JsonNode::isTextual, ItemRarity.class)
            .ifPresent(rarity -> item.editMeta(itemMeta -> itemMeta.setRarity(rarity)));
        Parsers.getIntNodeValue(node, "repairCost")
                .ifPresent(cost -> item.setData(DataComponentTypes.REPAIR_COST, cost));
        Parsers.getNodeValue(node, "tool", JsonNode::isObject, ToolComponent.class)
            .ifPresent(tool -> item.editMeta(itemMeta -> itemMeta.setTool(tool)));
        Parsers.getKeyNodeValue(node, "tooltipStyle")
            .ifPresent(style -> item.setData(DataComponentTypes.TOOLTIP_STYLE, style));
        Parsers.getBooleanNodeValue(node, "unbreakable")
            .ifPresent(unbreakable -> item.getItemMeta().setUnbreakable(unbreakable));


        CustomItem.Builder builder = CustomItem.builder()
            .item(item);
        Parsers.getItemKeyNodeValue(node, "useRemainder").ifPresent(builder::useRemainder);
        return builder.build();
    }

}
