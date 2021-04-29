package client.controller;

import client.view.*;
import server.model.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

public class PatientApplicationController {
    private static final int DEFAULT_PORT = 9090;
    private static final String DEFAULT_ADDRESS = "127.0.0.1";

    private PatientApplicationView view;

    Socket client;
    Properties connectProps;

    PrintWriter out;
    ObjectInputStream in;

    Thread clientThread = null;
    private boolean isRunning = true;

    public PatientApplicationController(PatientApplicationView view) {
        this.view = view;

        final int windowWidth = 1200;
        final int windowHeight = 800;

        this.view.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.view.getFrame().setSize(windowWidth, windowHeight);
        this.view.getFrame().setVisible(true);

        connectProps = readProperties("config.properties");
        initController();
    }

    private void initController() {
        view.getConnectButton().addActionListener(e -> connect());

        view.getSaveButton().addActionListener(e -> save());
        view.getOpenButton().addActionListener(e -> load());

        view.getNewEntryButton().addActionListener(e -> initAdditionDialog());
        view.getSearchButton().addActionListener(e -> initSearchingDialog());
        view.getDeleteButton().addActionListener(e -> initRemovalDialog());

        view.getMainTable().getFirstPageButton().addActionListener(e -> getFirstPage());
        view.getMainTable().getLastPageButton().addActionListener(e -> getLastPage());
        view.getMainTable().getNextPageButton().addActionListener(e -> getNextPage());
        view.getMainTable().getPrevPageButton().addActionListener(e -> getPrevPage());

        view.getSaveItem().addActionListener(e -> save());
        view.getOpenItem().addActionListener(e -> load());
        view.getNewEntryItem().addActionListener(e -> initAdditionDialog());
        view.getSearchItem().addActionListener(e -> initSearchingDialog());
        view.getDeleteItem().addActionListener(e -> initRemovalDialog());
    }

    private void getNextPage() {
        if (out != null) {
            out.println("NP");
        }
    }

    private void getPrevPage() {
        if (out != null) {
            out.println("PP");
        }
    }

    private void getFirstPage() {
        if (out != null) {
            out.println("FP");
        }
    }

    private void getLastPage() {
        if (out != null) {
            out.println("LP");
        }
    }

    private void connect() {
        if (clientThread == null) {
            clientThread = new Thread() {
                @Override
                public void run() {
                    try {
                        client = new Socket(connectProps.getProperty("address"), Integer.parseInt(connectProps.getProperty("port")));

                        out = new PrintWriter(client.getOutputStream(), true);
                        in = new ObjectInputStream(client.getInputStream());

                        isRunning = true;
                        while (isRunning) {
                            Object receivedArr = in.readObject();
                            if (receivedArr == null) continue;
                            updateTableView((ArrayList<Object[]>) (receivedArr));
                        }
                    }
                    catch (SocketException e) { clientThread = null; }
                    catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
                }
            };
            clientThread.start();
        }
    }

    private void updateTableView(ArrayList<Object[]> newRows) {
        DefaultTableModel tableViewModel = (DefaultTableModel) view.getMainTable().getTable().getModel();
        tableViewModel.setRowCount(0);
        newRows.forEach(tableViewModel::addRow);
    }

    public static Properties readProperties(String fileName) {
        FileInputStream propertiesFileStream = null;
        Properties prop = null;

        try {
            propertiesFileStream = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(propertiesFileStream);
            propertiesFileStream.close();
        }
        catch (Exception e) {
            prop = new Properties();
            prop.setProperty("address", DEFAULT_ADDRESS);
            prop.setProperty("port", String.valueOf(DEFAULT_PORT));
        }

        return prop;
    }

    private void load() {
        final int windowWidth = 400;
        final int windowHeight = 200;

        ArrayInteractionDialog interactionDialog = new ArrayInteractionDialog();
        interactionDialog.getFrame().setTitle("Load");
        interactionDialog.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        interactionDialog.getFrame().setSize(windowWidth, windowHeight);
        interactionDialog.getFrame().setVisible(true);
        interactionDialog.getActionButton().setText("Загрузить");

        interactionDialog.getActionButton().addActionListener(e -> {
            String fileName = interactionDialog.getArrayNameField().getText();
            if (out != null)  {
                out.println("L|"+fileName);
                interactionDialog.getFrame().dispose();
            };
        });
    }

    private void save() {
        final int windowWidth = 400;
        final int windowHeight = 200;

        ArrayInteractionDialog interactionDialog = new ArrayInteractionDialog();
        interactionDialog.getFrame().setTitle("Save");
        interactionDialog.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        interactionDialog.getFrame().setSize(windowWidth, windowHeight);
        interactionDialog.getFrame().setVisible(true);
        interactionDialog.getActionButton().setText("Сохранить");

        interactionDialog.getActionButton().addActionListener(e -> {
            String fileName = interactionDialog.getArrayNameField().getText();
            if (out != null)  {
                out.println("S|"+fileName);
                interactionDialog.getFrame().dispose();
            };
        });
    }

