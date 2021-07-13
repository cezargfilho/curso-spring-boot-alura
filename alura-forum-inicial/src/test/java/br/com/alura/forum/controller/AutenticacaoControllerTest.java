package br.com.alura.forum.controller;

import java.net.URI;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.alura.forum.modelo.Usuario;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
@ActiveProfiles("test")
public class AutenticacaoControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private TestEntityManager em;

	@Test
	public void deveDevolver400CasoDadosDeAutenticacaoInvalidos() throws Exception  {
		URI uri = new URI("/auth");

		String json = "\"email\":\"invalido@email.com\",\"senha\":\"123456\"";
				
		mockMvc.perform(MockMvcRequestBuilders
				.post(uri)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status()
				.is(HttpStatus.BAD_REQUEST.value()));
	}
	
	@Test
	public void deveDevolver200CasoDadosDeAutenticacaoValidos() throws Exception {
		
		Usuario usuario = new Usuario();
		usuario.setNome("Aluno");
		usuario.setEmail("aluno@email.com");
		usuario.setSenha("$2a$10$iv.5VotDUkAjyukxPJ33OeuH0Pi5fpg2OJw8v1CHauFnzc/8mhGEO");

		em.persist(usuario);
		
		URI uri = new URI("/auth");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("email", "aluno@email.com");
		jsonObj.put("senha", "123456");
		String json = jsonObj.toString();
				
		mockMvc.perform(MockMvcRequestBuilders
				.post(uri)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status()
				.isOk());
	}

}
