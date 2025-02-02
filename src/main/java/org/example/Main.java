package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Book;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
//注释
@ServletComponentScan
@ComponentScan("org.example.mapper")
@ComponentScan("org.example.config")
@EnableTransactionManagement
@SpringBootApplication
@EnableCaching
public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        SpringApplication.run(Main.class,args);
        log.info("success");
      //  Book book=new Book();
    }
}