package http;

import enums.HttpMethod;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private String method;
    private String path;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        log.debug("request line : {}",requestLine);
        String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException(requestLine + "이 형식에 맞지 않습니다.");
        }

        method = tokens[0];
        processPathAndParams(tokens[1]);
    }

    private void processPathAndParams(String url) {
        int index = url.indexOf("?");
        if (index == -1) {
            path = url;
        }
        else {
            path = url.substring(0, index);
            params = HttpRequestUtils.parseQueryString(url.substring(index + 1));
        }
    }

    public HttpMethod getMethod() {
        return HttpMethod.valueOf(method);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
