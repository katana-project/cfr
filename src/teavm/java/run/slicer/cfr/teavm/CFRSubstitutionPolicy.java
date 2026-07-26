package run.slicer.cfr.teavm;

import org.teavm.extension.Autoregistered;
import org.teavm.extension.spi.substitution.SimpleSubstitutionPolicy;
import org.teavm.extension.spi.substitution.SubstitutionSink;

import java.util.Set;

@Autoregistered
public class CFRSubstitutionPolicy extends SimpleSubstitutionPolicy {
    private static final Set<String> CLASSLIB_SUBSTITUTIONS = Set.of(
            "java.util.logging.Handler"
    );

    @Override
    public void contribute(SubstitutionSink sink) {
        sink.selectClasses(CLASSLIB_SUBSTITUTIONS::contains)
                .packagePrefix("run.slicer.cfr.teavm.classlib.")
                .simpleNamePrefix("T");
    }
}
