package com.skybook.ui;

import com.skybook.model.*;
import com.skybook.data.*;
import com.skybook.ui.components.*;
import com.skybook.util.*;
import com.skybook.main.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReservationsPanel extends JPanel {
    private JTable resTable;
    private DefaultTableModel resModel;

    public ReservationsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        String[] cols = {"Res ID", "Flight", "Route", "Date", "Status", "Total Paid"};
        resModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        resTable = new JTable(resModel);
        resTable.setRowHeight(28);
        add(new JScrollPane(resTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        JButton cancelBtn = new JButton("Cancel Selected Booking");

        bottom.add(refreshBtn);
        bottom.add(cancelBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadReservations());
        
        cancelBtn.addActionListener(e -> {
            int row = resTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a booking to cancel.");
                return;
            }
            String resId = (String) resModel.getValueAt(row, 0);
            Reservation res = FlightDatabase.getAllReservations().stream()
                    .filter(r -> r.getReservationId().equals(resId)).findFirst().orElse(null);

            if (res != null) {
                if (res.getStatus().equals("Cancelled")) {
                    JOptionPane.showMessageDialog(this, "This booking is already cancelled.");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel booking " + resId + " for a full refund?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    res.cancel();
                    JOptionPane.showMessageDialog(this, "Booking cancelled. Refund of $" + res.getTotalPrice() + " processed.");
                    loadReservations();
                }
            }
        });
        
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                loadReservations();
            }
        });

        loadReservations();
    }

    private void loadReservations() {
        resModel.setRowCount(0);
        User current = UserDatabase.getLoggedInUser();
        if (current == null) return;
        List<Reservation> myRes = FlightDatabase.getReservationsForUser(current);
        for (Reservation r : myRes) {
            resModel.addRow(new Object[]{
                r.getReservationId(), r.getFlight().getFlightNo(),
                r.getFlight().getFrom() + " -> " + r.getFlight().getTo(),
                r.getFlight().getDate().toString(), r.getStatus(),
                String.format("$%.2f", r.getTotalPrice())
            });
        }
    }
}
