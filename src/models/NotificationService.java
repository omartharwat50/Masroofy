package models;
public class NotificationService implements INotificationService {
    private static final double THRESHOLD_PERCENTAGE = 80.0;
    
    @Override
    public void triggerNotification(String message) {
        System.out.println("🔔 NOTIFICATION: " + message);
        System.out.println("📧 Email sent: " + message);
        System.out.println("📱 Push notification: " + message);
    }
    
    @Override
    public void showAlert(String message) {
        System.out.println("⚠️ ALERT: " + message);
    }
    
    @Override
    public boolean isThresholdExceeded(double percentage) {
        return percentage >= THRESHOLD_PERCENTAGE;
    }
}