package com.mrbreaknfix.event;

import java.lang.reflect.Method;

public class Listener {
    public final Method method;
    public final Object object;
    public final Class<?> event;
    public final Priority priority;

    public Listener(Method method, Object object, Class<?> event, Priority priority){
        this.method = method;
        this.object = object;
        this.event = event;
        this.priority = priority;
    }
}