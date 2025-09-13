package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ConsumeEffectDeserializer extends StdDeserializer<ConsumeEffect> {

    public ConsumeEffectDeserializer() { super(ConsumeEffect.class); }

    @Override
    public ConsumeEffect deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        Optional<Type> type = Parsers.getNodeValue(node, "type", JsonNode::isTextual, Type.class);
        if(type.isEmpty()) {
            return null;
        }

        return switch (type.get()) {
            case APPLY_STATUS_EFFECTS -> {
                JsonNode effects = node.get("effects");
                if(effects == null || !effects.isArray()) {
                    yield null;
                }

                List<PotionEffect> potionEffects = new ArrayList<>();
                for(JsonNode effect : effects) {
                    PotionEffect potionEffect = ItemCreator.getMapper().convertValue(effect, PotionEffect.class);
                    if(potionEffect != null) {
                        potionEffects.add(potionEffect);
                    }
                }

                float probability = Parsers.getFloatNodeValue(node, "probability").orElse(1.0f);
                yield ConsumeEffect.applyStatusEffects(potionEffects, probability);
            }
            case CLEAR_ALL_STATUS_EFFECTS -> ConsumeEffect.clearAllStatusEffects();
            case PLAY_SOUND -> Parsers.getKeyNodeValue(node, "sound")
                .map(ConsumeEffect::playSoundConsumeEffect)
                .orElse(null);
            case REMOVE_STATUS_EFFECTS -> {
                JsonNode effects = node.get("effects");
                if(effects == null || !effects.isArray()) {
                    yield null;
                }

                Set<PotionEffectType> effectTypes = new HashSet<>();
                for(JsonNode effect : effects) {
                    String rawKey = effect.asText();
                    if(rawKey == null || rawKey.isBlank()) {
                        continue;
                    }
                    Key key = Key.key(rawKey);

                    PotionEffectType effectType = Registry.MOB_EFFECT.get(key);
                    if(effectType != null) {
                        effectTypes.add(effectType);
                    }
                }


                yield ConsumeEffect.removeEffects(RegistrySet.keySetFromValues(RegistryKey.MOB_EFFECT, effectTypes));
            }
            case TELEPORT_RANDOMLY -> Parsers.getFloatNodeValue(node, "diameter")
                .map(ConsumeEffect::teleportRandomlyEffect)
                .orElse(null);
        };
    }

    public enum Type {
        APPLY_STATUS_EFFECTS,
        CLEAR_ALL_STATUS_EFFECTS,
        PLAY_SOUND,
        REMOVE_STATUS_EFFECTS,
        TELEPORT_RANDOMLY
    }
}
