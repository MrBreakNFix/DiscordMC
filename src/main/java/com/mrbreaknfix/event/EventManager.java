package com.mrbreaknfix.event;

import com.mrbreaknfix.DiscordMC;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public final class EventManager {

    private final List<Listener> events;

    public EventManager() {
        events = new ArrayList<>();
    }

    public void addListener(@NotNull Object object) throws IllegalArgumentException {
        getEvents(object);
    }

    public void removeListener(@NotNull Object object) {
        events.removeIf(listener -> listener.object == object);
    }

    private void getEvents(@NotNull Object object) {
        Class<?> clazz = object.getClass();
        Arrays.stream(clazz.getDeclaredMethods()).spliterator().forEachRemaining(method -> {
            if (method.isAnnotationPresent(Sub.class)) {
                Class<?>[] prams = method.getParameterTypes();
                if (prams.length != 1) {
                    throw new IllegalArgumentException("Method " + method.getName() + " in class " + clazz.getSimpleName() + " has invalid number of parameters.");
                }
                if (!Event.class.isAssignableFrom(prams[0])) {
                    throw new IllegalArgumentException("Method " + method.getName() + " in class " + clazz.getSimpleName() + " has invalid parameter type.");
                }
                this.events.add(new Listener(method, object, prams[0], getPriority(method)));
                this.events.sort(Comparator.comparing(o -> o.priority));
            }
        });
    }

    public boolean trigger(@NotNull Event event) {
        events.spliterator().forEachRemaining(listener -> {
            // check if event is cancelled
            if(event.isCancelled()) {
                return;
            }

            if(listener.event == event.getClass()){
                try {
                    listener.method.setAccessible(true);
                    listener.method.invoke(listener.object, event);
                } catch (Exception e) {
                    DiscordMC.LOGGER.error("Error invoking event " + event.getClass().getSimpleName() + " for listener " + listener.object.getClass().getSimpleName());
                }
            }
        });
        return event.isCancelled();
    }

    private Priority getPriority(@NotNull Method method) {
        return method.getAnnotation(Sub.class).priority();
    }

}