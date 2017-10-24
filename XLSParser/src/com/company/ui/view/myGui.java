package com.company.ui.view;

import javax.swing.*;

public class myGui extends JFrame{
    public static final int HEIGHT = 700;
    public static final int WIDTH = 900;



    private JButton button1;
    private JTextArea textArea1;
    private JPanel mypanel;

    public myGui(){
        setSize(WIDTH,HEIGHT);
        setContentPane(mypanel);
        setLocationRelativeTo(null);
    }

    public JButton getButton1() {
        return button1;
    }

    public JTextArea getTextArea1() {
        return textArea1;
    }
}
