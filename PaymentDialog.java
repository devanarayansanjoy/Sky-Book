import javax.swing.*;
import java.awt.*;

public class PaymentDialog extends JDialog {
    private boolean success = false;
    private final double amount;

    public PaymentDialog(JDialog parent, double amount) {
        super(parent, "Payment", true);
        this.amount = amount;
        setSize(350, 250);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        JLabel amtLabel = new JLabel(String.format("Amount Due: $%.2f", amount));
        amtLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(amtLabel, gc);

        gc.gridwidth = 1;
        gc.gridy = 1; gc.gridx = 0; panel.add(new JLabel("Card Number:"), gc);
        JTextField cardField = new JTextField(16);
        gc.gridx = 1; panel.add(cardField, gc);

        gc.gridy = 2; gc.gridx = 0; panel.add(new JLabel("Expiry (MM/YY):"), gc);
        JTextField expField = new JTextField(5);
        gc.gridx = 1; panel.add(expField, gc);

        gc.gridy = 3; gc.gridx = 0; panel.add(new JLabel("CVV:"), gc);
        JTextField cvvField = new JTextField(4);
        gc.gridx = 1; panel.add(cvvField, gc);

        JButton payBtn = new JButton("Confirm Payment");
        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setVisible(false);

        gc.gridy = 4; gc.gridx = 0; gc.gridwidth = 2;
        panel.add(payBtn, gc);
        gc.gridy = 5;
        panel.add(progress, gc);

        payBtn.addActionListener(e -> {
            if (cardField.getText().length() != 16 || !cardField.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid Card Number (must be 16 digits).");
                return;
            }
            if (!expField.getText().matches("\\d{2}/\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Invalid Expiry Format.");
                return;
            }
            if (cvvField.getText().length() != 3 || !cvvField.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid CVV.");
                return;
            }

            payBtn.setEnabled(false);
            progress.setVisible(true);

            // Simulate network payment
            new Timer(1500, evt -> {
                ((Timer)evt.getSource()).stop();
                success = true;
                dispose();
            }).start();
        });

        add(panel);
    }

    public boolean isSuccess() { return success; }
}
