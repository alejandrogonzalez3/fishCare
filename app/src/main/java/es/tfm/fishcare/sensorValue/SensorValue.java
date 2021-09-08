package es.tfm.fishcare.sensorValue;

import java.util.Date;

import es.tfm.fishcare.pojos.Sensor;

public class SensorValue {

    private Long id;
    private Float value;
    private Date date;
    private SensorValueState state;
    private Sensor sensor;

    public SensorValue(Float value, Date date, SensorValueState state) {
        this.value = value;
        this.date = date;
        this.state = state;
    }

    public SensorValue() {
    }

    public SensorValueState getState() {
        return state;
    }

    public void setState(SensorValueState state) {
        this.state = state;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}