package re.sylfa.itemcreator.deserializers.types;

import org.bukkit.Material;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Optional;

public class MaterialDeserializer extends StdDeserializer<Material> {
    public MaterialDeserializer() {
        super(Material.class);
    }

    @Override
    public Material deserialize(JsonParser p, DeserializationContext ctxt) {
        return Optional.ofNullable(p.getValueAsString())
            .map(Material::matchMaterial)
            .orElse(Material.STONE);
    }
}
