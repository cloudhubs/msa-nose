package edu.baylor.ecs.rad.dataflow;

import edu.baylor.ecs.rad.instruction.IndexWrapper;
import edu.baylor.ecs.rad.instruction.InstructionInfo;
import edu.baylor.ecs.rad.instruction.StringStackElement;
import edu.baylor.ecs.rad.model.HttpMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * This class detects the argument of a method call from a list of {@link edu.baylor.ecs.rad.instruction.InstructionInfo}.
 * It constructs a list of {@link edu.baylor.ecs.rad.instruction.StringStackElement}.
 *
 * @author Dipta Das
 */

public class LocalVariableScanner {

    public static int findIndexForMethodCall(List<InstructionInfo> instructions, String method) throws DataFlowException {
        int index = 0;
        for (InstructionInfo instruction : instructions) {
            if (instruction.getInstruction() instanceof IndexWrapper) {
                IndexWrapper indexWrapper = (IndexWrapper) instruction.getInstruction();

                if (indexWrapper.getType().equals("Method") && indexWrapper.getValue() instanceof String) {
                    String value = (String) indexWrapper.getValue();
                    if (value.contains(method)) {
                        return index;
                    }
                }
            }
            index++;
        }
        throw new DataFlowException("method not found");
    }

    public static List<StringStackElement> peekParamForMethodCall(List<InstructionInfo> instructions, int index, int numberOfParams) throws DataFlowException {
        // skip through
        for (index = index - 1; index >= 0 && index < instructions.size(); index--) {
            InstructionInfo instruction = instructions.get(index);
            String opcode = instruction.getOpcode();

            if (opcode.contains("ldc") || opcode.contains("aload") || opcode.contains("iload") || opcode.contains("getstatic")) {
                numberOfParams--;
            }

            if (numberOfParams == 1) { // url is always first parameter
                break;
            }
        }

        return peekImmediateStringVariable(instructions, index);
    }

    public static List<StringStackElement> peekImmediateStringVariable(List<InstructionInfo> instructions, int index) throws DataFlowException {
        List<StringStackElement> stringStackElements = new ArrayList<>();

        boolean appendStack = false;
        boolean fieldAccess = false;

        for (index = index - 1; index >= 0 && index < instructions.size(); index--) {
            InstructionInfo instruction = instructions.get(index);

            boolean foundImmediate = false;

            if (getLDC(instruction) != null) {
                foundImmediate = true;

                StringStackElement stringStackElement = new StringStackElement(
                        StringStackElement.StringStackElementType.CONSTANT,
                        getLDC(instruction)
                );
                stringStackElements.add(stringStackElement);

            } else if (getLoadInstructionPointer(instruction) != null) {
                int pointer = getLoadInstructionPointer(instruction);

                if (fieldAccess) { // must have aload_0 before field access
                    fieldAccess = false;
                    if (pointer != 0) {
                        throw new DataFlowException("field access error");
                    }

                } else {
                    foundImmediate = true;

                    try {
                        int storeIndex = peekImmediateStoreIndex(instructions, index, pointer);
                        stringStackElements.addAll(peekImmediateStringVariable(instructions, storeIndex)); // recursive call
                    } catch (DataFlowException e) { // not declared inside the method, possibly method parameter
                        StringStackElement stringStackElement = new StringStackElement(
                                StringStackElement.StringStackElementType.PARAM,
                                Integer.toString(pointer)
                        );
                        stringStackElements.add(stringStackElement);
                    }
                }

            } else if (getFieldAccess(instruction) != null) {
                foundImmediate = true;
                fieldAccess = true;

                StringStackElement stringStackElement = new StringStackElement(
                        StringStackElement.StringStackElementType.FIELD,
                        getFieldAccess(instruction)
                );
                stringStackElements.add(stringStackElement);

            } else if (isStringBuilderAppend(instruction)) {
                appendStack = true;

            } else if (isStringBuilderInit(instruction)) {
                if (appendStack) {
                    return stringStackElements;
                }
            }

            // string constant found and no append operation required
            // otherwise append until StringBuilder Init found
            if (foundImmediate && !appendStack) {
                return stringStackElements;
            }
        }

        throw new DataFlowException("string variable not found");
    }

    private static boolean isStringBuilderAppend(InstructionInfo instruction) {
        if (instruction.getOpcode().equals("invokevirtual") && instruction.getInstruction() instanceof IndexWrapper) {
            IndexWrapper indexWrapper = (IndexWrapper) instruction.getInstruction();

            if (indexWrapper.getType().equals("Method")) {
                String value = (String) indexWrapper.getValue();
                return value.contains("java.lang.StringBuilder.append");
            }
        }
        return false;
    }

