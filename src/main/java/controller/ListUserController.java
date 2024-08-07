package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import java.util.Map;
import model.User;
import util.HttpRequestUtils;

public class ListUserController extends AbstractController{

    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(request.getHeader("Cookie"));

        if (isLogin(cookies.get("logined"))) {
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

            response.forwardBody(userList.toString());
        }
        else {
            response.sendRedirect("/index.html");
        }
    }

    private boolean isLogin(String loginedCookie) {
        return Boolean.parseBoolean(loginedCookie);
    }
}
