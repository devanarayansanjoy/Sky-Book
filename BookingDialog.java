import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BookingDialog extends JDialog {
    private final Flight flight;
    private final SearchPanel searchPanel;
    private Set<Integer> selectedSeats;
    private JTextField[] nameFields;
    private JTextField[] passFields;
    
    public BookingDialog(JFrame parent, Flight flight, SearchPanel searchPanel) {
        super(parent, "Book Flight " + flight.getFlightNo(), true);
        this.flight = flight;
        this.searchPanel = searchPanel;
        setSize(500, 600);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel configPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        configPanel.add(new JLabel("Number of Passengers:"));
        JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(1, 1, flight.getSeatsAvailable(), 1));
        configPanel.add(countSpinner);
        
        configPanel.add(new JLabel("Class:"));
        JComboBox<String> classBox = new JComboBox<>(new String[]{"Economy", "Business"});
        configPanel.add(classBox);
        
        configPanel.add(new JLabel("Extra Baggage ($50 each):"));
        JSpinner bagSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        configPanel.add(bagSpinner);
        main.add(configPanel, BorderLayout.NORTH);

        JPanel passengerPanel = new JPanel();
        passengerPanel.setLayout(new BoxLayout(passengerPanel, BoxLayout.Y_AXIS));
        main.add(new JScrollPane(passengerPanel), BorderLayout.CENTER);

        Runnable refreshPassengers = () -> {
            passengerPanel.removeAll();
            int c = (Integer) countSpinner.getValue();
            nameFields = new JTextField[c];
            passFields = new JTextField[c];
            for (int i = 0; i < c; i++) {
                JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
                p.setBorder(BorderFactory.createTitledBorder("Passenger " + (i+1)));
                p.add(new JLabel("Full Name:"));
                nameFields[i] = new JTextField(15);
                p.add(nameFields[i]);
                p.add(new JLabel("Passport No:"));
                passFields[i] = new JTextField(15);
                p.add(passFields[i]);
                p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                passengerPanel.add(p);
            }
            passengerPanel.add(Box.createVerticalGlue());
            passengerPanel.revalidate();
            passengerPanel.repaint();
            selectedSeats = null; // Reset seats if passenger count changes
        };
        countSpinner.addChangeListener(e -> refreshPassengers.run());
        refreshPassengers.run();

        JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JLabel totalLabel = new JLabel("Total: $0.00", SwingConstants.CENTER);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        Runnable updateTotal = () -> {
            int c = (Integer) countSpinner.getValue();
            double classPrice = flight.getPriceForClass((String) classBox.getSelectedItem());
            double bags = (Integer) bagSpinner.getValue() * 50.0;
            totalLabel.setText(String.format("Total: $%.2f", (classPrice * c) + bags));
        };
        countSpinner.addChangeListener(e -> updateTotal.run());
        classBox.addActionListener(e -> updateTotal.run());
        bagSpinner.addChangeListener(e -> updateTotal.run());
        updateTotal.run();

        JButton seatBtn = new JButton("Select Seats");
        seatBtn.addActionListener(e -> {
            int c = (Integer) countSpinner.getValue();
            SeatMapDialog smd = new SeatMapDialog(this, flight, c);
            smd.setVisible(true);
            if (smd.isConfirmed()) {
                selectedSeats = smd.getSelectedSeats();
                seatBtn.setText("Seats Selected (" + selectedSeats.size() + ")");
            }
        });

        JButton payBtn = new JButton("Proceed to Payment");
        payBtn.addActionListener(e -> {
            if (selectedSeats == null || selectedSeats.size() != (Integer) countSpinner.getValue()) {
                JOptionPane.showMessageDialog(this, "Please select your seats first.");
                return;
            }
            for (JTextField tf : nameFields) if (tf.getText().isBlank()) { JOptionPane.showMessageDialog(this, "Fill all names."); return; }
            for (JTextField tf : passFields) if (tf.getText().isBlank()) { JOptionPane.showMessageDialog(this, "Fill all passports."); return; }

            double total = ((Integer) countSpinner.getValue() * flight.getPriceForClass((String) classBox.getSelectedItem())) + ((Integer)bagSpinner.getValue() * 50.0);
            
            PaymentDialog pd = new PaymentDialog(this, total);
            pd.setVisible(true);
            if (pd.isSuccess()) {
                List<String> pList = new ArrayList<>();
                for (int i=0; i<nameFields.length; i++) {
                    pList.add(nameFields[i].getText() + " (PP: " + passFields[i].getText() + ")");
                }
                Reservation res = new Reservation(flight, UserDatabase.getLoggedInUser(), pList, (String) classBox.getSelectedItem(), (Integer) bagSpinner.getValue(), total, selectedSeats);
                FlightDatabase.addReservation(res);
                flight.bookSeats(selectedSeats);
                
                searchPanel.loadFlights(FlightDatabase.getAllFlights()); // Refresh search table
                
                String receipt = ReceiptGenerator.generateReceipt(res);
                JOptionPane.showMessageDialog(this, new JTextArea(receipt), "Booking Confirmed!", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });

        bottomPanel.add(totalLabel);
        bottomPanel.add(seatBtn);
        bottomPanel.add(payBtn);
        main.add(bottomPanel, BorderLayout.SOUTH);

        add(main);
    }
}
