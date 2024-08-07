package controller;

import http.HttpRequest;
import http.HttpResponse;

public class AbstractController implements Controller{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.equalsMethod("GET")) {
            doGet(request, response);
        }
        if (request.equalsMethod("POST")) {
            doPost(request, response);
        }

    }
    private void doGet(HttpRequest request, HttpResponse response) {}
    private void doPost(HttpRequest request, HttpResponse response) {}
}
