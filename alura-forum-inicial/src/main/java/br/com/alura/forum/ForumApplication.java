package br.com.alura.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
public class ForumApplication {

	/*
	 * Tudo o que não vem habilitado por padrão, deve ser habilitado na classe
	 * 'Prefix'Application
	 */

	public static void main(String[] args) {
		SpringApplication.run(ForumApplication.class, args);
	}

}
