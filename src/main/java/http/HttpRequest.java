package http;

import enums.HttpMethod;
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
    private RequestLine requestLine;
    private Map<String, String> params = new HashMap<>();
    private Map<String, String> header = new HashMap<>();

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = br.readLine();
            if (line == null) {
                return;
            }
            requestLine = new RequestLine(line);

            line = br.readLine();
            while (!line.equals("")) {
                log.debug("header : {}", line);
                processHeader(line);
                line = br.readLine();
                if (line == null) {
                    break;
                }
            }

            if (getMethod().isPost()) {
                int contentLength = Integer.parseInt(header.get("Content-Length"));
                String body = IOUtils.readData(br, contentLength);
                params = HttpRequestUtils.parseQueryString(body);
            } else {
                params = requestLine.getParams();
            }
        }
        catch (IOException io) {
            log.error(io.getMessage());
        }
    }

    private void processHeader(String line) {
        log.debug("HTTP Header Info = {}", line);

        String[] info = line.split(": ");
        header.put(info[0].trim(), info[1].trim());
    }

    public HttpCookies getCookies() {
        return new HttpCookies(getHeader("Cookie"));
    }

    public HttpSession getSession() {
        return HttpSessions.getSession(getCookies().getCookie("JSESSION"));
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParameter(String key) {
        return params.get(key);
    }

    public String getHeader(String key) {
        return header.get(key);
    }
}
