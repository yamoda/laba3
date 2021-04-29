package server.controller;

import server.model.*;
import server.view.View;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    private boolean isRunning = true;
    private final int port = 9090;

    Thread serverThread = null;
    Socket clientSocket = null;
    ServerSocket serverSocket = null;

    private View view;
    private PatientModel model;
    private Logger logger;

    public Controller(View view) {
        this.view = view;
        this.model=  new PatientModel(10);

        this.logger = new Logger(view.getLoggingArea().getLogArea());
        this.view.getLoggingArea().getLogArea().setEditable(false);

        this.view.getWindow().setTitle("Server");
        this.view.getWindow().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.view.getWindow().setSize(800, 600);
        this.view.getWindow().setVisible(true);

        this.view.getControlPanel().getStartButton().addActionListener(e -> runServer());
        this.view.getControlPanel().getStopButton().addActionListener(e -> stopServer());
    }

    private void runServer() {
        if (serverThread == null) {
            serverThread = new Thread() {
                @Override
                public void run() {
                    try {
                        logger.addLog("Сервер запустился");

                        isRunning = true;
                        serverSocket = new ServerSocket(9090);

                        logger.addLog("Ожидание подключения от клиента");
                        clientSocket = serverSocket.accept();

                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

                        logger.addLog("Клиент подключен");
                        while (isRunning) {
                            String request = in.readLine();
                            if (request != null) {
                                String[] args = request.split("\\|");

                                switch (args[0]) {
                                    case "L" -> {
                                        String fileName = args[1] + ".xml";
                                        logger.addLog("Запрос на загрузку массива " + fileName);
                                        load(fileName, out);
                                        logger.addLog("Запрос на загрузку массива выполнен успешно");
                                    }
                                    case "S" -> {
                                        String fileName = args[1] + ".xml";
                                        logger.addLog("Запрос на сохранение массива " + fileName);
                                        save(fileName);
                                        logger.addLog("Запрос на сохранение массива выполнен успешно");
                                    }
                                    case "NP" -> {
                                        logger.addLog("Запрос на следующую страницу");
                                        nextPage(out);
                                        logger.addLog("Запрос на следующую страницу выполнен успешно");
                                    }
                                    case "PP" -> {
                                        logger.addLog("Запрос на предыдущую страницу");
                                        prevPage(out);
                                        logger.addLog("Запрос на предыдущую страницу выполнен успешно");
                                    }
                                    case "FP" -> {
                                        logger.addLog("Запрос на первую страницу");
                                        firstPage(out);
                                        logger.addLog("Запрос на первую страницу выполнен успешно");
                                    }
                                    case "LP" -> {
                                        logger.addLog("Запрос на последнюю страницу");
                                        lastPage(out);
                                        logger.addLog("Запрос на последнюю страницу выполнен успешно");
                                    }
                                    case "ADD" -> {
                                        logger.addLog("Запрос добавления " + request);
                                        add(args, out);
                                        logger.addLog("Запрос добавления выполнен успешно");
                                    }
                                    case "SRCH" -> {
                                        logger.addLog("Запрос на поиск " + request);
                                        search(args);
                                        logger.addLog("Запрос поиска выполнен успешно");
                                    }
                                    case "RMV" -> {
                                        logger.addLog("Запрос на удаление " + request);
                                        delete(args, out);
                                        logger.addLog("Запрос удаления выполнен успешно");
                                    }
                                }
                            }
                        }

                        in.close();
                        out.close();

                        clientSocket.close();
                        serverSocket.close();
                    } catch (SocketException e) {
                        logger.addLog("Сервер остановлен");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            serverThread.start();
        }
    }

    private void stopServer() {
        if (serverSocket != null) {
            try {
                isRunning = false;
                serverSocket.close();
                serverThread = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void load(String fileName, ObjectOutputStream out) throws IOException {
        PatientParser xmlDocumentReader = new PatientParser();
        ArrayList<Patient> documentEntries = xmlDocumentReader.readAndParse(fileName);
        model.setPatients(documentEntries);
        out.writeObject(model.getPage(model.getCurrentPage()));
    }

    private void save(String fileName) {
        PatientParser xmlModelWriter = new PatientParser();
        xmlModelWriter.parseAndWrite("Patients", "Patient", model.getPatients(), fileName);
    }

    private void add(String[] args, ObjectOutputStream out) throws IOException {
        String fullName = args[1];
        String address = args[2];
        String doctorName = args[3];
        Date appointmentDate = new Date(args[4]);
        Date birthDate = new Date(args[5]);
        String conclusion = args[6];

        ArrayList<Patient> patientModel = model.getPatients();
        patientModel.add(new Patient(fullName, address, birthDate, appointmentDate, doctorName, conclusion));
        model.setPatients(patientModel);

        out.writeObject(model.getPage(model.getCurrentPage()));
    }

    private boolean stringMatch(String firstString, String secondString) {
        String[] firstNameByPosition = firstString.split(" ");
        String[] secondNameByPosition = secondString.split(" ");
        int minLength = Math.min(firstNameByPosition.length, secondNameByPosition.length);

        for (int i=0; i<minLength; i++) {
            if (!firstNameByPosition[i].equals(secondNameByPosition[i])) {
                return false;
            }
        }

        return true;
    }

    private List<Patient> search(String fullName, String doctorName, Date birthDate) {
        String strIgnoreValue = "-1";
        String birthDateIgnoreString = "31/12/1899";

        return model.getPatients()
                .stream()
                .filter(patient -> stringMatch(patient.getFullName(), fullName) || fullName.equals(strIgnoreValue))
                .filter(patient -> stringMatch(patient.getDoctorName(), doctorName) || doctorName.equals(strIgnoreValue))
                .filter(patient -> patient.getBirthDate().toString().equals(birthDate.toString()) || birthDate.toString().equals(birthDateIgnoreString))
                .collect(Collectors.toList());
    }

    private void search(String[] args) throws IOException {
        List<Patient> found = search(args[1], args[2], new Date(args[3]));

        ServerSocket foundSocket = new ServerSocket(9000);
        Socket foundRequest = foundSocket.accept();

        ObjectOutputStream foundOut = new ObjectOutputStream(foundRequest.getOutputStream());
        foundOut.writeObject(found.stream().map(Patient::toObject).collect(Collectors.toList()));

        foundOut.close();
        foundRequest.close();
        foundSocket.close();
    }

    private void delete(String[] args, ObjectOutputStream out) throws IOException {
        List<Patient> toRemove = search(args[1], args[2], new Date(args[3]));
        model.getPatients().removeAll(toRemove);

        ServerSocket removeSocket = new ServerSocket(8000);
        Socket removeRequest = removeSocket.accept();

        PrintWriter removeOut = new PrintWriter(removeRequest.getOutputStream());

        removeOut.println(toRemove.size());
        out.writeObject(model.getPage(model.getCurrentPage()));

        removeOut.close();
        removeRequest.close();
        removeSocket.close();
    }

    private void nextPage(ObjectOutputStream out) throws IOException {
        int currentPage = model.getCurrentPage();
        List<Object> nextPage = model.getPage(currentPage + 1);
        out.writeObject(nextPage);

        if (nextPage != null) model.setCurrentPage(currentPage + 1);
    }

    private void prevPage(ObjectOutputStream out) throws  IOException {
        int currentPage = model.getCurrentPage();
        List<Object> nextPage = model.getPage(currentPage - 1);
        out.writeObject(nextPage);

        if (nextPage != null) model.setCurrentPage(currentPage - 1);
    }

    private void firstPage(ObjectOutputStream out) throws IOException {
        model.setCurrentPage(1);
        out.writeObject(model.getPage(1));
    }

    private void lastPage(ObjectOutputStream out) throws IOException {
        int lastPage = model.getPatients().size() / model.getRowsOnPage() + 1;
        model.setCurrentPage(lastPage);
        out.writeObject(model.getPage(lastPage));
    }
}
