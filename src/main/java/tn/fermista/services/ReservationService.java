package tn.fermista.services;

import tn.fermista.models.Reservation;
import tn.fermista.models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationService extends ServiceReservation {
    
    /**
     * Get all reservations for a specific user
     * @param userId The ID of the user
     * @return List of reservations for the user
     */
    public List<Reservation> getReservationsByUserId(int userId) {
        List<Reservation> userReservations = new ArrayList<>();
        try {
            List<Reservation> allReservations = showAll();
            for (Reservation reservation : allReservations) {
                if (reservation.getUser() != null && reservation.getUser().getId() == userId) {
                    userReservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userReservations;
    }
    
    /**
     * Cancel a reservation by setting its status to "canceled"
     * @param reservationId The ID of the reservation to cancel
     * @return true if successful, false otherwise
     */
    public boolean cancelReservation(int reservationId) {
        try {
            Reservation reservation = getById(reservationId);
            if (reservation != null) {
                reservation.setStatus(Reservation.STATUS_CANCELED);
                update(reservation);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
} 