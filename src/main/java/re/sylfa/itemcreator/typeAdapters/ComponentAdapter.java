package re.sylfa.itemcreator.typeAdapters;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ComponentAdapter implements JsonDeserializer<Component>{
    private static final MiniMessage mm = MiniMessage.miniMessage();
    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return mm.deserialize(json.getAsString());
    }
    
}
