package pl.softwareforge.jemiter;

import java.util.function.Consumer;

public interface Emitter
{
    Subscription subscribe(Consumer<Emitter> callback);
    void unsubscribe(Subscription subscription);
}
