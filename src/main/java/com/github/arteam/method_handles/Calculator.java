package com.github.arteam.method_handles;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.LongUnaryOperator;

public class Calculator {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final MethodType METHOD_TYPE = MethodType.methodType(
            long.class,
            long.class);

    private final boolean addition;
    private final boolean subtraction;
    private final boolean multiplication;
    private final boolean division;
    private final LongUnaryOperator dynamicLambda;
    private final LongUnaryOperator manualLambda;
    private final LongUnaryOperator fsm;

    public Calculator(boolean addition, boolean subtraction, boolean multiplication, boolean division) {
        this.addition = addition;
        this.subtraction = subtraction;
        this.multiplication = multiplication;
        this.division = division;
        this.dynamicLambda = dynamicLambda(addition, subtraction, multiplication, division);
        this.manualLambda = manualLambda(addition, subtraction, multiplication, division);
        this.fsm = fsm(addition, subtraction, multiplication, division);
    }

    long calculate1(long number) {
        long result = number;
        if (addition) {
            result += 5;
        }
        if (subtraction) {
            result -= 2;
        }
        if (multiplication) {
            result *= 2;
        }
        if (division) {
            result /= 4;
        }
        return result;
    }

    long calculate2(long number) {
        long result = number;
        result += 5;
        result *= 2;
        return result;
    }

    long calculate3(long number) {
        return dynamicLambda.applyAsLong(number);
    }

    long calculate4(long number) {
        return manualLambda.applyAsLong(number);
    }

    long calculate5(long number) {
        return fsm.applyAsLong(number);
    }

    private static long param(long l) {
        return l;
    }

    private static long add(long l) {
        return l + 5;
    }

    private static long sub(long l) {
        return l - 2;
    }

    private static long multiply(long l) {
        return l * 2;
    }

    private static long div(long l) {
        return l / 4;
    }

    private static LongUnaryOperator dynamicLambda(boolean addition, boolean subtraction, boolean multiplication, boolean division) {
        try {
            MethodHandle chain = LOOKUP.findStatic(Calculator.class, "param", METHOD_TYPE);
            if (addition) {
                chain = MethodHandles.filterArguments(LOOKUP.findStatic(Calculator.class, "add", METHOD_TYPE), 0, chain);
            }
            if (subtraction) {
                chain = MethodHandles.filterArguments(LOOKUP.findStatic(Calculator.class, "sub", METHOD_TYPE), 0, chain);
            }
            if (multiplication) {
                chain = MethodHandles.filterArguments(LOOKUP.findStatic(Calculator.class, "multiply", METHOD_TYPE), 0, chain);
            }
            if (division) {
                chain = MethodHandles.filterArguments(LOOKUP.findStatic(Calculator.class, "div", METHOD_TYPE), 0, chain);
            }
            // MethodHandles are slow if they are not static, generate a lambda to get it inlined?
            return (LongUnaryOperator) LambdaMetafactory.metafactory(
                    LOOKUP,
                    "applyAsLong",
                    MethodType.methodType(LongUnaryOperator.class, MethodHandle.class),
                    METHOD_TYPE,
                    MethodHandles.exactInvoker(METHOD_TYPE),
                    METHOD_TYPE
            ).getTarget().invokeExact(chain);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static LongUnaryOperator manualLambda(boolean addition, boolean subtraction, boolean multiplication, boolean division) {
        LongUnaryOperator result = l -> l;
        if (addition) {
            result = result.andThen(Calculator::add);
        }
        if (subtraction) {
            result = result.andThen(Calculator::sub);
        }
        if (multiplication) {
            result = result.andThen(Calculator::multiply);
        }
        if (division) {
            result = result.andThen(Calculator::div);
        }
        return result;
    }

    private static LongUnaryOperator fsm(boolean addition, boolean subtraction, boolean multiplication, boolean division) {
        if (addition && subtraction && multiplication && division) return number -> div(multiply(sub(add(number))));
        else if (addition && subtraction && multiplication) return number -> multiply(sub(add(number)));
        else if (addition && subtraction && division) return number -> div(sub(add(number)));
        else if (addition && subtraction) return number -> sub(add(number));
        else if (addition && multiplication && division) return number -> div(multiply(add(number)));
        else if (addition && multiplication) return number -> multiply(add(number));
        else if (addition && division) return number -> div(add(number));
        else if (addition) return Calculator::add;
        else if (subtraction && multiplication && division) return number -> div(multiply(sub(number)));
        else if (subtraction && multiplication) return number -> multiply(sub(number));
        else if (subtraction && division) return number -> div(sub(number));
        else if (subtraction) return Calculator::sub;
        else if (multiplication && division) return number -> div(multiply(number));
        else if (multiplication) return Calculator::multiply;
        else if (division) return Calculator::div;
        else return number -> number;
    }
}
