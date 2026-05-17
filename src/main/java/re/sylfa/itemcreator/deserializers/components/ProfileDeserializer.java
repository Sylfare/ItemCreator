package re.sylfa.itemcreator.deserializers.components;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Parsers;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Objects;
import java.util.Optional;

public class ProfileDeserializer extends StdDeserializer<ResolvableProfile> {
    public ProfileDeserializer() {
        super(ResolvableProfile.class);
    }
    @Override
    public ResolvableProfile deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = p.readValueAsTree();
        if(node == null) {
            return null;
        }

        ResolvableProfile.Builder builder = ResolvableProfile.resolvableProfile();

        Parsers.getStringNodeValue(node, "name").ifPresent(builder::name);
        Parsers.getUUIDNodeValue(node, "uuid").ifPresent(builder::uuid);

        if(node.has("property")) {
            Parsers.getNodeValue(node, "property", JsonNode::isObject, ProfileProperty.class)
                .ifPresent(builder::addProperty);
        } else if (node.has("properties")) {
            JsonNode properties = node.get("properties");
            properties.valueStream()
                .map(currentProperty -> ItemCreator.getMapper().convertValue(currentProperty, ProfileProperty.class))
                .filter(Objects::nonNull)
                .forEach(builder::addProperty);
        }

        return builder.build();
    }

    public static class ProfilePropertyDeserializer extends StdDeserializer<ProfileProperty> {
        public ProfilePropertyDeserializer() {
            super(ProfileProperty.class);
        }

        @Override
        public ProfileProperty deserialize(JsonParser p, DeserializationContext ctxt) {
            JsonNode node = p.readValueAsTree();
            if(node == null) {
                return null;
            }

            Optional<String> name = Parsers.getStringNodeValue(node, "name");
            if(name.isEmpty()) {
                return null;
            }
            Optional<String> value = Parsers.getStringNodeValue(node, "value");
            if(value.isEmpty()) {
                return null;
            }

            Optional<String> signature = Parsers.getStringNodeValue(node, "signature");

            return new ProfileProperty(name.get(), value.get(), signature.orElse(null));
        }
    }
}
