package re.sylfa.itemcreator.deserializers.components;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.EquippableComponent;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Log;
import re.sylfa.itemcreator.util.Parsers;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Optional;

public class EquippableDeserializer extends StdDeserializer<EquippableComponent> {
    public EquippableDeserializer() {
        super(EquippableComponent.class);
    }

    @Override
    public EquippableComponent deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        if(node == null) {
            return null;
        }
        JsonNode slotNode = node.get("slot");
        EquipmentSlot slot;
        if(slotNode != null && slotNode.isString()) {
            slot = ItemCreator.getMapper().convertValue(slotNode, EquipmentSlot.class);
            if(slot == null) {
                Log.error("%s is not a valid slot type", slotNode.stringValue());
            }

        } else {
            return null;
        }



        EquippableComponent equippableComponent = new ItemStack(Material.STONE).getItemMeta().getEquippable();
        Parsers.getBooleanNodeValue(node, "dispensable").ifPresent(equippableComponent::setDispensable);
        Parsers.getBooleanNodeValue(node, "damageOnHurt").ifPresent(equippableComponent::setDamageOnHurt);
        Parsers.getBooleanNodeValue(node, "equipOnInteract").ifPresent(equippableComponent::setEquipOnInteract);
        Parsers.getNamespacedKeyNodeValue(node, "model").ifPresent(equippableComponent::setModel);
        Parsers.getNamespacedKeyNodeValue(node, "cameraOverlay").ifPresent(equippableComponent::setCameraOverlay);
        Optional.ofNullable(slot).ifPresent(equippableComponent::setSlot);

        return equippableComponent;
    }
}
