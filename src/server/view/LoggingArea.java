package server.view;

import javax.swing.*;
import java.awt.*;

public class LoggingArea extends JPanel{
    private final JTextArea logArea;

    public LoggingArea() {
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setFont(new Font("Helvetica", Font.PLAIN,17));

        logArea.setBackground(Color.DARK_GRAY);
        logArea.setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(logArea);
        add(scroll, BorderLayout.CENTER);
    }

    public JTextArea getLogArea() { return logArea; }
}
