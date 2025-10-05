package config.source.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.source.AbstractConfigSource;
import config.source.KeyPathResolvers;

public class JsonConfigSource extends AbstractConfigSource<ObjectNode> {

  private final ObjectMapper mapper = new ObjectMapper();

  public JsonConfigSource(ObjectNode root) {
    super(root, KeyPathResolvers.DOT);
  }

  @Override
  public Object getRaw(String key) {
    JsonNode node = this.getNodeByPath(this.keyPathResolver.normalizeKey(key));
    return this.convertJsonNode(node);
  }

  @Override
  public Object getRaw(String key, Object defaultValue) {
    Object value = this.getRaw(key);
    return value != null ? value : defaultValue;
  }

  @Override
  public void setRaw(String key, Object value) {
    String normalized = this.keyPathResolver.normalizeKey(key);
    String[] parts = normalized.split("\\.");
    ObjectNode current = this.source;

    for (int i = 0; i < parts.length - 1; i++) {
      JsonNode child = current.get(parts[i]);
      if (!(child instanceof ObjectNode)) {
        ObjectNode newNode = this.mapper.createObjectNode();
        current.set(parts[i], newNode);
        current = newNode;
      } else {
        current = (ObjectNode) child;
      }
    }

    String last = parts[parts.length - 1];
    if (value == null) {
      current.remove(last);
    } else {
      current.set(last, this.mapper.valueToTree(value));
    }
  }

  @Override
  public Set<String> keys() {
    return this.collectKeys("", this.source);
  }

  // ----------------- ヘルパーメソッド -----------------

  private Set<String> collectKeys(String prefix, ObjectNode node) {
    Set<String> result = new LinkedHashSet<>();
    node.fields().forEachRemaining(entry -> {
      String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
      JsonNode child = entry.getValue();
      if (child.isObject()) {
        result.addAll(this.collectKeys(key, (ObjectNode) child));
      } else if (child.isArray()) {
        ArrayNode arr = (ArrayNode) child;
        for (int i = 0; i < arr.size(); i++) {
          JsonNode elem = arr.get(i);
          if (elem.isObject()) {
            result.addAll(this.collectKeys(key + "." + i, (ObjectNode) elem));
          } else {
            result.add(key + "." + i);
          }
        }
      } else {
        result.add(key);
      }
    });
    return result;
  }

  private JsonNode getNodeByPath(String path) {
    String[] parts = path.split("\\.");
    JsonNode current = this.source;
    for (String part : parts) {
      if (current == null) {
        return null;
      }
      if (current.isArray()) {
        int idx = Integer.parseInt(part);
        current = current.get(idx);
      } else {
        current = current.get(part);
      }
    }
    return current;
  }

  private Object convertJsonNode(JsonNode node) {
    if (node == null || node.isNull()) {
      return null;
    }
    if (node.isValueNode()) {
      if (node.isTextual()) {
        return node.asText();
      }
      if (node.isInt()) {
        return node.asInt();
      }
      if (node.isLong()) {
        return node.asLong();
      }
      if (node.isDouble()) {
        return node.asDouble();
      }
      if (node.isFloat()) {
        return node.floatValue();
      }
      if (node.isBoolean()) {
        return node.asBoolean();
      }
      return node.asText();
    }
    if (node.isArray()) {
      List<Object> list = new ArrayList<>();
      node.forEach(elem -> list.add(this.convertJsonNode(elem)));
      return list;
    }
    if (node.isObject()) {
      Map<String, Object> map = new LinkedHashMap<>();
      node.fields().forEachRemaining(e -> map.put(e.getKey(), this.convertJsonNode(e.getValue())));
      return map;
    }
    return null;
  }
}
