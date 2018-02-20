/* 
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Oleg Kurbatov (o.v.kurbatov@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.firmata4j;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import jssc.SerialNativeInterface;
import jssc.SerialPortList;
import org.firmata4j.ui.JPinboard;

/**
 * Example of usage {@link JPinboard}.
 * 
 * @author Oleg Kurbatov &lt;o.v.kurbatov@gmail.com&gt;
 */
public class Example {

    private static final JFrame INITIALIZATION_FRAME = new JFrame();

    public static void main(String[] args) throws IOException, InterruptedException {
        try { // set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Example.class.getName()).log(Level.SEVERE, "Cannot load system look and feel.", ex);
        }
        // requesting a user to define the port name
        String port = requestPort();
        final IODevice device = new FirmataDevice(port);
        showInitializationMessage();
        device.start();
        try {
            device.ensureInitializationIsDone();
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(INITIALIZATION_FRAME, e.getMessage(), "Connection error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        hideInitializationWindow();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame mainFrame = new JFrame("Pinboard Example");
                GridBagLayout layout = new GridBagLayout();
                GridBagConstraints constraints = new GridBagConstraints();
                mainFrame.setLayout(layout);
                constraints.gridy = 0;
                constraints.fill = GridBagConstraints.BOTH;
                constraints.weightx = 1;
                constraints.weighty = 1;
                JPinboard pinboard = new JPinboard(device);
                layout.setConstraints(pinboard, constraints);
                mainFrame.add(pinboard);
                mainFrame.pack();
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        try {
                            device.stop();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        super.windowClosing(e);
                    }
                });
                mainFrame.setVisible(true);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static String requestPort() {
        return getPortName();
    }

    public static String getPortName() {
        JComboBox<String> portNameSelector = new JComboBox<>();
        portNameSelector.setModel(new DefaultComboBoxModel<String>());
        String[] portNames;
        if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_MAC_OS_X) {
            // for MAC OS default pattern of jssc library is too restrictive
            portNames = SerialPortList.getPortNames("/dev/", Pattern.compile("tty\\..*"));
        } else {
            portNames = SerialPortList.getPortNames();
        }
        for (String portName : portNames) {
            portNameSelector.addItem(portName);
        }
        if (portNameSelector.getItemCount() == 0) {
            JOptionPane.showMessageDialog(null, "Cannot find any serial port", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(new JLabel("Port "));
        panel.add(portNameSelector);
        if (JOptionPane.showConfirmDialog(null, panel, "Select the port", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            return portNameSelector.getSelectedItem().toString();
        } else {
            System.exit(0);
        }
        return "";
    }

    private static void showInitializationMessage() {
        showInitializationMessage(INITIALIZATION_FRAME);
    }

    static void showInitializationMessage(JFrame initializationFrame) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                JFrame frame = initializationFrame;
                frame.setUndecorated(true);
                JLabel label = new JLabel("Connecting to device");
                label.setHorizontalAlignment(JLabel.CENTER);
                frame.add(label);
                frame.pack();
                frame.setSize(frame.getWidth() + 40, frame.getHeight() + 40);
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
                int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
                frame.setLocation(x, y);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void hideInitializationWindow() {
        hideInitializationWindow(INITIALIZATION_FRAME);
    }

    static void hideInitializationWindow(JFrame initializationFrame) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    initializationFrame.setVisible(false);
                    initializationFrame.dispose();
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }
}
