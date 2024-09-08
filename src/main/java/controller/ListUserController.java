package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpSession;
import model.User;

public class ListUserController extends AbstractController{

    public void doPost(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession();

        if (isLogin(session)) {
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

    private boolean isLogin(HttpSession session) {
        return session.getAttribute("user") != null;
    }
}
