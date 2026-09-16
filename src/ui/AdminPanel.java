package ui;

import model.*;
import data.*;
import ui.components.*;
import util.*;
import app.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdminPanel extends JPanel {
    private JTable flightTable;
    private DefaultTableModel flightModel;

    private JTextField fNoField, fromField, toField, dateField, depField, arrField, ecoField, busField, capField;

    public AdminPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initForm();
        initTable();
    }

    private void initForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add New Flight"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridy = 0; gc.gridx = 0;
        form.add(new JLabel("Flight No:"), gc); fNoField = new JTextField(10); gc.gridx = 1; form.add(fNoField, gc);
        gc.gridx = 2; form.add(new JLabel("From:"), gc); fromField = new JTextField(10); gc.gridx = 3; form.add(fromField, gc);

        gc.gridy = 1; gc.gridx = 0;
        form.add(new JLabel("To:"), gc); toField = new JTextField(10); gc.gridx = 1; form.add(toField, gc);
        gc.gridx = 2; form.add(new JLabel("Date (YYYY-MM-DD):"), gc); dateField = new JTextField(10); gc.gridx = 3; form.add(dateField, gc);

        gc.gridy = 2; gc.gridx = 0;
        form.add(new JLabel("Depart (HH:MM):"), gc); depField = new JTextField(10); gc.gridx = 1; form.add(depField, gc);
        gc.gridx = 2; form.add(new JLabel("Arrive (HH:MM):"), gc); arrField = new JTextField(10); gc.gridx = 3; form.add(arrField, gc);

        gc.gridy = 3; gc.gridx = 0;
        form.add(new JLabel("Eco Price ($):"), gc); ecoField = new JTextField(10); gc.gridx = 1; form.add(ecoField, gc);
        gc.gridx = 2; form.add(new JLabel("Bus Price ($):"), gc); busField = new JTextField(10); gc.gridx = 3; form.add(busField, gc);

        gc.gridy = 4; gc.gridx = 0;
        form.add(new JLabel("Capacity:"), gc); capField = new JTextField(10); gc.gridx = 1; form.add(capField, gc);

        JButton addBtn = new JButton("Add Flight");
        gc.gridy = 5; gc.gridx = 0; gc.gridwidth = 4;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
        form.add(addBtn, gc);

        addBtn.addActionListener(e -> {
            try {
                String no = fNoField.getText();
                String frm = fromField.getText();
                String to = toField.getText();
                LocalDate dt = LocalDate.parse(dateField.getText());
                String dep = depField.getText();
                String arr = arrField.getText();
                double ep = Double.parseDouble(ecoField.getText());
                double bp = Double.parseDouble(busField.getText());
                int cap = Integer.parseInt(capField.getText());

                FlightDatabase.addFlight(new Flight(no, frm, to, dt, dep, arr, ep, bp, cap));
                JOptionPane.showMessageDialog(this, "Flight added successfully!");
                loadFlights();
                
                fNoField.setText(""); fromField.setText(""); toField.setText("");
                dateField.setText(""); depField.setText(""); arrField.setText("");
                ecoField.setText(""); busField.setText(""); capField.setText("");

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date format.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Number format for prices or capacity.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please fill all fields correctly.");
            }
        });

        add(form, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] cols = {"Flight No", "Route", "Date", "Capacity", "Booked"};
        flightModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        flightTable = new JTable(flightModel);
        flightTable.setRowHeight(28);
        add(new JScrollPane(flightTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton rmBtn = new JButton("Remove Selected Flight");
        rmBtn.addActionListener(e -> {
            int row = flightTable.getSelectedRow();
            if (row == -1) return;
            String fNo = (String) flightModel.getValueAt(row, 0);
            FlightDatabase.getAllFlights().stream().filter(f -> f.getFlightNo().equals(fNo)).findFirst().ifPresent(f -> {
                FlightDatabase.removeFlight(f);
                loadFlights();
            });
        });
        bottom.add(rmBtn);
        add(bottom, BorderLayout.SOUTH);
        
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                loadFlights();
            }
        });
        
        loadFlights();
    }

    private void loadFlights() {
        flightModel.setRowCount(0);
        for (Flight f : FlightDatabase.getAllFlights()) {
            flightModel.addRow(new Object[]{
                f.getFlightNo(), f.getFrom() + " -> " + f.getTo(),
                f.getDate().toString(), f.getTotalCapacity(), f.getTakenSeats().size()
            });
        }
    }
}
