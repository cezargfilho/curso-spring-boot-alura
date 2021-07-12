package br.com.alura.forum.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.alura.forum.modelo.Curso;

@RunWith(SpringRunner.class)
@DataJpaTest // classe para testar repository
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles(value = "test")
public class CursoRepositoryTest {

	/*
	 * Por padrao o modulo de testes do Spring Boot espera um banco de dados em
	 * memoria para testes as configuracoes acima desativam isso e possibilitam a
	 * utilizacao de outro banco, baseado no arquivo application-test
	 */

	@Autowired
	private CursoRepository repository;

	@Autowired
	private TestEntityManager em;

	@Test
	public void deveCarregarUmCursoAoBuscarPeloNome() {
		String nomeCurso = "HTML 5";

		Curso html5 = new Curso();
		html5.setNome(nomeCurso);
		html5.setCategoria("Programacao");
		em.persist(html5);

		Curso curso = repository.findByNome(nomeCurso);
		Assert.assertNotNull(curso);
		Assert.assertEquals(nomeCurso, curso.getNome());
	}

	@Test
	public void naoDeveCarregarUmCursoComNomeNaoCadastrado() {
		String nomeCurso = "JPA";
		Curso curso = repository.findByNome(nomeCurso);
		Assert.assertNull(curso);
	}

}
