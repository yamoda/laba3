package server;

import server.controller.Controller;
import server.view.View;


public class Main {
    public static void main(String[] args) {
        var testView = new View();
        var testController = new Controller(testView);
    }
}
