package es.tfm.fishcare.notifications;

import es.tfm.fishcare.sensorValue.SensorValue;

public class Notification {

    private Long id;
    private SensorValue sensorValue;
    private Boolean isRead;
    private NotificationType notificationType;

    public Notification(SensorValue sensorValue, Boolean isRead, NotificationType notificationType) {
        this.isRead = isRead;
        this.sensorValue = sensorValue;
        this.notificationType = notificationType;
    }

    public Notification(SensorValue sensorValue, NotificationType notificationType) {
        this.sensorValue = sensorValue;
        this.isRead = false;
        this.notificationType = notificationType;
    }

    public SensorValue getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(SensorValue sensorValue) {
        this.sensorValue = sensorValue;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
