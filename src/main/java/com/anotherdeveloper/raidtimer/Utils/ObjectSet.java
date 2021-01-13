package com.anotherdeveloper.raidtimer.Utils;

import java.beans.ConstructorProperties;

public class ObjectSet {
    private Object a;
    private Object b;

    @ConstructorProperties({"a", "b"})
    public ObjectSet(final Object a, final Object b) {
        this.a = a;
        this.b = b;
    }

    public ObjectSet() {
    }

    public Object getA() {
        return this.a;
    }

    public void setA(final Object a) {
        this.a = a;
    }

    public Object getB() {
        return this.b;
    }

    public void setB(final Object b) {
        this.b = b;
    }
}
