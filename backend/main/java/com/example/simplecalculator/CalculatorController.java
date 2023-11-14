package com.example.simplecalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CalculatorController {
    private final ExpressionProcessor processor;
    private final ExpressionParser parser = new SpelExpressionParser();

    public CalculatorController(ExpressionProcessor processor) {
        this.processor = processor;
    }

    @GetMapping("/evaluate")
    public double evaluate(@RequestParam("exp") String exp) {
        String processedExpression = processor.getExpression(exp);
        return parser.parseExpression(processedExpression).getValue(Double.class);
    }
}
