package com.desafio.votacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
public class VotacaoApplication {

    private static final Logger log = LoggerFactory.getLogger(VotacaoApplication.class);

    static void main(String[] args) {
        SpringApplication.run(VotacaoApplication.class, args);
    }
}
