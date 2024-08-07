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

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = br.readLine();
            if (line == null) {
                return;
            }
            processRequestLine(line);

            line = br.readLine();
            while (!"".equals(line)) {
                log.debug("header : {}", line);
                processHeader(line);
                line = br.readLine();
            }

            if (method.equals("POST")) {
                int contentLength = Integer.parseInt(header.get("Content-Length"));
                String body = IOUtils.readData(br, contentLength);
                params = HttpRequestUtils.parseQueryString(body);
            }
        }
        catch (IOException io) {
            log.error(io.getMessage());
        }
    }

    private void processRequestLine(String line) {
        String[] tokens = line.split(" ");

        method = tokens[0];
        extractPathAndParams(tokens[1]);
    }

    private void processHeader(String line) {
        log.debug("HTTP Header Info = {}", line);

        String[] info = line.split(": ");
        header.put(info[0].trim(), info[1].trim());
    }

    private void extractPathAndParams(String url) {
        int index = url.indexOf("?");
        if (index == -1) {
            path = url;
        }
        else {
            path = url.substring(0, index);
            params = HttpRequestUtils.parseQueryString(url.substring(index + 1));
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

    public boolean equalsMethod(String method) {
        return this.method.equals(method);
    }
}
