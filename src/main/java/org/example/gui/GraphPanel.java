package org.example.gui;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;

public class GraphPanel {

    private JFrame frame;
    private JPanel panel;
    private JLabel verticesLabel;
    private JLabel edgesLabel;
    private JTextField verticesText;
    private JTextField edgesText;
    private JButton button;

    public GraphPanel() {

        frame = new JFrame("Force Directed Graph Layout");
        panel = new JPanel();

        verticesLabel = new JLabel("Number of vertices:");
        edgesLabel = new JLabel("Number of edges:");


        verticesText = new JTextField(10);
        edgesText = new JTextField(10);


        ((AbstractDocument) verticesText.getDocument())
                .setDocumentFilter(new IntegerFilter());

        ((AbstractDocument) edgesText.getDocument())
                .setDocumentFilter(new IntegerFilter());

        button = new JButton("Generate Graph");

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new GridLayout(0, 1, 5, 5));

        panel.add(verticesLabel);
        panel.add(verticesText);
        panel.add(edgesLabel);
        panel.add(edgesText);
        panel.add(button);
        frame.add(panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        //https://www.geeksforgeeks.org/java/message-dialogs-java-gui/
        button.addActionListener(e -> {
            if (verticesText.getText().isEmpty()  || edgesText.getText().isEmpty()){
                JOptionPane.showMessageDialog(frame,
                        "Please enter the missing input(s) ",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        });

    }


}
