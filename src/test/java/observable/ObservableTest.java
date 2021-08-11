package observable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.softwareforge.jemiter.Emitter;
import pl.softwareforge.jemiter.NotificationStrategy;
import pl.softwareforge.jemiter.Subscription;
import pl.softwareforge.jemiter.observable.Observable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class ObservableTest
{
    private SimpleObservableImplementation sut;

    private static class SimpleObservableImplementation
            extends Observable
    {
        public void simulateEmitSignal()
        {
            emit();
        }
    }

    @BeforeEach
    void initializeTestEnv()
    {
        sut = new SimpleObservableImplementation();
    }

    @AfterEach
    void terminateTestEnv()
    {
        sut = null;
    }

    @Test
    void basicSubscriptionTest()
    {
        var called = new AtomicBoolean(false);
        sut.subscribe(e -> called.set(true));
        sut.simulateEmitSignal();

        assertTrue(called.get());
    }

    @Test
    void subscribeAndUnsubscribeTest()
    {
        var called = new AtomicBoolean(false);

        var subscription = sut.subscribe(e -> called.set(true));
        sut.unsubscribe(subscription);

        sut.simulateEmitSignal();

        assertFalse(called.get());
    }

    @Test
    void subscribeCallAndUnsubscribeCallTest()
    {
        var called = new AtomicInteger(0);
        var subscription = sut.subscribe(e -> called.incrementAndGet());
        sut.simulateEmitSignal();
        sut.unsubscribe(subscription);
        sut.simulateEmitSignal();

        assertEquals(1, called.get(), "There should be no callback after unsubscription");
    }

    @ParameterizedTest
    @ValueSource(ints = {100, 2000, 30000})
    void quickCallbackLoopTest(int callbackCount)
    {
        var observable = new SimpleObservableImplementation();

        var called = new AtomicInteger(0);
        var subscription = observable.subscribe(e -> called.incrementAndGet());

        for (var i = 0; i < callbackCount; i++)
        {
            observable.simulateEmitSignal();
        }

        assertEquals(callbackCount, called.get());
    }

    private static class CustomNotificationStrategy
            implements NotificationStrategy
    {
        private final int mapElementCount;
        private final int expectedSubscriptionCallCount;
        private final int expectedUnsubscribeCallCount;
        private int addedElementCount = 0;
        private int removedElementCount = 0;


        CustomNotificationStrategy()
        {
            this.mapElementCount = 0;
            this.expectedSubscriptionCallCount = 0;
            this.expectedUnsubscribeCallCount = 0;
        }
        CustomNotificationStrategy(int mapElementCount,
                                   int expectedSubscriptionCallCount,
                                   int expectedUnsubscribeCallCount)
        {
            this.mapElementCount = mapElementCount;
            this.expectedSubscriptionCallCount = expectedSubscriptionCallCount;
            this.expectedUnsubscribeCallCount = expectedUnsubscribeCallCount;
        }
        @Override public void initialize(Map<Subscription, Consumer<Emitter>> subscriptionMap)
        {
            assertEquals(mapElementCount, subscriptionMap.size());
        }
        @Override public void terminate()
        {
            assertEquals( expectedSubscriptionCallCount, addedElementCount );
        }
        @Override public void emit(Emitter emitter)
        {

        }
        @Override public void subscribe(Map<Subscription, Consumer<Emitter>> subscriptionMap,
                                        Subscription subscription,
                                        Consumer<Emitter> callback)
        {
            addedElementCount++;
        }
        @Override public void unsubscribe(Subscription subscription)
        {
            removedElementCount++;
        }
    }

    @Test
    void testCustomNotificationStrategyChange()
    {
        var strategy = new CustomNotificationStrategy(0,4,3);

        sut.setNotificationStrategy(strategy);
        var subscription1 = sut.subscribe(e -> {});
        var subscription2 = sut.subscribe(e -> {});
        var subscription3 = sut.subscribe(e -> {});
        var subscription4 = sut.subscribe(e -> {});

        sut.unsubscribe(subscription2);
        sut.unsubscribe(subscription3);
        sut.unsubscribe(subscription4);

        sut.setNotificationStrategy(new CustomNotificationStrategy());
    }
}
