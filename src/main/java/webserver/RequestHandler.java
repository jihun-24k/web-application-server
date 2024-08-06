package webserver;

import static java.lang.System.in;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader bufferIn = new BufferedReader(new InputStreamReader(in));

            String line = bufferIn.readLine();
            String[] tokens = line.split(" ");

            String httpMethod = tokens[0];
            String url = tokens[1];
            User createdUser;

            int index = url.indexOf("?");
            String requestUrl = url;
            if (index > 0) {
                requestUrl = url.substring(0, index);
                String params = url.substring(index + 1);
                Map<String, String> userInfo = HttpRequestUtils.parseQueryString(params);

                createdUser = new User(
                        userInfo.get("userId"),
                        userInfo.get("password"),
                        userInfo.get("name"),
                        userInfo.get("email")
                );
            }

            Map<String, String> httpHeader = new HashMap<>();

            log.debug("HTTP RequestLine Info = {}", line);
            while (!"".equals(line) && line != null) {
                line = bufferIn.readLine();

                if (line == null || "".equals(line)) {
                    break;
                }

                String[] info = line.split(": ");
                httpHeader.put(info[0], info[1]);
            }

            if (httpMethod.equals("POST")) {
                int contentLength = Integer.parseInt(httpHeader.get("Content-Length"));
                String httpBody = IOUtils.readData(bufferIn, contentLength);

                Map<String, String> userInfo = HttpRequestUtils.parseQueryString(httpBody);

                createdUser = new User(
                        userInfo.get("userId"),
                        userInfo.get("password"),
                        userInfo.get("name"),
                        userInfo.get("email")
                );
            }

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = new byte[0];

            if (requestUrl.equals("/user/create")) {
                response302Header(dos);
            }
            else {
                body = Files.readAllBytes(new File("./webapp" + requestUrl).toPath());
                response200Header(dos, body.length);
            }
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
