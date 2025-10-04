package config.source.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.source.ConfigSource;

public class JsonConfigSource implements ConfigSource<JsonNode> {

  private final ObjectMapper mapper = new ObjectMapper();
  private final ObjectNode root;

  public JsonConfigSource(ObjectNode root) {
    this.root = root;
  }

  public JsonConfigSource(String json) throws Exception {
    JsonNode node = this.mapper.readTree(json);
    if (!node.isObject()) {
      throw new IllegalArgumentException("Root must be JSON object");
    }
    this.root = (ObjectNode) node;
  }

  @Override
  public JsonNode source() {
    return this.root.deepCopy();
  }

  private JsonNode getNode(String key) {
    String[] parts = key.split("\\.");
    JsonNode current = this.root;
    for (String part : parts) {
      if (current == null || !current.has(part)) {
        return null;
      }
      current = current.get(part);
    }
    return current;
  }

  private ObjectNode ensureParentNode(String key) {
    String[] parts = key.split("\\.");
    ObjectNode current = this.root;

    for (int i = 0; i < parts.length - 1; i++) {
      String part = parts[i];
      JsonNode node = current.get(part);
      ObjectNode obj;

      if (!(node instanceof ObjectNode)) {
        obj = this.mapper.createObjectNode();
        current.set(part, obj);
      } else {
        obj = (ObjectNode) node;
      }
      current = obj;
    }

    return current;
  }


  private String lastPart(String key) {
    String[] parts = key.split("\\.");
    return parts[parts.length - 1];
  }

  @Override
  public Object get(String key) {
    JsonNode node = this.getNode(key);
    if (node == null || node.isNull()) {
      return null;
    }
    if (node.isValueNode()) {
      return node.asText();
    }
    return node;
  }

  @Override
  public void set(String key, Object value) {
    if (value == null) {
      throw new IllegalArgumentException("null is not supported, use remove instead");
    }
    ObjectNode parent = this.ensureParentNode(key);
    String last = this.lastPart(key);

    if (value instanceof String s) {
      parent.put(last, s);
    } else if (value instanceof Integer i) {
      parent.put(last, i);
    } else if (value instanceof Long l) {
      parent.put(last, l);
    } else if (value instanceof Double d) {
      parent.put(last, d);
    } else if (value instanceof Float f) {
      parent.put(last, f);
    } else if (value instanceof Boolean b) {
      parent.put(last, b);
    } else if (value instanceof Collection<?> c) {
      ArrayNode arrayNode = this.mapper.createArrayNode();
      for (Object o : c) {
        arrayNode.add(o.toString());
      }
      parent.set(last, arrayNode);
    } else if (value instanceof Map<?, ?> m) {
      ObjectNode mapNode = this.mapper.createObjectNode();
      m.forEach((k, v) -> mapNode.put(k.toString(), v.toString()));
      parent.set(last, mapNode);
    } else {
      parent.putPOJO(last, value);
    }
  }


  @Override
  public String getString(String key) {
    Object val = this.get(key);
    return val != null ? val.toString() : null;
  }

  @Override
  public void setString(String key, String value) {
    this.set(key, value);
  }


  @Override
  public Integer getInt(String key) {
    Object val = this.get(key);
    return val != null ? Integer.valueOf(val.toString()) : null;
  }

  @Override
  public void setInt(String key, int value) {
    this.set(key, value);
  }


  @Override
  public Long getLong(String key) {
    Object val = this.get(key);
    return val != null ? Long.valueOf(val.toString()) : null;
  }

  @Override
  public void setLong(String key, long value) {
    this.set(key, value);
  }

  @Override
  public Double getDouble(String key) {
    Object val = this.get(key);
    return val != null ? Double.valueOf(val.toString()) : null;
  }

  @Override
  public void setDouble(String key, double value) {
    this.set(key, value);
  }

  @Override
  public Float getFloat(String key) {
    Object val = this.get(key);
    return val != null ? Float.valueOf(val.toString()) : null;
  }

  @Override
  public void setFloat(String key, float value) {
    this.set(key, value);
  }


  @Override
  public Boolean getBoolean(String key) {
    Object val = this.get(key);
    return val != null ? Boolean.valueOf(val.toString()) : null;
  }

  @Override
  public void setBoolean(String key, boolean value) {
    this.set(key, value);
  }


  @Override
  @SuppressWarnings("unchecked")
  public <E> Collection<E> getCollection(String key) {
    JsonNode node = this.getNode(key);
    if (node == null || !node.isArray()) {
      return List.of();
    }
    return StreamSupport.stream(node.spliterator(), false).map(JsonNode::asText).map(e -> (E) e)
        .collect(Collectors.toList());
  }

  @Override
  public <E> void setCollection(String key, Collection<E> value) {
    this.set(key, value);
  }


  @Override
  public <E> List<E> getList(String key) {
    return new ArrayList<>(this.getCollection(key));
  }

  @Override
  public <E> void setList(String key, List<E> value) {
    this.setCollection(key, value);
  }


  @Override
  public <E> Set<E> getSet(String key) {
    return new HashSet<>(this.getCollection(key));
  }

  @Override
  public <E> void setSet(String key, Set<E> value) {
    this.setCollection(key, value);
  }


  @Override
  @SuppressWarnings("unchecked")
  public <K, V> Map<K, V> getMap(String key) {
    JsonNode node = this.getNode(key);
    if (node == null || !node.isObject()) {
      return Map.of();
    }
    ObjectNode obj = (ObjectNode) node;
    Map<K, V> map = new HashMap<>();
    obj.fields()
        .forEachRemaining(entry -> map.put((K) entry.getKey(), (V) entry.getValue().asText()));
    return map;
  }

  @Override
  public <K, V> void setMap(String key, Map<K, V> value) {
    this.set(key, value);
  }

  @Override
  public void remove(String key) {
    ObjectNode parent = this.ensureParentNode(key);
    String last = this.lastPart(key);
    parent.remove(last);
  }

  @Override
  public Short getShort(String key) {
    Object val = this.get(key);
    return val != null ? Short.valueOf(val.toString()) : null;
  }

  @Override
  public void setShort(String key, short value) {
    this.set(key, value);
  }


  @Override
  public Byte getByte(String key) {
    Object val = this.get(key);
    return val != null ? Byte.valueOf(val.toString()) : null;
  }

  @Override
  public void setByte(String key, byte value) {
    this.set(key, value);
  }

}
