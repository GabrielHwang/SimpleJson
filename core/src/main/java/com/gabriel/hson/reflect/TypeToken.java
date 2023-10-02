package com.gabriel.hson.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeToken<T> {
    private final Class<? super T> rawType;
    private final Type type;
    private final int hashcode;
    @SuppressWarnings("unchecked")

    public TypeToken(Class<? super T> rawType, Type type, int hashcode) {
        this.rawType = rawType;
        this.type = type;
        this.hashcode = hashcode;
    }

    private Type getTypeTokenArgument() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            ParameterizedType parameterized = (ParameterizedType) superclass;
            if (parameterized.getRawType() == TypeToken.class) {
            }
        }
        return null;
    }
}
