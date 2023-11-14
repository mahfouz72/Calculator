package com.example.simplecalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Objects;

@SpringBootApplication
public class SimpleCalculatorApplication {
    public static void main(String[] args)  {
        SpringApplication.run(SimpleCalculatorApplication.class, args);
    }

}
