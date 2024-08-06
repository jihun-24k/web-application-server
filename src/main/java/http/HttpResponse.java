package http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private DataOutputStream dos;
    private Map<String, String> header = new HashMap<>();

    public HttpResponse(OutputStream out) {
        dos = new DataOutputStream(out);
    }

    public void forward(String path) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());

        if (path.endsWith(".css")) {
            addHeader("Content-Type", "text/css;");
        }
        if (path.endsWith(".html")) {
            addHeader("Content-Type", "text/html;charset=utf-8;");
        }
        if (path.endsWith(".js")) {
            addHeader("Content-Type", "application/javascript;");
        }
        addHeader("Content-Length", body.length + "");
        response200Header();
        responseBody(body);
    }
    public void forwardBody(String body) {
        byte[] contents = body.getBytes();
        addHeader("Content-Type", "text/html;charset=utf-8;");
        addHeader("Content-Length", contents.length + "");
        response200Header();
        responseBody(contents);
    }

    public void sendRedirect(String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            processHeaders();
            dos.writeBytes("Location: http://localhost:8080" + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    private void response200Header() {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processHeaders() {
        try {
            for (Entry<String, String> keyValue : header.entrySet()) {
                dos.writeBytes(keyValue.getKey() + ": " + keyValue.getValue() + "\r\n");
            }
        } catch (IOException io) {
            log.error(io.getMessage());
        }
    }
}
