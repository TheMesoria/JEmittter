package pl.softwareforge.jemiter;

import java.util.Map;
import java.util.function.Consumer;

public interface NotificationStrategy
{
    void initialize(Map<Subscription, Consumer<Emitter>> subscriptionMap);
    void terminate();

    void emit(Emitter emitter);
    void subscribe(Map<Subscription, Consumer<Emitter>> subscriptionMap,
                   Subscription subscription,
                   Consumer<Emitter> callback);
    void unsubscribe(Subscription subscription);
}
