package run.slicer.cfr.impl;

import org.benf.cfr.reader.api.OutputSinkFactory;
import org.benf.cfr.reader.api.SinkReturns;

import java.util.*;

public final class OutputSinkFactoryImpl implements OutputSinkFactory {
    private final Map<String, String> output = new HashMap<>();
    private final List<Throwable> exceptions = new ArrayList<>();

    @Override
    public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> collection) {
        return List.of(SinkClass.EXCEPTION_MESSAGE, SinkClass.DECOMPILED);
    }

    @Override
    public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
        return switch (sinkType) {
            case JAVA -> (s -> {
                if (!(s instanceof SinkReturns.Decompiled dec)) {
                    throw new RuntimeException("Unexpected sink input");
                }

                var className = dec.getClassName();
                if (!dec.getPackageName().isEmpty()) {
                    className = dec.getPackageName().replace('.', '/') + "/" + className;
                }

                output.put(className, dec.getJava());
            });
            case EXCEPTION -> (s -> exceptions.add(
                    s instanceof SinkReturns.ExceptionMessage ex
                            ? new RuntimeException(ex.getThrownException())
                            : new RuntimeException(s.toString())
            ));
            default -> (s -> {
            });
        };
    }

    public Map<String, String> output() {
        return this.output;
    }

    public List<Throwable> exceptions() {
        return this.exceptions;
    }
}
