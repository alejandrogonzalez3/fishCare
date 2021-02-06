package es.tfm.fishcare.notifications;

public class Notification {

    private String title;
    private String body;
    private Integer imgId;


    public Notification(String title, String body, Integer imgId) {
        this.title = title;
        this.body = body;
        this.imgId = imgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getImgId() {
        return imgId;
    }

    public void setImgId(Integer imgId) {
        this.imgId = imgId;
    }
}
