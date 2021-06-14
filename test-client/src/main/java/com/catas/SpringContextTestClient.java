package com.catas;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.catas"})
public class SpringContextTestClient {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringContextTestClient.class);
        MyController myController = (MyController) context.getBean("myController");

        myController.helloTest();

        myController.addTest();
    }
}
