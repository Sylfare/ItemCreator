package re.sylfa.itemcreator.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.IOException;
import java.util.Optional;

public class TextComponentDeserializer extends StdDeserializer<Component> {
    MiniMessage miniMessage = MiniMessage.miniMessage();

    public TextComponentDeserializer() {
        super(Component.class);
    }

    @Override
    public Component deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {

        return Optional.ofNullable(p.getValueAsString())
            .map(message -> miniMessage.deserialize("<!italic><white>"+message+"</white></!italic>"))
            .orElse(Component.empty());
    }
}
