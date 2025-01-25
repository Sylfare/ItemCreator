package re.sylfa.itemcreator.typeAdapters;

import java.lang.StackWalker.Option;
import java.lang.reflect.Type;

import org.bukkit.Material;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MaterialAdapter implements JsonDeserializer<Material> {

    @Override
    public Material deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String rawMaterial = json.getAsString();
        Material material = Material.matchMaterial(rawMaterial);
        if(material == null) {
            throw new JsonParseException(String.format("Bad Material: %s", rawMaterial));
        }

        return material;
    }
}