    private static boolean isStringBuilderInit(InstructionInfo instruction) {
        if (instruction.getOpcode().equals("invokespecial") && instruction.getInstruction() instanceof IndexWrapper) {
            IndexWrapper indexWrapper = (IndexWrapper) instruction.getInstruction();

            if (indexWrapper.getType().equals("Method")) {
                String value = (String) indexWrapper.getValue();
                return value.contains("java.lang.StringBuilder.<init>");
            }
        }
        return false;
    }

    private static String getFieldAccess(InstructionInfo instruction) {
        if (instruction.getOpcode().equals("getfield") && instruction.getInstruction() instanceof IndexWrapper) {
            IndexWrapper indexWrapper = (IndexWrapper) instruction.getInstruction();

            if (indexWrapper.getType().equals("Field")) {
                String value = (String) indexWrapper.getValue();
                return value.split("\\(")[0];
            }
        }
        return null;
    }

    public static HttpMethod peakHttpMethodForExchange(List<InstructionInfo> instructions, int index) {
        for (index = index - 1; index >= 0 && index < instructions.size(); index--) {
            InstructionInfo instruction = instructions.get(index);

            if (instruction.getOpcode().equals("getstatic") && instruction.getInstruction() instanceof IndexWrapper) {
                IndexWrapper indexWrapper = (IndexWrapper) instruction.getInstruction();

                if (indexWrapper.getType().equals("Field")) {
                    String value = (String) indexWrapper.getValue();

                    if (value.startsWith("org.springframework.http.HttpMethod")) {
                        if (value.contains("POST")) {
                            return HttpMethod.POST;
                        } else if (value.contains("PUT")) {
                            return HttpMethod.PUT;
                        } else if (value.contains("DELETE")) {
                            return HttpMethod.DELETE;
                        } else {
                            return HttpMethod.GET; // default
                        }
                    }
                }
            }
        }

        return HttpMethod.GET; // default
    }

    public static int peekImmediateStoreIndex(List<InstructionInfo> instructions, int index, int pointer) throws DataFlowException {
        for (index = index - 1; index >= 0 && index < instructions.size(); index--) {
            InstructionInfo instruction = instructions.get(index);

            if (isStoreInstructionPointer(instruction, pointer)) {
                return index;
            }
        }

        throw new DataFlowException("store pointer not found");
    }

    private static Integer getLoadInstructionPointer(InstructionInfo instruction) {
        splitOneByteLoadInstruction(instruction); // aload_1 to aload 1
        if (instruction.getOpcode().equals("aload") || instruction.getOpcode().equals("iload")) {
            return (int) instruction.getInstruction();
        }
        return null;
    }

    private static boolean isStoreInstructionPointer(InstructionInfo instruction, int pointer) {
        splitOneByteStoreInstruction(instruction); // astore_1 to astore 1
        if (instruction.getOpcode().equals("astore") || instruction.getOpcode().equals("istore")) {
            return pointer == (int) instruction.getInstruction();
        }
        return false;
    }

    private static void splitOneByteLoadInstruction(InstructionInfo instruction) {
        if (instruction.getOpcode().contains("aload_")) { // aload_1, aload_2
            String value = instruction.getOpcode().replace("aload_", "");
            instruction.setOpcode("aload");
            instruction.setInstruction(Integer.parseInt(value));
        } else if (instruction.getOpcode().contains("iload_")) { // iload_1, iload_2
            String value = instruction.getOpcode().replace("iload_", "");
            instruction.setOpcode("iload");
            instruction.setInstruction(Integer.parseInt(value));
        }
    }

    public static void splitOneByteStoreInstruction(InstructionInfo instruction) {
        if (instruction.getOpcode().contains("astore_")) { // astore_1, astore_2
            String value = instruction.getOpcode().replace("astore_", "");
            instruction.setOpcode("astore");
            instruction.setInstruction(Integer.parseInt(value));
        } else if (instruction.getOpcode().contains("istore_")) { // istore_1, istore_2
            String value = instruction.getOpcode().replace("istore_", "");
            instruction.setOpcode("istore");
            instruction.setInstruction(Integer.parseInt(value));
        }
    }

    public static String getLDC(InstructionInfo instruction) {
        if (instruction.getOpcode().equals("ldc") && instruction.getInstruction() instanceof IndexWrapper) {
            IndexWrapper indexWrapper = (IndexWrapper) instruction.getInstruction();

            if (indexWrapper.getType().equals("int")) {
                return "" + (int) indexWrapper.getValue();
            } else if (indexWrapper.getType().equals("String")) {
                return (String) indexWrapper.getValue();
            }
        }

        return null;
    }
}

