package com.github.toastshaman.httprouter.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.toastshaman.httprouter.domain.MapRoutingContext;

import java.util.Objects;

public class ContextWrapper extends MapRoutingContext {

    public final Context context;

    public ContextWrapper(Context context) {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ContextWrapper that = (ContextWrapper) o;
        return Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), context);
    }

    @Override
    public String toString() {
        return "ContextWrapper{" +
                "context=" + context +
                '}';
    }
}
