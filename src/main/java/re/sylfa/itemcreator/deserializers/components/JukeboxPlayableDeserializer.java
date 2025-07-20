package re.sylfa.itemcreator.deserializers.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;

import java.io.IOException;
import java.util.Optional;

public class JukeboxPlayableDeserializer extends StdDeserializer<JukeboxPlayableComponent> {
    public JukeboxPlayableDeserializer() { super(JukeboxPlayableComponent.class); }

    @Override
    public JukeboxPlayableComponent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Optional<NamespacedKey> songKey = Optional.ofNullable(NamespacedKey.fromString(p.getValueAsString()));
        if(songKey.isEmpty()) {
            return null;
        }

        JukeboxPlayableComponent component = new ItemStack(Material.STONE).getItemMeta().getJukeboxPlayable();
        component.setSongKey(songKey.get());

        return component;
    }
}
