package ui;

import model.User;
import data.UserDatabase;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private JTextField fullNameField, emailField, phoneField;
    private JPasswordField passwordField;

    public ProfilePanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        User user = UserDatabase.getLoggedInUser();
        if (user == null) return;

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        add(title, gc);

        gc.gridwidth = 1;
        
        // Non-editable Username
        gc.gridy = 1; gc.gridx = 0;
        add(new JLabel("Username:"), gc);
        JTextField usernameField = new JTextField(user.getUsername(), 15);
        usernameField.setEditable(false);
        gc.gridx = 1;
        add(usernameField, gc);

        // Editable Full Name
        gc.gridy = 2; gc.gridx = 0;
        add(new JLabel("Full Name:"), gc);
        fullNameField = new JTextField(user.getFullName(), 15);
        gc.gridx = 1;
        add(fullNameField, gc);

        // Editable Email
        gc.gridy = 3; gc.gridx = 0;
        add(new JLabel("Email Address:"), gc);
        emailField = new JTextField(user.getEmail() != null ? user.getEmail() : "", 15);
        gc.gridx = 1;
        add(emailField, gc);

        // Editable Phone
        gc.gridy = 4; gc.gridx = 0;
        add(new JLabel("Phone Number:"), gc);
        phoneField = new JTextField(user.getPhoneNumber() != null ? user.getPhoneNumber() : "", 15);
        gc.gridx = 1;
        add(phoneField, gc);

        // Editable Password
        gc.gridy = 5; gc.gridx = 0;
        add(new JLabel("Password:"), gc);
        passwordField = new JPasswordField(user.getPassword(), 15);
        gc.gridx = 1;
        add(passwordField, gc);

        JButton updateBtn = new JButton("Update Profile");
        JButton logoutBtn = new JButton("Logout");
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.add(updateBtn);
        btnPanel.add(logoutBtn);

        gc.gridy = 6; gc.gridx = 0; gc.gridwidth = 2;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
        gc.insets = new Insets(20, 10, 10, 10);
        add(btnPanel, gc);

        logoutBtn.addActionListener(e -> {
            UserDatabase.logout();
            new LoginFrame().setVisible(true);
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        updateBtn.addActionListener(e -> {
            String name = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String pass = new String(passwordField.getPassword());

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            user.setFullName(name);
            user.setEmail(email);
            user.setPhoneNumber(phone);
            user.setPassword(pass);
            
            UserDatabase.updateUser(user);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
