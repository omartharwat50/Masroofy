package models;
public interface INotificationService {
    void triggerNotification(String message);
    void showAlert(String message);
    boolean isThresholdExceeded(double percentage);
}