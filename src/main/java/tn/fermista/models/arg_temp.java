package tn.fermista.models;

import java.time.LocalDateTime;
import java.util.Objects;


public class arg_temp {
    private Long id;
    private String temperature;
    private LocalDateTime timeReceive;

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
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
        arg_temp argTemp = (arg_temp) o;
        return Objects.equals(id, argTemp.id) && Objects.equals(temperature, argTemp.temperature) && Objects.equals(timeReceive, argTemp.timeReceive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, temperature, timeReceive);
    }

    @Override
    public String toString() {
        return "arg_temp{" +
                "id=" + id +
                ", temperature='" + temperature + '\'' +
                ", timeReceive=" + timeReceive +
                '}';
    }
}
