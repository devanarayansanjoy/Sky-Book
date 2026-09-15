import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class SeatMapDialog extends JDialog {
    private final Flight flight;
    private final int requiredSeats;
    private final Set<Integer> selectedSeats = new HashSet<>();
    private boolean confirmed = false;

    public SeatMapDialog(JDialog parent, Flight flight, int requiredSeats) {
        super(parent, "Select Seats", true);
        this.flight = flight;
        this.requiredSeats = requiredSeats;
        setSize(400, 500);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel info = new JLabel("Please select " + requiredSeats + " seat(s).");
        info.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(info, BorderLayout.NORTH);

        int cap = flight.getTotalCapacity();
        int cols = 4;
        int rows = (int) Math.ceil((double) cap / cols);
        JPanel grid = new JPanel(new GridLayout(rows, cols, 5, 5));

        for (int i = 0; i < cap; i++) {
            JButton btn = new JButton(String.valueOf(i + 1));
            final int seatIndex = i;
            if (flight.isSeatTaken(i)) {
                btn.setBackground(Color.RED);
                btn.setEnabled(false);
            } else {
                btn.setBackground(Color.GREEN);
                btn.addActionListener(e -> {
                    if (selectedSeats.contains(seatIndex)) {
                        selectedSeats.remove(seatIndex);
                        btn.setBackground(Color.GREEN);
                    } else {
                        if (selectedSeats.size() < requiredSeats) {
                            selectedSeats.add(seatIndex);
                            btn.setBackground(Color.BLUE);
                        } else {
                            JOptionPane.showMessageDialog(this, "You have already selected " + requiredSeats + " seats.");
                        }
                    }
                });
            }
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            grid.add(btn);
        }

        panel.add(new JScrollPane(grid), BorderLayout.CENTER);

        JButton confirmBtn = new JButton("Confirm Seats");
        confirmBtn.addActionListener(e -> {
            if (selectedSeats.size() != requiredSeats) {
                JOptionPane.showMessageDialog(this, "Please select exactly " + requiredSeats + " seats.");
            } else {
                confirmed = true;
                dispose();
            }
        });
        panel.add(confirmBtn, BorderLayout.SOUTH);

        add(panel);
    }

    public boolean isConfirmed() { return confirmed; }
    public Set<Integer> getSelectedSeats() { return selectedSeats; }
}
