package server.view;

import javax.swing.*;

import java.awt.BorderLayout;

public class View {
    private final JFrame serverWindow;
    private final ControlPanel serverControlPanel;
    private final LoggingArea logArea;

    public View() {
        serverWindow = new JFrame();
        serverControlPanel = new ControlPanel();
        logArea = new LoggingArea();

        serverWindow.add(logArea, BorderLayout.CENTER);
        serverWindow.add(serverControlPanel, BorderLayout.SOUTH);
    }

    public JFrame getWindow() { return serverWindow; }

    public ControlPanel getControlPanel() { return serverControlPanel; }

    public LoggingArea getLoggingArea() { return logArea; }
}
