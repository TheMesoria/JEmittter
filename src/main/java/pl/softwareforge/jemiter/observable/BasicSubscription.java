package pl.softwareforge.jemiter.observable;

import pl.softwareforge.jemiter.Emitter;
import pl.softwareforge.jemiter.Subscription;

public class BasicSubscription
        implements Subscription
{
    private final Emitter source;

    BasicSubscription(Emitter emitter)
    {
        source = emitter;
    }

    @Override public void unsubscribe()
    {
        source.unsubscribe(this);
    }
}
