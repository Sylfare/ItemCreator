package re.sylfa.itemcreator.deserializers.components;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Optional;

public class JukeboxPlayableDeserializer extends StdDeserializer<JukeboxPlayableComponent> {
    public JukeboxPlayableDeserializer() { super(JukeboxPlayableComponent.class); }

    @Override
    public JukeboxPlayableComponent deserialize(JsonParser p, DeserializationContext ctxt) {
        Optional<NamespacedKey> songKey = Optional.ofNullable(NamespacedKey.fromString(p.getValueAsString()));
        if(songKey.isEmpty()) {
            return null;
        }

        JukeboxPlayableComponent component = new ItemStack(Material.STONE).getItemMeta().getJukeboxPlayable();
        component.setSongKey(songKey.get());

        return component;
    }
}
