package config.source.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.source.AbstractConfigSource;
import config.source.KeyPathResolvers;

public class JsonConfigSource extends AbstractConfigSource<ObjectNode> {

  private final JsonNodeFactory factory = JsonNodeFactory.instance;

  public JsonConfigSource(ObjectNode root) {
    super(root, KeyPathResolvers.DOT);
  }

  @Override
  public Object getRaw(String key) {
    JsonNode node = this.getNodeByPath(this.normalizeKey(key));
    return this.convertJsonNode(node);
  }

  @Override
  public Object getRaw(String key, Object defaultValue) {
    Object value = this.getRaw(key);
    return value != null ? value : defaultValue;
  }

  @Override
  public void setRaw(String key, Object value) {
    String normalized = this.normalizeKey(key);
    String[] parts = normalized.split("\\.");
    ObjectNode current = this.source;

    for (int i = 0; i < parts.length - 1; i++) {
      JsonNode child = current.get(parts[i]);
      if (child == null || !child.isObject()) {
        ObjectNode newNode = this.factory.objectNode();
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
      current.set(last, this.toJsonNode(value));
    }
  }

  @Override
  public Set<String> keys() {
    return this.collectKeys("", this.source);
  }

  private Set<String> collectKeys(String prefix, ObjectNode node) {
    Set<String> result = new LinkedHashSet<>();
    Iterator<Map.Entry<String, JsonNode>> iter = node.fields();
    while (iter.hasNext()) {
      Map.Entry<String, JsonNode> entry = iter.next();
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
    }
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
      for (JsonNode elem : node) {
        list.add(this.convertJsonNode(elem));
      }
      return list;
    }
    if (node.isObject()) {
      Map<String, Object> map = new LinkedHashMap<>();
      node.fields().forEachRemaining(e -> map.put(e.getKey(), this.convertJsonNode(e.getValue())));
      return map;
    }
    return null;
  }

  private JsonNode toJsonNode(Object value) {
    if (value == null) {
      return this.factory.nullNode();
    }
    if (value instanceof String s) {
      return this.factory.textNode(s);
    }
    if (value instanceof Integer i) {
      return this.factory.numberNode(i);
    }
    if (value instanceof Long l) {
      return this.factory.numberNode(l);
    }
    if (value instanceof Double d) {
      return this.factory.numberNode(d);
    }
    if (value instanceof Float f) {
      return this.factory.numberNode(f);
    }
    if (value instanceof Boolean b) {
      return this.factory.booleanNode(b);
    }
    if (value instanceof Map<?, ?> map) {
      ObjectNode node = this.factory.objectNode();
      map.forEach((k, v) -> node.set(k.toString(), this.toJsonNode(v)));
      return node;
    }
    if (value instanceof List<?> list) {
      ArrayNode arr = this.factory.arrayNode();
      for (Object elem : list) {
        arr.add(this.toJsonNode(elem));
      }
      return arr;
    }
    return this.factory.pojoNode(value);
  }
}
