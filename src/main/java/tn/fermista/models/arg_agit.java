package tn.fermista.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class arg_agit {


    private Long id;


    private String accelX;


    private String accelY;


    private String accelZ;


    private LocalDateTime timeReceive;

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccelX() {
        return accelX;
    }

    public void setAccelX(String accelX) {
        this.accelX = accelX;
    }

    public String getAccelY() {
        return accelY;
    }

    public void setAccelY(String accelY) {
        this.accelY = accelY;
    }

    public String getAccelZ() {
        return accelZ;
    }

    public void setAccelZ(String accelZ) {
        this.accelZ = accelZ;
    }

    public LocalDateTime getTimeReceive() {
        return timeReceive;
    }

    public void setTimeReceive(LocalDateTime timeReceive) {
        this.timeReceive = timeReceive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        arg_agit argAgit = (arg_agit) o;
        return Objects.equals(id, argAgit.id) && Objects.equals(accelX, argAgit.accelX) && Objects.equals(accelY, argAgit.accelY) && Objects.equals(accelZ, argAgit.accelZ) && Objects.equals(timeReceive, argAgit.timeReceive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accelX, accelY, accelZ, timeReceive);
    }

    @Override
    public String toString() {
        return "arg_agit{" +
                "id=" + id +
                ", accelX='" + accelX + '\'' +
                ", accelY='" + accelY + '\'' +
                ", accelZ='" + accelZ + '\'' +
                ", timeReceive=" + timeReceive +
                '}';
    }
}
