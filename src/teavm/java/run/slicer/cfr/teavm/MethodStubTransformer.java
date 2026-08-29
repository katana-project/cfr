package run.slicer.cfr.teavm;

import org.teavm.model.*;
import org.teavm.model.instructions.ExitInstruction;
import org.teavm.model.instructions.IntegerConstantInstruction;
import org.teavm.model.instructions.NullConstantInstruction;

import java.util.Arrays;
import java.util.logging.Handler;

public class MethodStubTransformer implements ClassHolderTransformer {
    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        switch (cls.getName()) {
            case "org.benf.cfr.reader.state.ClassFileSourceImpl" -> {
                this.stubWithNullConstant(cls.getMethod(new MethodDescriptor("getContentByFromReflectedClass", String.class, byte[].class)));
                this.stubWithBooleanConstant(cls.getMethod(new MethodDescriptor("CheckJrt", boolean.class)), false);
            }
            case "org.benf.cfr.reader.util.output.LoggerFactory" -> {
                this.stubWithNullConstant(cls.getMethod(new MethodDescriptor("getHandler", Handler.class)));
            }
            // TeaVM 0.15.0 removed the mapClass properties mechanism; the stock TLogger lacks the
            // setters that CFR's LoggerFactory calls, which is a hard link error. Supply them as
            // no-ops and let log output flow to the console as usual.
            case "java.util.logging.Logger" -> {
                this.stubVoidIfMissing(cls, "setUseParentHandlers", ValueType.BOOLEAN);
                this.stubVoidIfMissing(cls, "setLevel", ValueType.object("java.util.logging.Level"));
                this.stubVoidIfMissing(cls, "addHandler", ValueType.object("java.util.logging.Handler"));
            }
        }
    }

    private void stubVoidIfMissing(ClassHolder cls, String name, ValueType... parameterTypes) {
        final ValueType[] signature = Arrays.copyOf(parameterTypes, parameterTypes.length + 1);
        signature[parameterTypes.length] = ValueType.VOID;

        final var descriptor = new MethodDescriptor(name, signature);
        if (cls.getMethod(descriptor) != null) {
            return;
        }

        final MethodHolder method = new MethodHolder(descriptor);
        method.setLevel(AccessLevel.PUBLIC);
        method.setProgram(this.newProgram(parameterTypes.length));

        final BasicBlock block = method.getProgram().createBasicBlock();
        block.add(new ExitInstruction());

        cls.addMethod(method);
    }

    private void stubWithNullConstant(MethodHolder method) {
        final Program program = this.newProgram(method.parameterCount());

        final Variable nullConst = program.createVariable();
        final BasicBlock block = program.createBasicBlock();

        final var nullConstInsn = new NullConstantInstruction();
        nullConstInsn.setReceiver(nullConst);
        block.add(nullConstInsn);

        final var returnInsn = new ExitInstruction();
        returnInsn.setValueToReturn(nullConst);
        block.add(returnInsn);

        method.setProgram(program);
    }

    private void stubWithBooleanConstant(MethodHolder method, boolean value) {
        final Program program = this.newProgram(method.parameterCount());

        final Variable falseConst = program.createVariable();
        final BasicBlock block = program.createBasicBlock();

        final var falseConstInsn = new IntegerConstantInstruction();
        falseConstInsn.setConstant(value ? 1 : 0);
        falseConstInsn.setReceiver(falseConst);
        block.add(falseConstInsn);

        final var returnInsn = new ExitInstruction();
        returnInsn.setValueToReturn(falseConst);
        block.add(returnInsn);

        method.setProgram(program);
    }

    private Program newProgram(int parameterCount) {
        parameterCount++; // type var

        final Program program = new Program();
        for (int i = 0; i < parameterCount; i++) {
            program.createVariable();
        }

        return program;
    }
}
