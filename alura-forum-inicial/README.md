
# Curso 2 Spring Boot - Alura

### Paginação:
Recebendo parâmetros via *PATH*.

   ````java
@GetMapping
	public Page<TopicoDto> lista(
			@RequestParam(required = false) String nomeCurso, @RequestParam int pagina,
			@RequestParam int qtd, @RequestParam String ordenacao, @RequestParam Direction direcao) {
		
		//Direction pode vir como parametro
		Pageable paginacao = PageRequest.of(pagina, qtd, direcao, ordenacao);

		if (nomeCurso == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			return TopicoDto.converter(topicos);
		} else {
			Page<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
			return TopicoDto.converter(topicos);
		}
	}
````
> *Adaptar o código para retornar ***Page*** ao invés de ***List***

### Paginação (Simplificado):
Via módulo **SpringDataWebSupport**.

 * Habilita módulo na classe Main do projeto (ForumApplication).
	 * Utilizando a anotação ***@EnableSpringDataWebSupport***.
 * Modifica o parâmetro do método listar:
    * `@RequestParam(required = false) String nomeCurso, Pageable paginacao`
 * Para chamar o endpoint utilizar os parâmetros:
	* `?page=0&size=10&sort=id,desc`
	* Por convenção o *SpringBoot* entende os parâmetros em inglês.
	* O parâmetro *sort* pode aparecer múltiplas vezes:
		* `?page=0&size=10&sort=id,desc&sort=dataCriacao,asc`
	* Possibilitando a ordenação de múltiplos campos.
 * Possibilidade de ordenação padrão acrescentando a anotação ***@PageableDefault***.
	* `@PageableDefault(sort = "id",direction = Direction.DESC) Pageable paginacao`

### Cache:
Provedor padrão do Spring de cache utilizado não é recomendado para aplicações em produção. Um exemplo de provedor para cache é o [Redis](https://redis.io/)

* Adicionar dependência no **pom.xml**:
````xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-cache</artifactId>
</dependency>
````
* Habilitar o módulo de cache na classe *Main* do projeto:  `@EnableCaching`
* Inserir a anotação `@Cacheable` nos endpoints que desejar, se faz necessário adicionar o parâmetro `value=""`, ele serve como um *ID* para o *Spring* identificar o *cache*.

> Se parar por aí, um problema fica evidenciado, quando houver uma
> inserção, atualização ou exclusão o cache não é atualizado, desta
> forma é necessário mais algumas modificações.

 * Realizar a limpeza do cache sempre que houver uma dessas operações:
	 * Adicionar a anotação `@CacheEvict` nos endpoints que manipulam o objeto:
		 * `@CacheEvict(value = "listaDeTopicos", allEntries = true)`
		 * O parâmetro "value" é a ID do cache declarado anteriormente;
		 * O parâmetro "allEntries" é a opção para apagar todos os registros daquele cache;

> A utilização de cache geralmente ocorre em tabelas que sofrem pouca atualização. Não é uma boa prática aplicar cache em tabelas que os dados sofrem constante modificação.
