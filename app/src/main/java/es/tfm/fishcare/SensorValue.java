package es.tfm.fishcare;

import java.util.Date;

public class SensorValue {

    private String title;
    private String value;
    private Date date;
    private SensorValueState state;
    private String units;

    public SensorValue(String title, String value, Date date, SensorValueState state, String units) {
        this.title = title;
        this.value = value;
        this.date = date;
        this.state = state;
        this.units = units;
    }

    public SensorValueState getState() {
        return state;
    }

    public void setState(SensorValueState state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}