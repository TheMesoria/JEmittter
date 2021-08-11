package pl.softwareforge.jemiter.observable;

import pl.softwareforge.jemiter.Emitter;
import pl.softwareforge.jemiter.NotificationStrategy;
import pl.softwareforge.jemiter.Subscription;
import pl.softwareforge.jemiter.common.MultiThreadNotificationStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Observable
        implements Emitter
{
    private final Map<Subscription, Consumer<Emitter>> subscriptionMap = new HashMap<>();
    private NotificationStrategy notificationStrategy;

    protected Observable() {setNotificationStrategy(new MultiThreadNotificationStrategy());}

    protected void emit()
    {
        notificationStrategy.emit(this);
    }

    @Override public Subscription subscribe(Consumer<Emitter> callback)
    {
        var subscription = new BasicSubscription(this);
        notificationStrategy.subscribe(subscriptionMap, subscription, callback);

        return subscription;
    }

    public void unsubscribe(Subscription subscription)
    {
        notificationStrategy.unsubscribe(subscription);
    }

    public void setNotificationStrategy(NotificationStrategy strategy)
    {
        if (notificationStrategy != null) {this.notificationStrategy.terminate();}

        strategy.initialize(subscriptionMap);
        this.notificationStrategy = strategy;
        this.notificationStrategy.initialize(subscriptionMap);
    }
}
