package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class LoginController extends AbstractController{

    public void doPost(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter("userId");
        User user = DataBase.findUserById(userId);

        if (user != null && user.getPassword().equals(request.getParameter("password"))) {
            response.addHeader("Set-Cookie", "logined=true");
            response.sendRedirect("/index.html");
        } else {
            response.addHeader("Set-Cookie", "logined=false");
            response.sendRedirect("/user/login_failed.html");
        }
    }
}
