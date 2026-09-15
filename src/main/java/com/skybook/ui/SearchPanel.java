package com.skybook.ui;

import com.skybook.model.*;
import com.skybook.data.*;
import com.skybook.ui.components.*;
import com.skybook.util.*;
import com.skybook.main.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Arrays;

public class SearchPanel extends JPanel {
    private static final List<String> CITIES = Arrays.asList(
            "New York", "London", "Dubai", "Singapore", "Sydney", 
            "Paris", "Los Angeles", "Tokyo", "Seoul", "Berlin", 
            "Rome", "Madrid", "Miami", "Denver", "Chicago", 
            "Toronto", "Mumbai", "Delhi", "Beijing", "Amsterdam"
    );

    private JTextField fromField, toField, dateField;
    private JTable resultsTable;
    private DefaultTableModel resultsModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public SearchPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initTopPanel();
        initTablePanel();
        initBottomPanel();
        loadFlights(FlightDatabase.getAllFlights());
    }

    private void initTopPanel() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridy = 0; gc.gridx = 0; filterPanel.add(new JLabel("From:"), gc);
        fromField = new AutoCompleteTextField(10, CITIES);
        gc.gridx = 1; filterPanel.add(fromField, gc);

        gc.gridx = 2; filterPanel.add(new JLabel("To:"), gc);
        toField = new AutoCompleteTextField(10, CITIES);
        gc.gridx = 3; filterPanel.add(toField, gc);

        gc.gridx = 4; filterPanel.add(new JLabel("Date (YYYY-MM-DD):"), gc);
        dateField = new JTextField(10);
        gc.gridx = 5; filterPanel.add(dateField, gc);

        JButton searchBtn = new JButton("Search");
        gc.gridx = 6; filterPanel.add(searchBtn, gc);

        JButton showAllBtn = new JButton("Show All");
        gc.gridx = 7; filterPanel.add(showAllBtn, gc);

        searchBtn.addActionListener(e -> performSearch());
        showAllBtn.addActionListener(e -> {
            fromField.setText(""); toField.setText(""); dateField.setText("");
            loadFlights(FlightDatabase.getAllFlights());
        });

        add(filterPanel, BorderLayout.NORTH);
    }

    private void initTablePanel() {
        String[] cols = {"Flight No", "From", "To", "Date", "Depart", "Arrive", "Economy $", "Business $", "Seats Left"};
        resultsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int c) {
                if (c == 6 || c == 7) return Double.class;
                if (c == 8) return Integer.class;
                return String.class;
            }
        };
        resultsTable = new JTable(resultsModel);
        resultsTable.setRowHeight(28);
        sorter = new TableRowSorter<>(resultsModel);
        resultsTable.setRowSorter(sorter);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);
    }

    private void initBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton bookBtn = new JButton("Book Selected Flight");
        bookBtn.addActionListener(e -> {
            int row = resultsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a flight first.");
                return;
            }
            int modelRow = resultsTable.convertRowIndexToModel(row);
            String fNo = (String) resultsModel.getValueAt(modelRow, 0);
            Flight selected = FlightDatabase.getAllFlights().stream().filter(f -> f.getFlightNo().equals(fNo)).findFirst().orElse(null);
            if (selected != null) {
                new BookingDialog((JFrame) SwingUtilities.getWindowAncestor(this), selected, this).setVisible(true);
            }
        });
        bottomPanel.add(bookBtn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void performSearch() {
        String from = fromField.getText();
        String to = toField.getText();
        LocalDate d = null;
        if (!dateField.getText().isBlank()) {
            try {
                d = LocalDate.parse(dateField.getText());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
                return;
            }
        }
        loadFlights(FlightDatabase.search(from, to, d));
    }

    public void loadFlights(List<Flight> flights) {
        resultsModel.setRowCount(0);
        for (Flight f : flights) {
            resultsModel.addRow(new Object[]{
                    f.getFlightNo(), f.getFrom(), f.getTo(), f.getDate().toString(),
                    f.getDepartTime(), f.getArriveTime(), f.getPriceEconomy(),
                    f.getPriceBusiness(), f.getSeatsAvailable()
            });
        }
    }
}
