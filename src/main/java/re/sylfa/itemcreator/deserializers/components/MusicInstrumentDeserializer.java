package re.sylfa.itemcreator.deserializers.components;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.MusicInstrument;
import org.bukkit.Sound;
import re.sylfa.itemcreator.util.Parsers;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Optional;

public class MusicInstrumentDeserializer extends StdDeserializer<MusicInstrument> {

    public MusicInstrumentDeserializer() { super(MusicInstrument.class); }

    @Override
    public MusicInstrument deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        if (node == null || (!node.isObject() && !node.isString())) {
            return null;
        }

        // by key
        if(node.isString()) {
            Key key = Key.key(node.asString());
            return RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).get(key);
        }

        // by inline instrument
        if(node.isObject()) {
            Optional<Key> soundKey = Parsers.getKeyNodeValue(node, "soundKey");
            if (soundKey.isEmpty()) {
                return null;
            }
            Optional<Float> duration = Parsers.getFloatNodeValue(node, "duration");
            Optional<Float> range = Parsers.getFloatNodeValue(node, "range");
            Optional<Component> description = Parsers.getComponentNodeValue(node, "description");
            TypedKey<Sound> typedKey = RegistryKey.SOUND_EVENT.typedKey(soundKey.get());

            return MusicInstrument.create(builder -> builder.empty()
                .soundEvent(typedKey)
                .description(description.orElse(Component.empty()))
                .duration(duration.orElse(1.0f))
                .range(range.orElse(1.0f))
            );
        }

        // default
        return null;
    }
}
