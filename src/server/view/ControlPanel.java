package server.view;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.*;

public class ControlPanel extends JPanel {
    JButton startButton;
    JButton stopButton;

    public ControlPanel() {
        setLayout(new GridLayout(1, 2));

        startButton = new JButton("Запустить сервер");
        stopButton = new JButton("Остановить сервер");

        startButton.setBackground(Color.PINK);
        stopButton.setBackground(Color.PINK);

        add(startButton);
        add(stopButton);
    }

    public JButton getStartButton() { return startButton; }

    public JButton getStopButton() { return stopButton; }
}
