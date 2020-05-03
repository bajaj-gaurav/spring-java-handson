package com.threading.SPEL;

import java.util.GregorianCalendar;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelMain {

    public static void main(String[] args)
    {
        //String VC = "hello";
        ExpressionParser parser = new SpelExpressionParser();
        ParserContext templateParserContext
                = new TemplateParserContext("{", "}");

        //String message = (String) exp.getValue();

        GregorianCalendar c = new GregorianCalendar();
        c.set(1856, 7, 9);

        // The constructor arguments are name, birthday, and nationality.
        Inventor tesla = new Inventor("Nikola Tesla", c.getTime(), "Serbian");
        //Inventor tesla1 = new Inventor("Nikola Tesla1", c.getTime(), "Serbian");

        //EvaluationContext simpleContext = new StandardEvaluationContext(tesla);
        EvaluationContext simpleContext = new StandardEvaluationContext();
        simpleContext.setVariable("name", "gaurav");


        //parser.parseExpression("VC").setValue(simpleContext, "1234");
        //simpleContext.setVariable("name", "helo");
        Expression exp = parser.parseExpression("Hello { name } World", templateParserContext);


        System.out.println(exp.getValue(simpleContext, String.class));



        //System.out.println((exp.getValue()));
        //System.out.println((exp));


    }
}
