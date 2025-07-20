package re.sylfa.itemcreator.util;

import com.fasterxml.jackson.databind.JsonNode;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.deserializers.components.ToolRuleDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Parsers {

    static <T> Optional<T> getNodeValue(@NonNull JsonNode node, @NonNull String valueName, @NonNull Predicate<JsonNode> nodePredicate, @NonNull Class<T> valueClass) {
        JsonNode foundNode = node.get(valueName);
        return foundNode != null && nodePredicate.test(foundNode)
            ? Optional.ofNullable(ItemCreator.getMapper().convertValue(foundNode, valueClass))
            : Optional.empty();
    }

    static Optional<Float> getFloatNodeValue(@NonNull JsonNode node, @NonNull String valueName) {
        return getNodeValue(node, valueName, JsonNode::isNumber, Float.class);
    }

    static Optional<String> getStringNodeValue(@NonNull JsonNode node, @NonNull String valueName) {
        return getNodeValue(node, valueName, JsonNode::isTextual, String.class);
    }

    static Optional<Boolean> getBooleanNodeValue(@NonNull JsonNode node, @NonNull String valueName) {
        return getNodeValue(node, valueName, JsonNode::isBoolean, Boolean.class);
    }

    static Optional<Material> getMaterialNodeValue(@NonNull JsonNode node, @NonNull String valueName) {
        return getNodeValue(node, valueName, JsonNode::isTextual, Material.class);
    }

    static Optional<Component[]> getComponentArrayNodeValue(@NonNull JsonNode node, @NonNull String valueName) {
        JsonNode foundNode = node.get(valueName);
        if(foundNode == null || !foundNode.isArray()) {
            return Optional.empty();
        }
        List<Component> components = new ArrayList<>();
        for(JsonNode line : foundNode) {

            components.add(ItemCreator.getMapper().convertValue(line, Component.class));
        }
        return components.isEmpty()
            ? Optional.empty()
            : Optional.of(components.toArray(Component[]::new));
    }

    static Optional<List<ToolRuleDeserializer.RuleValues>> getToolRulesArrayNodeValue(@NonNull JsonNode node, @NonNull String valueName) {
        JsonNode foundNode = node.get(valueName);
        if(foundNode == null || !foundNode.isArray()) {
            return Optional.empty();
        }

        List<ToolRuleDeserializer.RuleValues> ruleValues = new ArrayList<>();
        for(JsonNode rule : foundNode) {
            if(!rule.isObject()) {
                continue;
            }
            ToolRuleDeserializer.RuleValues ruleValue = ItemCreator.getMapper().convertValue(rule, ToolRuleDeserializer.RuleValues.class);
            if(ruleValue != null) {
                ruleValues.add(ruleValue);
            }
        }
        return Optional.of(ruleValues);
    }

    static Optional<Integer> getNodeIntValue(@NonNull JsonNode node, @NonNull String valueName) {
        return getNodeValue(node, valueName, JsonNode::isIntegralNumber, Integer.class);
    }

    static Optional<Component> getNodeComponentValue(@NonNull JsonNode node, @NonNull String valueName) {
        return getNodeValue(node, valueName, JsonNode::isTextual, Component.class);
    }

    static Optional<NamespacedKey> getNodeNamespacedKeyValue(@NonNull JsonNode node, @NonNull String valueName) {
        return getNodeValue(node, valueName, JsonNode::isTextual, NamespacedKey.class);
    }

    static Optional<Key> getNodeKeyValue(@NonNull JsonNode node, @NonNull String valueName) {
        return getNodeValue(node, valueName, JsonNode::isTextual, Key.class);
    }

    static Optional<ItemStack> getNodeItemStackValue(@NonNull JsonNode node, @NonNull String valueName) {
        return getNodeValue(node, valueName, jsonNode -> jsonNode.isTextual() || jsonNode.isObject(), ItemStack.class);
    }
}
