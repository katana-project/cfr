package run.slicer.cfr.teavm;

import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

public class CFRPlugin implements TeaVMPlugin {
    @Override
    public void install(TeaVMHost host) {
        host.add(new MethodStubTransformer());
        host.add(new MethodDelegationTransformer());
    }
}
