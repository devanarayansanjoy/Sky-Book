package ui;

import model.*;
import data.*;
import ui.components.*;
import util.*;
import app.*;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;

    public LoginFrame() {
        setTitle("SkyBook - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Welcome to SkyBook");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        panel.add(title, gc);

        gc.gridwidth = 1;
        gc.gridy = 1; gc.gridx = 0;
        panel.add(new JLabel("Username:"), gc);
        userField = new JTextField(15);
        gc.gridx = 1;
        panel.add(userField, gc);

        gc.gridy = 2; gc.gridx = 0;
        panel.add(new JLabel("Password:"), gc);
        passField = new JPasswordField(15);
        gc.gridx = 1;
        panel.add(passField, gc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        gc.gridy = 3; gc.gridx = 0; gc.gridwidth = 2;
        panel.add(btnPanel, gc);

        loginBtn.addActionListener(e -> {
            String u = userField.getText();
            String p = new String(passField.getPassword());
            if (u.isBlank() || p.isBlank()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User loggedIn = UserDatabase.login(u, p);
            if (loggedIn != null) {
                new MainFrame().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerBtn.addActionListener(e -> {
            RegistrationDialog dialog = new RegistrationDialog(this);
            dialog.setVisible(true);
        });

        add(panel);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
