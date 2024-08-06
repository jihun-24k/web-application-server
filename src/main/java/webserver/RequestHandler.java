package webserver;

import db.DataBase;
import http.HttpRequest;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

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

            HttpRequest httpRequest = new HttpRequest(in);

            User createdUser = new User(
                    httpRequest.getParameter("userId"),
                    httpRequest.getParameter("password"),
                    httpRequest.getParameter("name"),
                    httpRequest.getParameter("email")
            );
            DataBase.addUser(createdUser);

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = new byte[0];

            if (httpRequest.getPath().equals("/user/create")) {
                response302Header(dos);
                responseBody(dos, body);
            }
            else if (httpRequest.getPath().equals("/user/login")) {
                if (httpRequest.getMethod().equals("POST")) {
                    String userId = httpRequest.getParameter("userId");
                    User user = DataBase.findUserById(userId);

                    if (user != null && user.getPassword().equals(httpRequest.getParameter("password"))) {
                        responseSetCookieHeader(dos, "/index.html","logined=true");
                    } else {
                        responseSetCookieHeader(dos, "/user/login_failed.html", "logined=false");
                    }
                }
            }
            else if(httpRequest.getPath().equals("/user/list")) {
                Map<String, String> cookies = HttpRequestUtils.parseCookies(httpRequest.getHeader("Cookie"));
                boolean isLogined = Boolean.parseBoolean(cookies.get("logined"));
                if (isLogined) {
                    StringBuilder userList = new StringBuilder();

                    int index = 1;
                    for (User user : DataBase.findAll()) {
                        userList.append("<tr>");
                        userList.append("<th scope='row'>").append(index++).append("</th>");
                        userList.append("<td>").append(user.getUserId()).append("</td>");
                        userList.append("<td>").append(user.getName()).append("</td>");
                        userList.append("<td>").append(user.getEmail()).append("</td>");
                        userList.append("<td><a href='#' class='btn btn-success' role='button'>수정</a></td>");
                        userList.append("</tr>");
                    }

                    body = userList.toString().getBytes();
                    response200Header(dos, body.length);
                }
                else {
                    response302Header(dos);
                }
            }
            else if (httpRequest.getPath().equals("/css/styles.css")) {
                body = Files.readAllBytes(new File("./webapp" + httpRequest.getPath()).toPath());
                responseCSSHeader(dos, body.length);
            }
            else {
                body = Files.readAllBytes(new File("./webapp" + httpRequest.getPath()).toPath());
                response200Header(dos, body.length);
            }
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseSetCookieHeader(DataOutputStream dos, String responseUrl, String cookieStatus) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Location: "+ responseUrl +"\r\n");
            dos.writeBytes("Set-Cookie: " + cookieStatus + "\r\n");
            dos.writeBytes("\r\n");
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
    private void responseCSSHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html \r\n");
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
