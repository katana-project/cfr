package run.slicer.cfr;

import org.teavm.jso.*;
import org.teavm.jso.core.JSMapLike;
import run.slicer.cfr.impl.ClassFileSourceImpl;
import run.slicer.cfr.impl.OutputSinkFactoryImpl;
import org.benf.cfr.reader.api.CfrDriver;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSPromise;
import org.teavm.jso.core.JSString;
import org.teavm.jso.typedarrays.Uint8Array;

import java.util.List;

public class Main {
    @JSExport
    public static JSPromise<JSMapLike<JSString>> decompile(String[] names, Options options) {
        return decompile0(names, options == null || JSObjects.isUndefined(options) ? JSObjects.create() : options);
    }

    private static JSPromise<JSMapLike<JSString>> decompile0(String[] names, Options options) {
        return JSPromise.callAsync(() -> {
            final var sinkFactory = new OutputSinkFactoryImpl();
            new CfrDriver.Builder()
                    .withClassFileSource(new ClassFileSourceImpl(name0 -> source0(options, name0)))
                    .withOutputSink(sinkFactory)
                    .withOptions(options.rawOptions())
                    .build()
                    .analyse(List.of(names));

            final JSMapLike<JSString> output = JSObjects.create();
            for (final var entry : sinkFactory.output().entrySet()) {
                output.set(entry.getKey(), JSString.valueOf(entry.getValue()));
            }

            // not sure how to get these exceptions across since they can be non-fatal, log them for now
            for (final Throwable throwable : sinkFactory.exceptions()) {
                error(JSExceptions.getJSException(throwable));
            }

            return output;
        });
    }

    private static byte[] source0(Options options, String name) {
        final Uint8Array b = options.source(name).await();
        return b == null || JSObjects.isUndefined(b) ? null : unwrapByteArray(b);
    }

    @JSBody(params = {"data"}, script = "return data;")
    private static native @JSByRef(optional = true) byte[] unwrapByteArray(Uint8Array data);

    @JSBody(params = "message", script = "console.error(message);")
    private static native void error(JSObject message);
}
