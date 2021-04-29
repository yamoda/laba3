package client.view;

import javax.swing.*;
import java.awt.*;

public class ArrayInteractionDialog {
    private final JFrame dialogWindow;

    private final JButton actionButton;
    private final JTextField arrayNameField;

    public ArrayInteractionDialog() {
        dialogWindow = new JFrame();
        dialogWindow.setLayout(new GridLayout(3, 1));

        Font mainFont = new Font("Times Roman", Font.PLAIN,23);

        JLabel arrayNameLabel = new JLabel("Введите имя массива");
        arrayNameField = new JTextField();
        actionButton = new JButton();

        arrayNameField.setFont(mainFont);
        arrayNameLabel.setFont(mainFont);

        dialogWindow.add(arrayNameLabel);
        dialogWindow.add(arrayNameField);
        dialogWindow.add(actionButton);
    }

    public JFrame getFrame() { return dialogWindow; }

    public JButton getActionButton() { return actionButton; }

    public JTextField getArrayNameField() { return arrayNameField; }
}
