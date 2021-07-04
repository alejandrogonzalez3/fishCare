package es.tfm.fishcare;

public class Sensor {
    private String name;
    private String units;
    private Float maxAllowedValue;
    private Float minAllowedValue;

    public Sensor(String name, String units, Float maxAllowedValue, Float minAllowedValue) {
        this.name = name;
        this.units = units;
        this.maxAllowedValue = maxAllowedValue;
        this.minAllowedValue = minAllowedValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Float getMaxAllowedValue() {
        return maxAllowedValue;
    }

    public void setMaxAllowedValue(Float maxAllowedValue) {
        this.maxAllowedValue = maxAllowedValue;
    }

    public Float getMinAllowedValue() {
        return minAllowedValue;
    }

    public void setMinAllowedValue(Float minAllowedValue) {
        this.minAllowedValue = minAllowedValue;
    }
}
