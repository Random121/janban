package persistence;

import org.json.JSONObject;

// This interface represents an object that can be written as JSON.
// This interface was modeled after the JsonSerializationDemo project
// (https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo).
public interface JsonSerializable {
    // EFFECTS: returns the JSON object representation
    JSONObject toJson();
}
