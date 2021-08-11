package pl.softwareforge.jemiter.common;

import pl.softwareforge.jemiter.Emitter;
import pl.softwareforge.jemiter.NotificationStrategy;
import pl.softwareforge.jemiter.Subscription;

import java.util.Map;
import java.util.function.Consumer;

public class MultiThreadNotificationStrategy
        implements NotificationStrategy
{
    private Map<Subscription, Consumer<Emitter>> subscriptionMap;

    @Override public void initialize(Map<Subscription, Consumer<Emitter>> subscriptionMap)
    {
        if (this.subscriptionMap != null) return;
        this.subscriptionMap = subscriptionMap;

    }
    @Override public void terminate()
    {
        synchronized (this.subscriptionMap)
        {
            subscriptionMap = null;
        }
    }

    @Override public void emit(Emitter emitter)
    {
        synchronized (this.subscriptionMap)
        {
            for (var subscription : subscriptionMap.entrySet())
            {
                subscription.getValue().accept(emitter);
            }
        }
    }
    @Override public void subscribe(Map<Subscription, Consumer<Emitter>> subscriptionMap,
                                    Subscription subscription,
                                    Consumer<Emitter> callback)
    {
        synchronized (this.subscriptionMap)
        {
            subscriptionMap.put(subscription, callback);
        }
    }
    @Override public void unsubscribe(Subscription subscription)
    {
        synchronized (this.subscriptionMap)
        {
            subscriptionMap.remove(subscription);
        }
    }
}
