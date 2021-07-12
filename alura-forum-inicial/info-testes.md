# Testes
As anotações a seguir fazem com que o *Spring* seja enxergado pelo JUnit (4.13)
````java
@RunWith(SpringRunner.class)
@SpringBootTest
````
> Para a versão 5 do JUnit, atualizações no código podem ser necessárias.

### Testes de Repository:
Para o teste de interfaces *repository* serão utilizadas as seguintes anotações:
````java
@RunWith(SpringRunner.class)
@DataJpaTest // classe para testar repository
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles(value = "test")
````
* `@DataJpaTest`: para testagem apenas de componentes JPA;
* `@AutoConfigureTestDatabase`: Por padrão o *Spring* espera um banco de dados em memória para realizar testes, essa anotação desativa a configuração padrão.
* `@ActiveProfiles`: Possibilita a utilização de *Profiles*, basta criar um arquivo *application-test.properties* com as configurações necessárias de *database* e *JPA*.

### Testes de Controller