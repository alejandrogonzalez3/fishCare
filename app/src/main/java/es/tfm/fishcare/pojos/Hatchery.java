package es.tfm.fishcare.pojos;

public class Hatchery {

    private Long id;
    private String name;

    public Hatchery(Long hatcheryId, String name) {
        this.id = hatcheryId;
        this.name = name;
    }

    public Long getHatcheryId() {
        return id;
    }

    public void setHatcheryId(Long hatcheryId) {
        this.id = hatcheryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
