package re.sylfa.itemcreator.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.Material;

import java.io.IOException;
import java.util.Optional;

public class MaterialDeserializer extends StdDeserializer<Material> {
    public MaterialDeserializer() {
        super(Material.class);
    }

    @Override
    public Material deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return Optional.ofNullable(p.getValueAsString())
            .map(Material::matchMaterial)
            .orElse(Material.STONE);
    }
}
