package program.user;

public interface SubscribeObserver {
    /**
     * adds new notification
     * @param name name of notification
     * @param description description of notification
     */
    void addNotification(String name, String description);
}
