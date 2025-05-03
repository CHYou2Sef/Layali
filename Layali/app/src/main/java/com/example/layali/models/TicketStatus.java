package com.example.layali.models;

public enum TicketStatus {
    AVAILABLE("Disponible"),
    RESERVED("Réservé"),
    SOLD("Vendu"),
    CANCELLED("Annulé");

    private final String displayName;

    TicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}