    private void initAdditionDialog() {
        final int windowWidth = 870;
        final int windowHeight = 500;

        var additionDialog = new AddToTableView();
        additionDialog.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        additionDialog.getFrame().setSize(windowWidth, windowHeight);
        additionDialog.getFrame().setVisible(true);

        additionDialog.getInputButton().addActionListener(e -> {
            var fullName = additionDialog.getFullNameField().getText();
            var address = additionDialog.getAddressField().getText();
            var doctorName = additionDialog.getDoctorFullNameField().getText();
            var conclusion = additionDialog.getConclusionField().getText();

            java.util.Date aDate = (java.util.Date) additionDialog.getAppointmentDateField().getValue();
            java.util.Date bDate = (java.util.Date) additionDialog.getBirthDateField().getValue();

            Date appointmentDate = new Date(aDate);
            Date birthDate = new Date(bDate);

            if(!fullName.isEmpty() && !address.isEmpty() && !doctorName.isEmpty() && !conclusion.isEmpty()) {
                if (out != null) {
                    out.println("ADD" + "|" + fullName + "|" + address + "|" + doctorName + "|" + appointmentDate.toString() + "|" + birthDate.toString() + "|" + conclusion);
                    additionDialog.getFrame().dispose();
                }
            }
            else JOptionPane.showMessageDialog(null, "Все строки должны быть заполнены корректно");
        });
    }

    private void initSearchingDialog() {
        final int windowWidth = 870;
        final int windowHeight = 500;

        var searchingDialog = new SearchingView();
        searchingDialog.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        searchingDialog.getFrame().setSize(windowWidth, windowHeight);
        searchingDialog.getFrame().setVisible(true);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1900);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 0);

        java.util.Date date = cal.getTime();
        searchingDialog.getBirthDateField().setValue(date);

        searchingDialog.getSearchButton().addActionListener(e -> {
            var fullName = searchingDialog.getFullNameField().getText();
            var doctorName = searchingDialog.getDoctorNameField().getText();

            java.util.Date bDate = (java.util.Date) searchingDialog.getBirthDateField().getValue();
            Date birthDate = new Date(bDate);

            if (out != null) {
                if (fullName.isEmpty()) fullName = "-1";
                if (doctorName.isEmpty()) doctorName = "-1";

                out.println("SRCH|"+ fullName + "|" + doctorName + "|" + birthDate.toString());

                try {
                    Thread.sleep(100);

                    Socket searchClient = new Socket(connectProps.getProperty("address"), 9000);
                    ObjectInputStream outRead = new ObjectInputStream(searchClient.getInputStream());

                    ArrayList<Object[]> foundArr = (ArrayList<Object[]>) outRead.readObject();

                    DefaultTableModel tableViewModel = (DefaultTableModel) searchingDialog.getMatchesTable().getTable().getModel();
                    tableViewModel.setRowCount(0);

                    foundArr.forEach(tableViewModel::addRow);
                    searchClient.close();
                }
                catch (IOException | InterruptedException | ClassNotFoundException ex) { ex.printStackTrace(); }
            }
        });
    }

    private void initRemovalDialog() {
        final int windowWidth = 870;
        final int windowHeight = 300;

        var removalDialog = new RemovalView();
        removalDialog.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        removalDialog.getFrame().setSize(windowWidth, windowHeight);
        removalDialog.getFrame().setVisible(true);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1900);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 0);

        java.util.Date date = cal.getTime();
        removalDialog.getBirthDateField().setValue(date);

        removalDialog.getRemovalButton().addActionListener(e -> {
            var fullName = removalDialog.getFullNameField().getText();
            var doctorName = removalDialog.getDoctorNameField().getText();

            java.util.Date bDate = (java.util.Date) removalDialog.getBirthDateField().getValue();
            Date birthDate = new Date(bDate);

            if (out != null) {
                if (fullName.isEmpty()) fullName = "-1";
                if (doctorName.isEmpty()) doctorName = "-1";

                out.println("RMV|" + fullName + "|" + doctorName + "|" + birthDate.toString());

                try {
                    Thread.sleep(100);

                    Socket removeClient = new Socket(connectProps.getProperty("address"), 8000);
                    BufferedReader outRead = new BufferedReader(new InputStreamReader(removeClient.getInputStream()));

                    int numRemoved = Integer.parseInt(outRead.readLine());

                    if (numRemoved == 0) removalDialog.getRemovalInfo().setText("Ничего не было удалено");
                    else removalDialog.getRemovalInfo().setText("Было удалено записей " + numRemoved);

                    removeClient.close();
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
