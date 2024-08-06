package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private String method;
    private String path;
    private Map<String, String> params = new HashMap<>();
    private Map<String, String> header = new HashMap<>();

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader bufferIn = new BufferedReader(new InputStreamReader(in));

        String line = bufferIn.readLine();
        extractRequestLine(line);

        log.debug("HTTP RequestLine Info = {}", line);
        while (line != null && !"".equals(line)) {

            line = bufferIn.readLine();
            if (line == null || "".equals(line)) {
                break;
            }
            extractHeader(line);
        }

        if (method.equals("POST")) {
            int contentLength = Integer.parseInt(header.get("Content-Length"));
            String body = IOUtils.readData(bufferIn, contentLength);
            params = HttpRequestUtils.parseQueryString(body);
        }
    }

    private void extractHeader(String line) {
        log.debug("HTTP Header Info = {}", line);

        String[] info = line.split(": ");
        header.put(info[0], info[1]);
    }

    private void extractRequestLine(String line) {
        String[] tokens = line.split(" ");

        method = tokens[0];
        extractPathAndParams(tokens[1]);
    }

    private void extractPathAndParams(String url) {
        if (url.startsWith("/user/create?")) {
            int index = url.indexOf("?");
            path = url.substring(0, index);
            params = HttpRequestUtils.parseQueryString(url.substring(index + 1));
        }
        else {
            path = url;
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getParameter(String key) {
        return params.get(key);
    }

    public String getHeader(String key) {
        return header.get(key);
    }
}
