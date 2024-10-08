package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class CreateUserController extends AbstractController{
    public void doGet(HttpRequest request, HttpResponse response) {
        User createdUser = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email")
        );
        DataBase.addUser(createdUser);
        response.sendRedirect("/index.html");
    }

    public void doPost(HttpRequest request, HttpResponse response) {
        User createdUser = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email")
        );
        DataBase.addUser(createdUser);
        response.sendRedirect("/index.html");
    }
}
