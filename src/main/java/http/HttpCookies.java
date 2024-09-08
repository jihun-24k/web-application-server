package http;

import java.util.Map;
import util.HttpRequestUtils;

public class HttpCookies {
    private Map<String, String> cookies;
    public HttpCookies(String cookies) {
        this.cookies = HttpRequestUtils.parseCookies(cookies);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
