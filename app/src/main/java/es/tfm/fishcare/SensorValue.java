package es.tfm.fishcare;

import java.util.Date;

public class SensorValue {

    private String value;
    private Date date;
    private SensorValueState state;
    private Sensor sensor;

    public SensorValue(String value, Date date, SensorValueState state) {
        this.value = value;
        this.date = date;
        this.state = state;
    }

    public SensorValue() {}

    public SensorValueState getState() {
        return state;
    }

    public void setState(SensorValueState state) {
        this.state = state;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}