package com.skybook.util;

import com.skybook.model.*;
import com.skybook.data.*;
import com.skybook.ui.*;
import com.skybook.ui.components.*;
import com.skybook.main.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReceiptGenerator {
    
    public static String generateReceipt(Reservation res) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        sb.append("========================================\n");
        sb.append("          SKYBOOK E-TICKET\n");
        sb.append("========================================\n");
        sb.append("Date Issued: ").append(dtf.format(LocalDateTime.now())).append("\n");
        sb.append("Reservation ID: ").append(res.getReservationId()).append("\n");
        sb.append("Status: ").append(res.getStatus()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Flight: ").append(res.getFlight().getFlightNo()).append("\n");
        sb.append("Route: ").append(res.getFlight().getFrom()).append(" to ").append(res.getFlight().getTo()).append("\n");
        sb.append("Date: ").append(res.getFlight().getDate().toString()).append("\n");
        sb.append("Departure: ").append(res.getFlight().getDepartTime()).append("\n");
        sb.append("Arrival: ").append(res.getFlight().getArriveTime()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Passenger Information\n");
        sb.append("Booked By: ").append(res.getUser().getFullName()).append("\n");
        sb.append("Passengers (").append(res.getPassengers().size()).append("):\n");
        for (String p : res.getPassengers()) {
            sb.append("  - ").append(p).append("\n");
        }
        sb.append("----------------------------------------\n");
        sb.append("Class: ").append(res.getSeatClass()).append("\n");
        sb.append("Seat Numbers: ").append(res.getBookedSeats().toString()).append("\n");
        sb.append("Baggage Add-ons: ").append(res.getBaggageCount()).append("\n");
        sb.append("========================================\n");
        sb.append(String.format("TOTAL PAID: $%.2f\n", res.getTotalPrice()));
        sb.append("========================================\n");
        sb.append("Thank you for flying with SkyBook!\n");
        
        return sb.toString();
    }
}
