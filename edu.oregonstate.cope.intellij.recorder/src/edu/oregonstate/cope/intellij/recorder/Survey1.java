package edu.oregonstate.cope.intellij.recorder;

import javax.swing.*;
import java.awt.event.*;

public class Survey1 extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton a02YearsRadioButton;
    private JRadioButton a25YearsRadioButton;
    private JRadioButton a510YearsRadioButton;
    private JRadioButton a1015YearsRadioButton;
    private JRadioButton a1520YearsRadioButton;
    private JRadioButton moreThan20YearsRadioButton;
    private JRadioButton a3039RadioButton;
    private JRadioButton a4049RadioButton;
    private JRadioButton a50OrOlderRadioButton;
    private JTextField emailTextField;

    public Survey1() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

}
