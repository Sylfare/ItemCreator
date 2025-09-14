package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.checkerframework.checker.nullness.qual.Nullable;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.JavaUtils;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class AttributeModifiersDeserializer extends StdDeserializer<ItemAttributeModifiers> {

    public AttributeModifiersDeserializer() {
        super(ItemAttributeModifiers.class);
    }

    @Override
    public ItemAttributeModifiers deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if(!node.isArray()) {
            return null;
        }

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
        JavaUtils.iteratorStream(node.elements())
            .map(parsedAttribute -> ItemCreator.getMapper().convertValue(parsedAttribute, AttributeModification.class))
            .filter(Objects::nonNull)
            .forEach(parsedAttribute -> parsedAttribute.addToBuilder(builder));

        return builder.build();
    }

    public record AttributeModification(
        Attribute attribute,
        NamespacedKey key,
        double amount,
        AttributeModifier.Operation operation,
        @Nullable AttributeModifierDisplay display,
        EquipmentSlotGroup equipmentSlotGroup
    ) {
        public void addToBuilder(ItemAttributeModifiers.Builder builder) {
            AttributeModifier modifier = new AttributeModifier(
                key,
                amount,
                operation,
                equipmentSlotGroup
            );

            if(display != null) {
                builder.addModifier(attribute, modifier, display);
            } else {
                builder.addModifier(attribute, modifier);
            }
        }
    }

    public static class AttributeModifierDeserializer extends StdDeserializer<AttributeModification> {
        public AttributeModifierDeserializer() {
            super(AttributeModification.class);
        }

        @Override
        public AttributeModification deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if(node == null || !node.isObject()) {
                return null;
            }
            Optional<Attribute> attribute = Parsers.getNodeValue(node, "attribute", JsonNode::isTextual, Attribute.class);
            Optional<NamespacedKey> key = Parsers.getNamespacedKeyNodeValue(node, "key");
            Optional<Double> amount = Parsers.getDoubleNodeValue(node, "amount");
            Optional<AttributeModifier.Operation> operation = Parsers.getNodeValue(node, "operation", JsonNode::isTextual, AttributeModifier.Operation.class);

            if(attribute.isEmpty() || key.isEmpty() || amount.isEmpty() || operation.isEmpty()) {
                return null;
            }

            Optional<AttributeModifierDisplay> display = Parsers.getNodeValue(node, "display", JsonNode::isTextual, AttributeModifierDisplay.class);
            EquipmentSlotGroup slotGroup = Parsers.getNodeValue(node, "slotGroup", JsonNode::isTextual, EquipmentSlotGroup.class)
                .orElse(EquipmentSlotGroup.ANY);
            return new AttributeModification(
                attribute.get(),
                key.get(),
                amount.get(),
                operation.get(),
                display.orElse(null),
                slotGroup
            );
        }
    }

    public static class AttributeModifierDisplayDeserializer extends StdDeserializer<AttributeModifierDisplay> {
        public AttributeModifierDisplayDeserializer() {
            super(AttributeModifierDisplay.class);
        }

        @Override
        public AttributeModifierDisplay deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getText();
            return switch (value) {
                case "reset" -> AttributeModifierDisplay.reset();
                case "hidden" -> AttributeModifierDisplay.hidden();
                default -> {
                    // try to parse the display as a component
                    Component component = ItemCreator.getMapper().convertValue(value, Component.class);
                    if(component == null) {
                        yield null;
                    }

                    yield AttributeModifierDisplay.override(component);
                }
            };
        }
    }
}
