package data;

import model.*;
import ui.*;
import ui.components.*;
import util.*;
import app.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightDatabase {
    private static final List<Flight> flights = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();
    private static final String DATA_FILE = "reservations.dat";

    static {
        loadFromFile();
        
        // 10 Sample Flights
        flights.add(new Flight("AI101", "New York", "London", LocalDate.now().plusDays(2), "08:00", "20:00", 450.0, 1200.0, 42));
        flights.add(new Flight("BA202", "London", "Dubai", LocalDate.now().plusDays(3), "14:30", "23:45", 380.0, 950.0, 30));
        flights.add(new Flight("EK303", "Dubai", "Singapore", LocalDate.now().plusDays(4), "02:15", "13:00", 520.0, 1400.0, 25));
        flights.add(new Flight("QF404", "Singapore", "Sydney", LocalDate.now().plusDays(5), "22:00", "09:30", 300.0, 800.0, 60));
        flights.add(new Flight("DL505", "New York", "Paris", LocalDate.now().plusDays(1), "19:00", "08:15", 410.0, 1100.0, 15));
        flights.add(new Flight("AA606", "Los Angeles", "Tokyo", LocalDate.now().plusDays(7), "10:00", "15:00", 600.0, 1800.0, 40));
        flights.add(new Flight("JL707", "Tokyo", "Seoul", LocalDate.now().plusDays(8), "09:00", "11:30", 200.0, 500.0, 20));
        flights.add(new Flight("AF808", "Paris", "Berlin", LocalDate.now().plusDays(2), "16:00", "17:45", 150.0, 400.0, 35));
        flights.add(new Flight("LH909", "Berlin", "Rome", LocalDate.now().plusDays(3), "12:30", "14:30", 180.0, 450.0, 28));
        flights.add(new Flight("AZ1010", "Rome", "Madrid", LocalDate.now().plusDays(4), "18:00", "20:30", 160.0, 420.0, 32));
    }

    public static List<Flight> getAllFlights() {
        return new ArrayList<>(flights);
    }

    public static List<Flight> search(String from, String to, LocalDate date) {
        List<Flight> results = flights.stream()
                .filter(f -> (from == null || from.isBlank() || f.getFrom().equalsIgnoreCase(from.trim())))
                .filter(f -> (to == null || to.isBlank() || f.getTo().equalsIgnoreCase(to.trim())))
                .filter(f -> (date == null || f.getDate().equals(date)))
                .collect(Collectors.toList());

        // If specific search yields no results, dynamically generate some flights!
        if (results.isEmpty() && from != null && !from.isBlank() && to != null && !to.isBlank()) {
            LocalDate searchDate = (date != null) ? date : LocalDate.now().plusDays(1);
            
            // Generate some dummy flights for this route
            Flight newFlight1 = new Flight("FL" + (int)(Math.random()*1000 + 100), from.trim(), to.trim(), searchDate, "08:00", "12:00", 300.0, 800.0, 50);
            Flight newFlight2 = new Flight("FL" + (int)(Math.random()*1000 + 100), from.trim(), to.trim(), searchDate, "14:00", "18:00", 350.0, 900.0, 50);
            Flight newFlight3 = new Flight("FL" + (int)(Math.random()*1000 + 100), from.trim(), to.trim(), searchDate, "20:00", "23:59", 250.0, 700.0, 50);
            
            flights.add(newFlight1);
            flights.add(newFlight2);
            flights.add(newFlight3);
            
            results.add(newFlight1);
            results.add(newFlight2);
            results.add(newFlight3);
        }

        return results;
    }

    public static List<Flight> search(String from, String to) {
        return search(from, to, null);
    }

    public static void addFlight(Flight f) { flights.add(f); }
    
    public static void removeFlight(Flight f) { flights.remove(f); }

    public static void addReservation(Reservation r) {
        reservations.add(r);
        saveToFile();
    }

    public static void removeReservation(Reservation r) {
        reservations.remove(r);
        saveToFile();
    }

    public static List<Reservation> getReservationsForUser(User u) {
        return reservations.stream()
                .filter(r -> r.getUser().getId().equals(u.getId()))
                .collect(Collectors.toList());
    }

    public static List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    @SuppressWarnings("unchecked")
    private static void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            reservations = (List<Reservation>) ois.readObject();
        } catch (Exception e) {
            System.out.println("No existing reservations file found or error loading. Starting fresh.");
        }
    }

    private static void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(reservations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
