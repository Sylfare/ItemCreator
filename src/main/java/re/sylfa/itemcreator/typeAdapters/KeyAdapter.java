package re.sylfa.itemcreator.typeAdapters;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.kyori.adventure.key.Key;

public class KeyAdapter implements JsonDeserializer<Key>{

    @Override
    public Key deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return Key.key(json.getAsString());
    }
    
}
