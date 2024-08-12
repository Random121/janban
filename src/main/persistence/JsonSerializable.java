package persistence;

import org.json.JSONObject;

// This interface represents an object that can be written as JSON.
public interface JsonSerializable {
    // EFFECTS: returns the JSON object representation
    JSONObject toJson();
}
