package tn.fermista.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class arg_agit {
    private int id;
    private String accel_x;
    private String accel_y;
    private String accel_z;
    private LocalDateTime time_receive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccelX() {
        return accel_x;
    }

    public void setAccelX(String accel_x) {
        this.accel_x = accel_x;
    }

    public String getAccelY() {
        return accel_y;
    }

    public void setAccelY(String accel_y) {
        this.accel_y = accel_y;
    }

    public String getAccelZ() {
        return accel_z;
    }

    public void setAccelZ(String accel_z) {
        this.accel_z = accel_z;
    }

    public LocalDateTime getTimeReceive() {
        return time_receive;
    }

    public void setTimeReceive(LocalDateTime time_receive) {
        this.time_receive = time_receive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        arg_agit argAgit = (arg_agit) o;
        return Objects.equals(id, argAgit.id) &&
                Objects.equals(accel_x, argAgit.accel_x) &&
                Objects.equals(accel_y, argAgit.accel_y) &&
                Objects.equals(accel_z, argAgit.accel_z) &&
                Objects.equals(time_receive, argAgit.time_receive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accel_x, accel_y, accel_z, time_receive);
    }

    @Override
    public String toString() {
        return "Arg_Agit{" +
                "id=" + id +
                ", accelX='" + accel_x + '\'' +
                ", accelY='" + accel_y + '\'' +
                ", accelZ='" + accel_z + '\'' +
                ", timeReceive=" + time_receive +
                '}';
    }
}