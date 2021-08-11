package pl.softwareforge.jemiter.common;

import pl.softwareforge.jemiter.Emitter;
import pl.softwareforge.jemiter.NotificationStrategy;
import pl.softwareforge.jemiter.Subscription;

import java.util.Map;
import java.util.function.Consumer;

public class SingleThreadNotificationStrategy
        implements NotificationStrategy
{
    private Map<Subscription, Consumer<Emitter>> subscriptionMap;

    @Override public void initialize(Map<Subscription, Consumer<Emitter>> subscriptionMap)
    {
        this.subscriptionMap = subscriptionMap;
    }
    @Override public void terminate()
    {
        subscriptionMap = null;
    }

    @Override public void emit(Emitter emitter)
    {
        for (var subscription : subscriptionMap.entrySet())
        {
            subscription.getValue().accept(emitter);
        }
    }
    @Override public void subscribe(Map<Subscription, Consumer<Emitter>> subscriptionMap,
                                    Subscription subscription,
                                    Consumer<Emitter> callback)
    {
        subscriptionMap.put(subscription, callback);
    }
    @Override public void unsubscribe(Subscription subscription)
    {
        subscriptionMap.remove(subscription);
    }
}
