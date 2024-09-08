package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String uuid;
    private final Map<String, Object> attribute = new HashMap<>();

    public HttpSession(String uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return uuid;
    }

    public void setAttribute(String name, Object value) {
        attribute.put(name, value);
    }

    public Object getAttribute(String name) {
        return attribute.get(name);
    }

    public void removeAttribute(String name) {
        attribute.remove(name);
    }

    public void invalidate() {
        HttpSessions.remove(uuid);
    }
}
