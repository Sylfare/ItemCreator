package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.EquippableComponent;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Log;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;
import java.util.Optional;

public class EquippableDeserializer extends StdDeserializer<EquippableComponent> {
    public EquippableDeserializer() {
        super(EquippableComponent.class);
    }

    @Override
    public EquippableComponent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        JsonNode slotNode = node.get("slot");
        EquipmentSlot slot;
        if(slotNode != null && slotNode.isTextual()) {
            slot = ItemCreator.getMapper().convertValue(slotNode, EquipmentSlot.class);
            if(slot == null) {
                Log.error("%s is not a valid slot type", slotNode.textValue());
            }

        } else {
            return null;
        }



        EquippableComponent equippableComponent = new ItemStack(Material.STONE).getItemMeta().getEquippable();
        Parsers.getBooleanNodeValue(node, "dispensable").ifPresent(equippableComponent::setDispensable);
        Parsers.getBooleanNodeValue(node, "damageOnHurt").ifPresent(damage -> equippableComponent.setDamageOnHurt(damage));
        Parsers.getBooleanNodeValue(node, "equipOnInteract").ifPresent(equippableComponent::setEquipOnInteract);
        Parsers.getNodeNamespacedKeyValue(node, "model").ifPresent(equippableComponent::setModel);
        Parsers.getNodeNamespacedKeyValue(node, "cameraOverlay").ifPresent(equippableComponent::setCameraOverlay);
        Optional.ofNullable(slot).ifPresent(equippableComponent::setSlot);

        return equippableComponent;
    }
}
