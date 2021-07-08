# Curso 2 Spring Boot - Alura

### 1. Paginação:
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

### 1.1 Paginação (Simplificado):
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

### 2. Cache:
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

### 3. Segurança:
Utilizado o módulo de dependência do próprio Spring.

* No arquivo ***pom.xml*** colocar a dependência do *Spring Security*.
	````xml
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-security</artifactId>
	</dependency>
	````
* Criar uma classe para a lógica de segurança da aplicação (SecurityConfigurations).
* Colocar as anotações `@EnableWebSecurity` e `@Configuration` na classe de configuração.
	* `@EnableWebSecurity` Habilita o módulo de segurança do Spring Boot, desta forma o framework vai visualizar esta classe para necessidades de segurança.
	* `@Configuration` é necessário para o framework carregar as configurações dentro da classe no start-up do projeto.
* Extender a classe `WebSecurityConfigurerAdapter` para utilizar alguns métodos que serão sobrescritos.

### 3.1 Configurando autorização:
Passos:
 * Sobrescrever os três métodos *configure( )* herdados da classe *WebSecurityConfigurerAdapter*.
	 - `configure(AuthenticationManagerBuilder auth)`: Sua responsabilidade é configurações de autenticação, como por exemplo: autenticação de acesso e login.
	 - `configure(HttpSecurity http)`: Tem como responsabilidade configurações de autorização, como por exemplo perfis de acesso e *URL*.
	 - `configure(WebSecurity web)`: A responsabilidade deste método é configurações de recursos estáticos, como arquivos CSS, JavaScript, imagens, entre outros.
* No método  `configure(HttpSecurity http)`, faremos as configurações a seguir:
	* ````java
		(...)
		http.authorizeRequests()
		.antMatchers(HttpMethod.GET, "/topicos").permitAll()
		.antMatchers(HttpMethod.GET, "/topicos/*").permitAll()
		.anyRequest().authenticated();
		````
	- Neste caso temos os dois endpoints liberados publicamente apenas por acesso via *Http Get* e qualquer autro acesso a API deve estar autenticada. Desta forma, torna-se necessário a criação de uma classe de autenticação.
* Na classe Usuário, adicionamos a assinatura de contrato com a interface *UserDetails* e implementamos todas sobrescrições de métodos.
	-  Nos métodos `getPassword()` e `getUsername` retornamos os atributos senha e email, já nos demais métodos sobrescritos modificamos o retorno para ***true***, pois não será feita validação para estes casos.
	- Já no método `getAuthorities()` é necessário a criação de uma classe para representar os perfis de acesso do usuário.
	- Para isso criamos o atributo `private List<Perfil> perfil` e colocamos este atributo como retorno do método `getAuthorities()`.
	- Além disso o relacionamento entre as entidades **Perfil** e **Usuario** deve ser declarado, neste caso como `@ManyToMany(fetch = FetchType.EAGER)`, já que necessitamos da lista de todos os perfil que um Usuário pode ter. 
* A criação da classe perfil deve segir as seguintes intruções:
	- Deve possuir a anotação *@Entity* e implementar a interface *GrantedAuthority*;
	- Possuir os atributos **id** e **nome**;
	- Sobrescrever o método *getAuthority( )*
* Voltando para a classe ***SecurityConfiguration*** no método *configure(HttpSecurity http)* devemos adicionar a chamada dos métodos `.and().formLogin();``.
	-	Assim será utilizado o formulário e *controller* padrão do *Spring Boot* para login.
	-	Acessando a url do projeto, será aberto o formulário de login, mas o erro ***"No AuthenticationProvider found"*** aparecerá, surgindo a necessidade da criação de uma classe de validação do usuário.

### 3.2 Autenticação de usuário:
No método `configure(AuthenticationManagerBuilder auth)`, é utilizado o seguinte trecho de código:
````java
auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
````
Para o entendimento deste trecho, segue os seguintes passos:
* `autenticacaoService` é um atributo de classe:
	* `@Autowired
	private AutenticacaoService autenticacaoService;`
* A classe **AutenticacaoService** necessita ser criada pois ela será a responsável pela autenticação do usuário.
* Na classe **AutenticacaoService** é necessário colocar a anotação ***@Service*** para que o *Spring* consiga gerenciar a classe.
	* Também é necessário implementar a interface **UserDetailsService** e sobrescrever o método `UserDetails loadUserByUsername(String username)`.
	* A lógica do método é simples para este caso, apenas é realizado uma busca utilizando o parâmetro *username* e checado o retorno da busca contém o `Optional<Usuario>`.
* Voltando ao trecho de código, a chamada do método `.passwordEncoder(new  BCryptPasswordEncoder()` é necessário apenas para validar a senha passada do usuário.
> Não é necessário realizar esta verificação de forma explícita pela razão da classe **AutenticacaoService** implementar **UserDetailsService**, e a classe **Usuario** implementar **UserDetails**.