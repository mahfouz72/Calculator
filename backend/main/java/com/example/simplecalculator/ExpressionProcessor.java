package com.example.simplecalculator;
import org.springframework.stereotype.Component;
@Component
public class ExpressionProcessor {
    public String getExpression(String exp) {

        char[] expression = exp.toCharArray();
        StringBuilder processedExpression = new StringBuilder();
        int decimalPoints = 0;

        for (int i = 0; i < expression.length; i++) {

            char c = expression[i];
            if (isOperator(c)) decimalPoints = 0;
            if (c == '.') decimalPoints = 1;

            if (c == '÷') processedExpression.append('/');
            else if (c == '×') processedExpression.append('*');
            else if (c == '√') {
                processedExpression.append("(");
                i++;
                while (i < expression.length && !isOperator(expression[i])) {
                    processedExpression.append(expression[i]);
                    i++;
                }
                processedExpression.append("^0.5)");
                i--;
            } else if (Character.isDigit(c)) {
                if (i < expression.length - 1 && expression[i + 1] == '÷' && decimalPoints == 0)
                    processedExpression.append(Double.parseDouble(String.valueOf(c)));
                else
                    processedExpression.append(c);

            } else processedExpression.append(c);

        }
        return processedExpression.toString();
    }
    boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '÷' || c == '×' || c == '%';
    }
}
