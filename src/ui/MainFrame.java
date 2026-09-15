package ui;

import model.*;
import data.*;
import ui.components.*;
import util.*;
import app.*;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("SkyBook - Flight Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Search Flights", new SearchPanel());
        tabs.addTab("My Reservations", new ReservationsPanel());
        
        if (UserDatabase.getLoggedInUser() != null && UserDatabase.getLoggedInUser().isAdmin()) {
            tabs.addTab("Admin - Manage Flights", new AdminPanel());
        }
        
        add(tabs);
        setJMenuBar(buildMenuBar());
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(e -> {
            UserDatabase.logout();
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        
        fileMenu.add(logout);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        bar.add(fileMenu);
        return bar;
    }
}
