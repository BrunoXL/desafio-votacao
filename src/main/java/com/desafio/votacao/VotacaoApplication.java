package com.desafio.votacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class VotacaoApplication {

    static void main(String[] args) {
        SpringApplication.run(VotacaoApplication.class, args);
    }
}
