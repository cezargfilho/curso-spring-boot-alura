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
