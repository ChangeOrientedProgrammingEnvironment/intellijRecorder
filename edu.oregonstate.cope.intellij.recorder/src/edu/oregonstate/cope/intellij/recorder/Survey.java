package edu.oregonstate.cope.intellij.recorder;

import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.util.Random;

public class Survey extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton a02YearsRadioButton;
    private JRadioButton a25YearsRadioButton;
    private JRadioButton a510YearsRadioButton;
    private JRadioButton a1015YearsRadioButton;
    private JRadioButton a1520YearsRadioButton;
    private JRadioButton moreThan20YearsRadioButton;

    private JRadioButton Q8d;
    private JRadioButton Q8e;
    private JTextField emailTextField;
    private JRadioButton Q2a;
    private JRadioButton Q2b;
   // private JPanel Q2;
    private JRadioButton Q2c;
    private JRadioButton Q2d;
    private JRadioButton Q2e;
    private JRadioButton Q2f;
    private JRadioButton Q3a;
    private JRadioButton Q3b;
    private JRadioButton Q3c;
    private JRadioButton Q3d;
    private JRadioButton Q4a;
    private JRadioButton Q4b;
    private JRadioButton Q4c;
    private JRadioButton Q4d;
    private JRadioButton Q4e;
    private JRadioButton Q5a;
    private JRadioButton Q5b;
    private JRadioButton Q5c;
    private JRadioButton Q5d;
    private JRadioButton Q6a;
    private JRadioButton Q6b;
    private JRadioButton Q6c;
    private JRadioButton Q6d;
    private JRadioButton Q6e;
    private JRadioButton Q7a;
    private JRadioButton Q7b;
    private JRadioButton Q7c;
    private JRadioButton Q8a;
    private JRadioButton Q8b;
    private JRadioButton Q8c;
    private JRadioButton Q8f;

    private String Q1;
    private String Q2;
    private String Q3;
    private String Q4;
    private String Q5;
    private String Q6;
    private String Q7;
    private String Q8;

    private JSONObject surveyAnswers;
    private String email;

    public Survey() {
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
        ActionListener BG1listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println(e.getSource().toString());
                Q1 = ((JRadioButton)e.getSource()).getText().toString();
            }
        };
        a02YearsRadioButton.addActionListener(BG1listener);
        a25YearsRadioButton.addActionListener(BG1listener);
        a510YearsRadioButton.addActionListener(BG1listener);
        a1015YearsRadioButton.addActionListener(BG1listener);
        a1520YearsRadioButton.addActionListener(BG1listener);
        moreThan20YearsRadioButton.addActionListener(BG1listener);

        ActionListener Q2listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 Q2 = ((JRadioButton)e.getSource()).getText().toString();
            }
        };
        Q2a.addActionListener(Q2listener);
        Q2b.addActionListener(Q2listener);
        Q2c.addActionListener(Q2listener);
        Q2d.addActionListener(Q2listener);
        Q2e.addActionListener(Q2listener);
        Q2f.addActionListener(Q2listener);
        ActionListener Q3listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Q3 = ((JRadioButton)e.getSource()).getText().toString();
            }
        };
        Q3a.addActionListener(Q3listener);
        Q3b.addActionListener(Q3listener);
        Q3c.addActionListener(Q3listener);
        Q3d.addActionListener(Q3listener);
        ActionListener Q4listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Q4 = ((JRadioButton)e.getSource()).getText().toString();
            }
        };
        Q4a.addActionListener(Q4listener);
        Q4b.addActionListener(Q4listener);
        Q4c.addActionListener(Q4listener);
        Q4d.addActionListener(Q4listener);
        Q4e.addActionListener(Q4listener);
        ActionListener Q5listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Q5 = ((JRadioButton)e.getSource()).getText().toString();
            }
        };
        Q5a.addActionListener(Q5listener);
        Q5b.addActionListener(Q5listener);
        Q5c.addActionListener(Q5listener);
        Q5d.addActionListener(Q5listener);
        ActionListener Q6listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Q6 = ((JRadioButton)e.getSource()).getText().toString();
            }
        };
        Q6a.addActionListener(Q6listener);
        Q6b.addActionListener(Q6listener);
        Q6c.addActionListener(Q6listener);
        Q6d.addActionListener(Q6listener);
        Q6e.addActionListener(Q6listener);
        ActionListener Q7listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Q7 = ((JRadioButton)e.getSource()).getText().toString();
            }
        };
        Q7a.addActionListener(Q7listener);
        Q7b.addActionListener(Q7listener);
        Q7c.addActionListener(Q7listener);
        ActionListener Q8listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Q8 = ((JRadioButton)e.getSource()).getText().toString();
            }
        };
        Q8a.addActionListener(Q8listener);
        Q8b.addActionListener(Q8listener);
        Q8c.addActionListener(Q8listener);
        Q8d.addActionListener(Q8listener);
        Q8e.addActionListener(Q8listener);
        Q8f.addActionListener(Q8listener);
    }

    private void onOK() {
        System.out.println(areRadioButtonsComplete());
        surveyAnswers = getSurveyResults();
        this.email = getRandomEmailIfAbsent(emailTextField.getText());
        System.out.println("Email: "+this.email);
        System.out.println("JSON: "+surveyAnswers);
        //dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private boolean areRadioButtonsComplete() {
        if (checkButton1() && checkButton2() && checkButton3() && checkButton4() && checkButton5() && checkButton6() && checkButton7() && checkButton8()) {
            return true;
        }
        return false;
    }

    private boolean checkButton1() {
        if (a02YearsRadioButton.isSelected()) {
            return true;
        } else if (a25YearsRadioButton.isSelected()) {
            return true;
        } else if (a510YearsRadioButton.isSelected()) {
            return true;
        } else if (a1015YearsRadioButton.isSelected()) {
            return true;
        } else if (a1520YearsRadioButton.isSelected()) {
            return true;
        } else if (moreThan20YearsRadioButton.isSelected()) {
            return true;
        }
        return false;
    }

    private boolean checkButton2() {
        if (Q2a.isSelected()) {
            return true;
        } else if (Q2b.isSelected()) {
            return true;
        } else if (Q2c.isSelected()) {
            return true;
        } else if (Q2d.isSelected()) {
            return true;
        } else if (Q2e.isSelected()) {
            return true;
        } else if (Q2f.isSelected()) {
            return true;
        }
        return false;
    }

    private boolean checkButton3() {
        if (Q3a.isSelected()) {
            return true;
        } else if (Q3b.isSelected()) {
            return true;
        } else if (Q3c.isSelected()) {
            return true;
        } else if (Q3d.isSelected()) {
            return true;
        }
        return false;
    }

    private boolean checkButton4() {
        if (Q4a.isSelected()) {
            return true;
        } else if (Q4b.isSelected()) {
            return true;
        } else if (Q4c.isSelected()) {
            return true;
        } else if (Q4d.isSelected()) {
            return true;
        } else if (Q4e.isSelected()) {
            return true;
        }
        return false;
    }

    private boolean checkButton5() {
        if (Q5a.isSelected()) {
            return true;
        } else if (Q5b.isSelected()) {
            return true;
        } else if (Q5c.isSelected()) {
            return true;
        } else if (Q5d.isSelected()) {
            return true;
        }
        return false;
    }

    private boolean checkButton6() {
        if (Q6a.isSelected()) {
            return true;
        } else if (Q6b.isSelected()) {
            return true;
        } else if (Q6c.isSelected()) {
            return true;
        } else if (Q6d.isSelected()) {
            return true;
        } else if (Q6e.isSelected()) {
            return true;
        }
        return false;
    }

    private boolean checkButton7() {
        if (Q7a.isSelected()) {
            return true;
        } else if (Q7b.isSelected()) {
            return true;
        } else if (Q7c.isSelected()) {
            return true;
        }
        return false;
    }

    private boolean checkButton8() {
        if (Q8a.isSelected()) {
            return true;
        } else if (Q8b.isSelected()) {
            return true;
        } else if (Q8c.isSelected()) {
            return true;
        } else if (Q8d.isSelected()) {
            return true;
        } else if (Q8e.isSelected()) {
            return true;
        } else if (Q8f.isSelected()) {
            return true;
        }
        return false;
    }

    public JSONObject getSurveyResults() {
        JSONObject resultObject = new JSONObject();
        resultObject.put("Q1",Q1);
        resultObject.put("Q2",Q2);
        resultObject.put("Q3",Q3);
        resultObject.put("Q4",Q4);
        resultObject.put("Q5",Q5);
        resultObject.put("Q6",Q6);
        resultObject.put("Q7",Q7);
        resultObject.put("Q8",Q8);
        return resultObject;
    }

    private String getRandomEmailIfAbsent(String email) {
        if (email == null || email.trim().isEmpty())
            return new BigInteger(96, new Random()).toString(32);
        else
            return email.trim();
    }

    public String getEmail(){
        return email;
    }
    public JSONObject getsurveyAnswers(){
        return surveyAnswers;
    }
}
