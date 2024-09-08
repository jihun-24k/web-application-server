package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {
    private static Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession getSession(String uuid) {
        if (sessions.get(uuid) == null) {
            addSession(uuid);
        }
        return sessions.get(uuid);
    }

    private static void addSession(String uuid) {
        sessions.put(uuid, new HttpSession(uuid));
    }

    public static void remove(String uuid) {
        sessions.remove(uuid);
    }
}
