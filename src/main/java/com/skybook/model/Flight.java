package com.skybook.model;

import com.skybook.data.*;
import com.skybook.ui.*;
import com.skybook.ui.components.*;
import com.skybook.util.*;
import com.skybook.main.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Flight {
    private String flightNo;
    private String from;
    private String to;
    private LocalDate date;
    private String departTime;
    private String arriveTime;
    private double priceEconomy;
    private double priceBusiness;
    private int seatsAvailable;
    private int totalCapacity;
    private Set<Integer> takenSeats; // Store taken seat numbers (0 to totalCapacity - 1)

    public Flight(String flightNo, String from, String to, LocalDate date,
                  String departTime, String arriveTime,
                  double priceEconomy, double priceBusiness, int totalCapacity) {
        this.flightNo = flightNo;
        this.from = from;
        this.to = to;
        this.date = date;
        this.departTime = departTime;
        this.arriveTime = arriveTime;
        this.priceEconomy = priceEconomy;
        this.priceBusiness = priceBusiness;
        this.totalCapacity = totalCapacity;
        this.seatsAvailable = totalCapacity;
        this.takenSeats = new HashSet<>();
    }

    // Getters and Setters
    public String getFlightNo() { return flightNo; }
    public void setFlightNo(String flightNo) { this.flightNo = flightNo; }
    
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getDepartTime() { return departTime; }
    public void setDepartTime(String departTime) { this.departTime = departTime; }
    
    public String getArriveTime() { return arriveTime; }
    public void setArriveTime(String arriveTime) { this.arriveTime = arriveTime; }
    
    public double getPriceEconomy() { return priceEconomy; }
    public void setPriceEconomy(double priceEconomy) { this.priceEconomy = priceEconomy; }
    
    public double getPriceBusiness() { return priceBusiness; }
    public void setPriceBusiness(double priceBusiness) { this.priceBusiness = priceBusiness; }
    
    public int getSeatsAvailable() { return totalCapacity - takenSeats.size(); }
    public int getTotalCapacity() { return totalCapacity; }
    
    public Set<Integer> getTakenSeats() { return takenSeats; }
    
    // Seat Management
    public boolean bookSeats(Set<Integer> seats) {
        for (Integer seat : seats) {
            if (takenSeats.contains(seat) || seat < 0 || seat >= totalCapacity) {
                return false; // Seat already taken or invalid
            }
        }
        takenSeats.addAll(seats);
        return true;
    }

    public void freeSeats(Set<Integer> seats) {
        takenSeats.removeAll(seats);
    }
    
    public double getPriceForClass(String seatClass) {
        return "Business".equalsIgnoreCase(seatClass) ? priceBusiness : priceEconomy;
    }

    public boolean isSeatTaken(int seatIndex) {
        return takenSeats.contains(seatIndex);
    }
}
