package re.sylfa.itemcreator.deserializers.types;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Optional;

public class TextComponentDeserializer extends StdDeserializer<Component> {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public TextComponentDeserializer() {
        super(Component.class);
    }

    @Override
    public Component deserialize(JsonParser p, DeserializationContext ctxt){

        return Optional.ofNullable(p.getValueAsString())
            .map(message -> miniMessage.deserialize("<!italic><white>"+message+"</white></!italic>"))
            .orElse(Component.empty());
    }
}
