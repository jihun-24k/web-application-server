package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpSession;
import model.User;

public class LoginController extends AbstractController{

    public void doPost(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter("userId");
        User user = DataBase.findUserById(userId);

        if (user != null && user.getPassword().equals(request.getParameter("password"))) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("/index.html");
        } else {
            response.sendRedirect("/user/login_failed.html");
        }
    }
}
