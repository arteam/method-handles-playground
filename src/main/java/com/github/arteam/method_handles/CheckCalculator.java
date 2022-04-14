package com.github.arteam.method_handles;

public class CheckCalculator {

    public static void main(String[] args) {
        Calculator calculator = new Calculator(true, false, true, false);
        long number = 1;
        System.out.println(calculator.calculate3(number));
        System.out.println(calculator.calculate1(number));
        System.out.println(calculator.calculate2(number));
        System.out.println(calculator.calculate4(number));
        System.out.println(calculator.calculate5(number));
        System.out.println(calculator.calculate6(number));
    }

}
