package application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by brian on 10/13/18.
 */
public class Window {

    private EventHandlers handler = new EventHandlers();

    private Window() {
    }

    public static void showWindow() {

        JFrame window = createFrame();
        window.getContentPane()
              .add(createPanel("Control Blue LED","On/Off",EventHandlers::blueLedOnOff));
        window.getContentPane()
              .add(createPanel("Control Red LED","On/Off",EventHandlers::redLedOnOff));
        window.getContentPane().add(createPanel("Motor Direction","Left/Right",EventHandlers::changeMotorDirection));
        window.getContentPane().add(createPanel("Control Motor","On/Off",EventHandlers::motorOnOff));
        window.pack();
        window.setVisible(true);
    }


    private static JFrame createFrame() {
        JFrame window = new JFrame();
        window.setLayout(new GridLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return window;
    }

    private static JPanel createPanel(String labelText, String buttonText, ActionListener listener){
        JPanel panel = new JPanel();
        JButton button = new JButton(buttonText);
        button.addActionListener(listener);
        panel.add(new JLabel(labelText));
        panel.add(button);
        return panel;
    }
}
