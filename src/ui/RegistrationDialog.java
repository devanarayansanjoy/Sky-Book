package ui;

import data.UserDatabase;

import javax.swing.*;
import java.awt.*;

public class RegistrationDialog extends JDialog {
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField adminSecretField;
    private JLabel adminSecretLabel;
    private JRadioButton userRadio;
    private JRadioButton adminRadio;

    public RegistrationDialog(JFrame parent) {
        super(parent, "Register New Account", true);
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        panel.add(title, gc);

        // Role Selection
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userRadio = new JRadioButton("Normal User", true);
        adminRadio = new JRadioButton("Administrator");
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(userRadio);
        roleGroup.add(adminRadio);
        rolePanel.add(userRadio);
        rolePanel.add(adminRadio);
        
        gc.gridy = 1; gc.gridwidth = 2;
        panel.add(rolePanel, gc);

        gc.gridwidth = 1;
        
        // Full Name
        gc.gridy = 2; gc.gridx = 0;
        panel.add(new JLabel("Full Name:"), gc);
        fullNameField = new JTextField(15);
        gc.gridx = 1;
        panel.add(fullNameField, gc);

        // Email
        gc.gridy = 3; gc.gridx = 0;
        panel.add(new JLabel("Email Address:"), gc);
        emailField = new JTextField(15);
        gc.gridx = 1;
        panel.add(emailField, gc);

        // Phone Number
        gc.gridy = 4; gc.gridx = 0;
        panel.add(new JLabel("Phone Number:"), gc);
        phoneField = new JTextField(15);
        gc.gridx = 1;
        panel.add(phoneField, gc);

        // Username
        gc.gridy = 5; gc.gridx = 0;
        panel.add(new JLabel("Username:"), gc);
        usernameField = new JTextField(15);
        gc.gridx = 1;
        panel.add(usernameField, gc);

        // Password
        gc.gridy = 6; gc.gridx = 0;
        panel.add(new JLabel("Password:"), gc);
        passwordField = new JPasswordField(15);
        gc.gridx = 1;
        panel.add(passwordField, gc);

        // Admin Secret Code
        adminSecretLabel = new JLabel("Admin Secret Code:");
        adminSecretField = new JTextField(15);
        adminSecretLabel.setVisible(false);
        adminSecretField.setVisible(false);
        
        gc.gridy = 7; gc.gridx = 0;
        panel.add(adminSecretLabel, gc);
        gc.gridx = 1;
        panel.add(adminSecretField, gc);

        // Toggle Admin Secret Code visibility based on role selection
        adminRadio.addActionListener(e -> {
            adminSecretLabel.setVisible(true);
            adminSecretField.setVisible(true);
            revalidate();
            repaint();
        });
        userRadio.addActionListener(e -> {
            adminSecretLabel.setVisible(false);
            adminSecretField.setVisible(false);
            revalidate();
            repaint();
        });

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton registerBtn = new JButton("Register");
        JButton cancelBtn = new JButton("Cancel");
        btnPanel.add(registerBtn);
        btnPanel.add(cancelBtn);

        gc.gridy = 8; gc.gridx = 0; gc.gridwidth = 2;
        gc.insets = new Insets(15, 8, 8, 8);
        panel.add(btnPanel, gc);

        cancelBtn.addActionListener(e -> dispose());

        registerBtn.addActionListener(e -> handleRegistration());

        add(panel);
    }

    private void handleRegistration() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        boolean isAdmin = adminRadio.isSelected();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isAdmin) {
            String secretCode = adminSecretField.getText().trim();
            if (!secretCode.equals("SKYBOOK-ADMIN")) {
                JOptionPane.showMessageDialog(this, "Invalid Admin Secret Code.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        boolean success = UserDatabase.registerUser(username, password, fullName, email, phone, isAdmin);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
