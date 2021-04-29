package client;

import client.controller.PatientApplicationController;
import client.view.PatientApplicationView;

public class Main {
    public static void main(String[] args) {
        PatientApplicationView applicationView = new PatientApplicationView();
        PatientApplicationController applicationController = new PatientApplicationController(applicationView);
    }
}
