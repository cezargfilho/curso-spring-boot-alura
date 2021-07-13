# Testes
As anotações a seguir fazem com que o *Spring* seja enxergado pelo JUnit (4.13)
```java
@RunWith(SpringRunner.class)
@SpringBootTest
```
> Para a versão 5 do JUnit, atualizações no código podem ser necessárias.

### Testes de Repository:
Para o teste de interfaces *repository* serão utilizadas as seguintes anotações:
```java
@RunWith(SpringRunner.class)
@DataJpaTest // classe para testar repository
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles(value = "test")
```
* `@DataJpaTest`: para testagem apenas de componentes JPA;
* `@AutoConfigureTestDatabase`: Por padrão o *Spring* espera um banco de dados em memória para realizar testes, essa anotação desativa a configuração padrão.
* `@ActiveProfiles`: Possibilita a utilização de *Profiles*, basta criar um arquivo *application-test.properties* com as configurações necessárias de *database* e *JPA*.

### Testes de Controller
Para o teste de classes *Conrtollers* serão utilizadas as seguintes anotações:
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
@ActiveProfiles("test")
```
* `@SpringBootTest`: Vai carregar todo o contexto da aplicação;
* `@AutoConfigureMockMvc`: Habilita a auto-configuração do *MockMvc*;
* `@AutoConfigureTestEntityManager`: Habilita o uso do `TestEntityManager` sem a anotação `@DataJpaTest`;
	* Como o `@DataJpaTest` por padrão é transacional, se faz necessário o uso da `@Transactional`.
> `@WebMvcTest` seria utilizado se a intenção fosse, apenas testar o controller sem necessitar do carregamento de classes que são gerenciadas pelo *Spring*.
> Ao invés do *TestEntityManager* poderia ter sido injetado um *repository* para inserção dos dados necessários.