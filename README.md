## 1. Descrição do projeto

API REST para gerenciamento de estoque de lojas, permitindo o controle de produtos, usuários, movimentações de estoque e autenticação segura por diferentes perfis de acesso.

# 2. Objetivo do projeto

Este projeto foi desenvolvido para aplicar conceitos de desenvolvimento backend profissional utilizando Spring Boot, explorando segurança, persistência de dados, cache, mensageria, testes automatizados, Docker e deploy em nuvem.

---

# 3. Tecnologias Utilizadas

- **Java** e **Spring Boot** para o desenvolvimento da API.
- **PostgreSQL** como banco de dados relacional.
- **Redis** para implementar cache na consulta de produtos, utilizando as anotações `@Cacheable` e `@CacheEvict`, com o objetivo de reduzir consultas ao banco de dados e compreender o funcionamento de cache em aplicações Spring.
- **RabbitMQ** para implementar comunicação assíncrona entre serviços. Foi utilizado para enviar notificações quando a quantidade de um produto fica abaixo do estoque mínimo, permitindo estudar os conceitos de **Producer**, **Consumer** e filas de mensagens.
- **Docker** e **Docker Compose** para containerizar toda a aplicação e seus serviços (API, PostgreSQL, Redis e RabbitMQ).
- **Render** para realizar o deploy da aplicação em produção.

---
# 4. Arquitetura da aplicação

A aplicação foi organizada seguindo uma arquitetura em camadas, separando as responsabilidades de cada componente.
### Entity
Representa as entidades do domínio da aplicação e o mapeamento das tabelas do banco de dados através do JPA/Hibernate. Neste projeto, as principais entidades são:
- Loja
- Usuário
- Produto
- Movimentação
### Repository
Camada responsável pelo acesso aos dados. Utiliza o Spring Data JPA para realizar operações de persistência, como consultas, inserções, atualizações e remoções no banco de dados.
### Service
Camada onde está concentrada toda a regra de negócio da aplicação.
Algumas regras implementadas:
- validação dos dados recebidos;
- criação de movimentações de estoque;
- validação da quantidade disponível antes de realizar uma saída;
- atualização automática da quantidade em estoque;
- envio de notificação via RabbitMQ quando o estoque fica abaixo do mínimo permitido;
- regras de acesso entre usuários e lojas.
### Controller
Responsável por expor os endpoints da API REST.
Recebe as requisições HTTP, valida os dados de entrada, chama a camada de serviço e retorna as respostas ao cliente.

### DTO (Data Transfer Object)
Utilizado para padronizar os dados enviados e recebidos pela API.
Os DTOs evitam expor diretamente as entidades do banco de dados e permitem controlar exatamente quais informações fazem parte das requisições e respostas.
### Exception
Contém as exceções personalizadas e o tratamento global de erros da aplicação, retornando respostas padronizadas para situações como:
- recurso não encontrado;
- dados inválidos;
- conflitos;
- operações não permitidas.
### Security
Camada responsável pela autenticação e autorização da aplicação.
Nela estão configurados:
- Spring Security;
- autenticação utilizando JWT;
- criptografia de senhas com BCrypt;
- filtros de autenticação;
- controle de acesso baseado em perfis (roles).
### Config
Contém as configurações gerais da aplicação, como:
- configuração do RabbitMQ;
- configuração do JWT;
- configuração do Redis;
- demais configurações necessárias para inicialização dos serviços.

# 5. Funcionalidades

A API oferece funcionalidades para gerenciamento de estoque, controle de usuários, autenticação e movimentações de produtos, permitindo que cada loja administre seus próprios recursos de forma segura.
## Usuários

- Cadastro de usuários.
- Autenticação utilizando e-mail e senha.
- Controle de acesso baseado em perfis (**ADMIN** e **ESTOQUISTA**).
- Associação obrigatória de cada usuário a uma única loja.
- Restrição de acesso aos recursos da própria loja.
### Permissões
**ADMIN**
- Cadastrar, atualizar, consultar e remover lojas.
- Cadastrar, atualizar, consultar e remover produtos.
- Cadastrar novos usuários.
- Realizar movimentações de estoque.

**ESTOQUISTA**
- Consultar produtos da sua loja.
- Realizar movimentações de estoque.
## Lojas

- Cadastro de lojas.
- Consulta de lojas.
- Atualização de informações da loja.
- Remoção de lojas.
- Associação de usuários e produtos à loja.

Cada loja possui seus próprios usuários e produtos, garantindo o isolamento das informações entre diferentes estabelecimentos.
## Produtos

- Cadastro de produtos.
- Consulta de produtos.
- Atualização de informações dos produtos.
- Exclusão de produtos.
- Controle da quantidade em estoque.
- Definição do estoque mínimo.
- Identificação dos produtos através do SKU.

O SKU deve ser único dentro de cada loja, podendo existir em lojas diferentes.

## Movimentações

O sistema permite registrar movimentações de entrada e saída de produtos.

Para cada movimentação são registrados:

- produto;
- usuário responsável;
- tipo da movimentação (ENTRADA ou SAÍDA);
- quantidade movimentada;
- data e hora da operação.

Durante uma movimentação, a aplicação:

- atualiza automaticamente a quantidade em estoque;
- impede saídas com quantidade superior ao estoque disponível;
- registra o histórico completo das movimentações;
- garante que apenas usuários da mesma loja possam movimentar seus produtos;
- utiliza bloqueio pessimista (**PESSIMISTIC_WRITE**) para evitar inconsistências em movimentações simultâneas;
- publica um evento no RabbitMQ quando o estoque fica abaixo do mínimo configurado.

A autenticação da aplicação foi implementada utilizando **Spring Security** e **JWT (JSON Web Token)**.
O fluxo de autenticação funciona da seguinte forma:
1. O usuário realiza o login utilizando e-mail e senha.
2. A senha informada é comparada com a senha criptografada armazenada no banco utilizando **BCrypt**.
3. Em caso de autenticação bem-sucedida, a aplicação gera um **JWT**.
4. Nas requisições seguintes, o cliente envia esse token no cabeçalho `Authorization`.
5. O Spring Security valida o token e identifica o usuário autenticado.
6. A aplicação verifica as permissões do usuário e sua loja antes de permitir o acesso aos recursos.
As senhas nunca são armazenadas em texto puro, sendo protegidas através de criptografia com **BCrypt**.
Além da autorização por perfil (**ADMIN** e **ESTOQUISTA**), a aplicação também impede que um usuário acesse informações pertencentes a outra loja.

# 7. Banco de Dados
O projeto utiliza **PostgreSQL** como banco de dados relacional, juntamente com **Spring Data JPA** e **Hibernate** para o mapeamento objeto-relacional.
## Entidades
- Loja
- Usuário
- Produto
- Movimentação
- 
### Loja
- nome
### Usuário
- nome
- e-mail
- senha (hash)
- role
### Produto
- nome
- SKU
- quantidade em estoque
- estoque mínimo
### Movimentação
- tipo da movimentação
- quantidade
- data/hora da movimentação

## Relacionamentos
- Uma **Loja** possui vários **Usuários** (1:N).
- Uma **Loja** possui vários **Produtos** (1:N).
- Um **Produto** possui várias **Movimentações** (1:N).
- Um **Usuário** pode realizar várias **Movimentações** (1:N).
- Cada **Produto** pertence a apenas uma **Loja**.
- Cada **Usuário** pertence a apenas uma **Loja**.

O PostgreSQL foi escolhido por ser um banco de dados relacional amplamente utilizado em aplicações web e por oferecer recursos como integridade referencial, transações e alta confiabilidade.
O projeto utiliza **Hibernate/JPA** para o acesso aos dados.
Nesta versão da aplicação não foram utilizadas ferramentas de migração de banco de dados, como Flyway ou Liquibase, sendo adotado o gerenciamento automático do esquema pelo Hibernate (`ddl-auto`).

# 8. Cache com Redis
Para reduzir a quantidade de consultas ao banco de dados e melhorar o desempenho da aplicação, foi implementado um sistema de cache utilizando **Redis**.
O cache foi aplicado na busca de produtos, armazenando temporariamente os resultados mais acessados. Dessa forma, quando uma mesma consulta é realizada novamente, a resposta pode ser obtida diretamente do Redis, sem necessidade de uma nova consulta ao PostgreSQL.
Para a implementação foram utilizadas as anotações do Spring Cache:
- **@Cacheable**: armazena o resultado de uma consulta no Redis.
- **@CacheEvict**: remove os dados armazenados sempre que um produto é atualizado ou removido, evitando que informações desatualizadas permaneçam em cache.

Além da melhoria de desempenho, a utilização do Redis teve como principal objetivo o aprendizado de conceitos como cache distribuído, invalidação de cache e integração do Spring Boot com sistemas de cache.

# 9. Mensageria com RabbitMQ
Foi utilizado **RabbitMQ** para implementar comunicação assíncrona entre componentes da aplicação.
Sempre que uma movimentação de saída faz com que a quantidade de um produto fique abaixo do estoque mínimo definido, a aplicação publica um evento na fila do RabbitMQ.
O evento é enviado por uma classe **Producer**, responsável por publicar a mensagem contendo as informações do produto. Em seguida, uma classe **Consumer** recebe essa mensagem e realiza o processamento necessário, simulando o envio de uma notificação de estoque baixo.
Essa abordagem desacopla a lógica de notificação da regra principal de negócio, tornando a aplicação mais escalável e permitindo que novas funcionalidades possam consumir esse mesmo evento futuramente sem alterar a lógica da movimentação de estoque.
Com essa implementação foram estudados conceitos como:
- Producer;
- Consumer;
- Exchanges;
- Queues;
- Mensageria;
- Comunicação assíncrona;
- Arquitetura orientada a eventos.
# 10. Testes

Para garantir o correto funcionamento das principais regras de negócio da aplicação, foram implementados **testes unitários** utilizando **JUnit 5** e **Mockito**.
Os testes utilizam **Mocks** para simular o comportamento dos repositórios e demais dependências, permitindo validar a lógica da camada de serviço de forma isolada, sem necessidade de acessar o banco de dados.
As principais regras testadas foram:
- realização de entrada de produtos no estoque;
- realização de saída de produtos;
- validação de quantidade insuficiente em estoque;
- tentativa de movimentação de um produto inexistente;
- bloqueio de movimentações entre usuários e produtos de lojas diferentes;
- envio de evento para o RabbitMQ quando o estoque fica abaixo do mínimo.
Durante os testes também foram verificadas chamadas aos repositórios e ao Producer do RabbitMQ utilizando o Mockito, garantindo que as interações entre os componentes ocorressem conforme esperado.
A implementação desses testes teve como objetivo validar as regras críticas da aplicação, aumentar a confiabilidade do sistema e praticar conceitos de testes unitários no ecossistema Spring Boot.
# 11. Docker
A aplicação foi totalmente containerizada utilizando **Docker**, permitindo que todos os serviços sejam executados de forma padronizada em qualquer ambiente.
Para facilitar a execução local, foi utilizado **Docker Compose**, responsável por orquestrar todos os containers necessários para a aplicação.
Os serviços utilizados são:
- **Controle Estoque API**: aplicação Spring Boot.
- **PostgreSQL**: banco de dados relacional.
- **Redis**: armazenamento em cache.
- **RabbitMQ**: broker de mensagens para comunicação assíncrona.
Cada serviço é executado em seu próprio container, comunicando-se através da rede criada automaticamente pelo Docker Compose.
Essa abordagem elimina a necessidade de instalar manualmente cada tecnologia na máquina do desenvolvedor, tornando o ambiente mais simples de configurar e reproduzir.
Para iniciar toda a aplicação basta executar:
```
docker compose up --build
```

# 12. Deploy

O deploy da aplicação foi realizado utilizando a plataforma **Render**, permitindo disponibilizar a API em um ambiente de produção.
Durante o deploy foram configuradas variáveis de ambiente para informações sensíveis, como credenciais do banco de dados, configurações do RabbitMQ, Redis e chave secreta utilizada pelo JWT.
O banco de dados PostgreSQL também foi hospedado na plataforma Render, permitindo que a aplicação permanecesse totalmente disponível na nuvem.
A API pode ser acessada através do endereço:

```
https://controle-estoque-api-mn2e.onrender.com/
```

O processo de deploy é realizado automaticamente sempre que uma nova alteração é enviada para a branch principal do repositório, simplificando a publicação de novas versões da aplicação.

---
# 13. Como executar o projeto

## Pré-requisitos
Antes de executar o projeto é necessário possuir instalado:
- Docker
- Docker Compose
- Git
## Clonar o repositório

```
git clone https://github.com/Arthurecomp/-controle-estoque-api
```

```
cd controle-estoque-api
```

## Configurar as variáveis de ambiente

Criar um arquivo `.env` utilizando como base o arquivo `.env.example`.

## Executar a aplicação

```
docker compose up --build
```

Após a inicialização dos containers, os serviços estarão disponíveis em:

- API: `http://localhost:8080`
- RabbitMQ Management: `http://localhost:15672`
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`

Para utilizar as rotas protegidas da aplicação, basta realizar o login e utilizar o JWT retornado no cabeçalho `Authorization` das requisições.


<img width="733" height="784" alt="Pasted image 20260729151804" src="https://github.com/user-attachments/assets/ca7dd52f-0a5a-4c53-8e06-be88ae57ce45" />


# 2. Objetivo do projeto

Este projeto foi desenvolvido para aplicar conceitos de desenvolvimento backend profissional utilizando Spring Boot, explorando segurança, persistência de dados, cache, mensageria, testes automatizados, Docker e deploy em nuvem.

---

# 3. Tecnologias Utilizadas

- **Java** e **Spring Boot** para o desenvolvimento da API.
- **PostgreSQL** como banco de dados relacional.
- **Redis** para implementar cache na consulta de produtos, utilizando as anotações `@Cacheable` e `@CacheEvict`, com o objetivo de reduzir consultas ao banco de dados e compreender o funcionamento de cache em aplicações Spring.
- **RabbitMQ** para implementar comunicação assíncrona entre serviços. Foi utilizado para enviar notificações quando a quantidade de um produto fica abaixo do estoque mínimo, permitindo estudar os conceitos de **Producer**, **Consumer** e filas de mensagens.
- **Docker** e **Docker Compose** para containerizar toda a aplicação e seus serviços (API, PostgreSQL, Redis e RabbitMQ).
- **Render** para realizar o deploy da aplicação em produção.

---
# 4. Arquitetura da aplicação

A aplicação foi organizada seguindo uma arquitetura em camadas, separando as responsabilidades de cada componente.
### Entity
Representa as entidades do domínio da aplicação e o mapeamento das tabelas do banco de dados através do JPA/Hibernate. Neste projeto, as principais entidades são:
- Loja
- Usuário
- Produto
- Movimentação
### Repository
Camada responsável pelo acesso aos dados. Utiliza o Spring Data JPA para realizar operações de persistência, como consultas, inserções, atualizações e remoções no banco de dados.
### Service
Camada onde está concentrada toda a regra de negócio da aplicação.
Algumas regras implementadas:
- validação dos dados recebidos;
- criação de movimentações de estoque;
- validação da quantidade disponível antes de realizar uma saída;
- atualização automática da quantidade em estoque;
- envio de notificação via RabbitMQ quando o estoque fica abaixo do mínimo permitido;
- regras de acesso entre usuários e lojas.
### Controller
Responsável por expor os endpoints da API REST.
Recebe as requisições HTTP, valida os dados de entrada, chama a camada de serviço e retorna as respostas ao cliente.

### DTO (Data Transfer Object)
Utilizado para padronizar os dados enviados e recebidos pela API.
Os DTOs evitam expor diretamente as entidades do banco de dados e permitem controlar exatamente quais informações fazem parte das requisições e respostas.
### Exception
Contém as exceções personalizadas e o tratamento global de erros da aplicação, retornando respostas padronizadas para situações como:
- recurso não encontrado;
- dados inválidos;
- conflitos;
- operações não permitidas.
### Security
Camada responsável pela autenticação e autorização da aplicação.
Nela estão configurados:
- Spring Security;
- autenticação utilizando JWT;
- criptografia de senhas com BCrypt;
- filtros de autenticação;
- controle de acesso baseado em perfis (roles).
### Config
Contém as configurações gerais da aplicação, como:
- configuração do RabbitMQ;
- configuração do JWT;
- configuração do Redis;
- demais configurações necessárias para inicialização dos serviços.

# 5. Funcionalidades

A API oferece funcionalidades para gerenciamento de estoque, controle de usuários, autenticação e movimentações de produtos, permitindo que cada loja administre seus próprios recursos de forma segura.
## Usuários

- Cadastro de usuários.
- Autenticação utilizando e-mail e senha.
- Controle de acesso baseado em perfis (**ADMIN** e **ESTOQUISTA**).
- Associação obrigatória de cada usuário a uma única loja.
- Restrição de acesso aos recursos da própria loja.
### Permissões
**ADMIN**
- Cadastrar, atualizar, consultar e remover lojas.
- Cadastrar, atualizar, consultar e remover produtos.
- Cadastrar novos usuários.
- Realizar movimentações de estoque.

**ESTOQUISTA**
- Consultar produtos da sua loja.
- Realizar movimentações de estoque.
## Lojas

- Cadastro de lojas.
- Consulta de lojas.
- Atualização de informações da loja.
- Remoção de lojas.
- Associação de usuários e produtos à loja.

Cada loja possui seus próprios usuários e produtos, garantindo o isolamento das informações entre diferentes estabelecimentos.
## Produtos

- Cadastro de produtos.
- Consulta de produtos.
- Atualização de informações dos produtos.
- Exclusão de produtos.
- Controle da quantidade em estoque.
- Definição do estoque mínimo.
- Identificação dos produtos através do SKU.

O SKU deve ser único dentro de cada loja, podendo existir em lojas diferentes.

## Movimentações

O sistema permite registrar movimentações de entrada e saída de produtos.

Para cada movimentação são registrados:

- produto;
- usuário responsável;
- tipo da movimentação (ENTRADA ou SAÍDA);
- quantidade movimentada;
- data e hora da operação.

Durante uma movimentação, a aplicação:

- atualiza automaticamente a quantidade em estoque;
- impede saídas com quantidade superior ao estoque disponível;
- registra o histórico completo das movimentações;
- garante que apenas usuários da mesma loja possam movimentar seus produtos;
- utiliza bloqueio pessimista (**PESSIMISTIC_WRITE**) para evitar inconsistências em movimentações simultâneas;
- publica um evento no RabbitMQ quando o estoque fica abaixo do mínimo configurado.

A autenticação da aplicação foi implementada utilizando **Spring Security** e **JWT (JSON Web Token)**.
O fluxo de autenticação funciona da seguinte forma:
1. O usuário realiza o login utilizando e-mail e senha.
2. A senha informada é comparada com a senha criptografada armazenada no banco utilizando **BCrypt**.
3. Em caso de autenticação bem-sucedida, a aplicação gera um **JWT**.
4. Nas requisições seguintes, o cliente envia esse token no cabeçalho `Authorization`.
5. O Spring Security valida o token e identifica o usuário autenticado.
6. A aplicação verifica as permissões do usuário e sua loja antes de permitir o acesso aos recursos.
As senhas nunca são armazenadas em texto puro, sendo protegidas através de criptografia com **BCrypt**.
Além da autorização por perfil (**ADMIN** e **ESTOQUISTA**), a aplicação também impede que um usuário acesse informações pertencentes a outra loja.

# 7. Banco de Dados
O projeto utiliza **PostgreSQL** como banco de dados relacional, juntamente com **Spring Data JPA** e **Hibernate** para o mapeamento objeto-relacional.
## Entidades
- Loja
- Usuário
- Produto
- Movimentação
- 
### Loja
- nome
### Usuário
- nome
- e-mail
- senha (hash)
- role
### Produto
- nome
- SKU
- quantidade em estoque
- estoque mínimo
### Movimentação
- tipo da movimentação
- quantidade
- data/hora da movimentação

## Relacionamentos
- Uma **Loja** possui vários **Usuários** (1:N).
- Uma **Loja** possui vários **Produtos** (1:N).
- Um **Produto** possui várias **Movimentações** (1:N).
- Um **Usuário** pode realizar várias **Movimentações** (1:N).
- Cada **Produto** pertence a apenas uma **Loja**.
- Cada **Usuário** pertence a apenas uma **Loja**.

O PostgreSQL foi escolhido por ser um banco de dados relacional amplamente utilizado em aplicações web e por oferecer recursos como integridade referencial, transações e alta confiabilidade.
O projeto utiliza **Hibernate/JPA** para o acesso aos dados.
Nesta versão da aplicação não foram utilizadas ferramentas de migração de banco de dados, como Flyway ou Liquibase, sendo adotado o gerenciamento automático do esquema pelo Hibernate (`ddl-auto`).

# 8. Cache com Redis
Para reduzir a quantidade de consultas ao banco de dados e melhorar o desempenho da aplicação, foi implementado um sistema de cache utilizando **Redis**.
O cache foi aplicado na busca de produtos, armazenando temporariamente os resultados mais acessados. Dessa forma, quando uma mesma consulta é realizada novamente, a resposta pode ser obtida diretamente do Redis, sem necessidade de uma nova consulta ao PostgreSQL.
Para a implementação foram utilizadas as anotações do Spring Cache:
- **@Cacheable**: armazena o resultado de uma consulta no Redis.
- **@CacheEvict**: remove os dados armazenados sempre que um produto é atualizado ou removido, evitando que informações desatualizadas permaneçam em cache.

Além da melhoria de desempenho, a utilização do Redis teve como principal objetivo o aprendizado de conceitos como cache distribuído, invalidação de cache e integração do Spring Boot com sistemas de cache.

# 9. Mensageria com RabbitMQ
Foi utilizado **RabbitMQ** para implementar comunicação assíncrona entre componentes da aplicação.
Sempre que uma movimentação de saída faz com que a quantidade de um produto fique abaixo do estoque mínimo definido, a aplicação publica um evento na fila do RabbitMQ.
O evento é enviado por uma classe **Producer**, responsável por publicar a mensagem contendo as informações do produto. Em seguida, uma classe **Consumer** recebe essa mensagem e realiza o processamento necessário, simulando o envio de uma notificação de estoque baixo.
Essa abordagem desacopla a lógica de notificação da regra principal de negócio, tornando a aplicação mais escalável e permitindo que novas funcionalidades possam consumir esse mesmo evento futuramente sem alterar a lógica da movimentação de estoque.
Com essa implementação foram estudados conceitos como:
- Producer;
- Consumer;
- Exchanges;
- Queues;
- Mensageria;
- Comunicação assíncrona;
- Arquitetura orientada a eventos.
# 10. Testes

Para garantir o correto funcionamento das principais regras de negócio da aplicação, foram implementados **testes unitários** utilizando **JUnit 5** e **Mockito**.
Os testes utilizam **Mocks** para simular o comportamento dos repositórios e demais dependências, permitindo validar a lógica da camada de serviço de forma isolada, sem necessidade de acessar o banco de dados.
As principais regras testadas foram:
- realização de entrada de produtos no estoque;
- realização de saída de produtos;
- validação de quantidade insuficiente em estoque;
- tentativa de movimentação de um produto inexistente;
- bloqueio de movimentações entre usuários e produtos de lojas diferentes;
- envio de evento para o RabbitMQ quando o estoque fica abaixo do mínimo.
Durante os testes também foram verificadas chamadas aos repositórios e ao Producer do RabbitMQ utilizando o Mockito, garantindo que as interações entre os componentes ocorressem conforme esperado.
A implementação desses testes teve como objetivo validar as regras críticas da aplicação, aumentar a confiabilidade do sistema e praticar conceitos de testes unitários no ecossistema Spring Boot.
# 11. Docker
A aplicação foi totalmente containerizada utilizando **Docker**, permitindo que todos os serviços sejam executados de forma padronizada em qualquer ambiente.
Para facilitar a execução local, foi utilizado **Docker Compose**, responsável por orquestrar todos os containers necessários para a aplicação.
Os serviços utilizados são:
- **Controle Estoque API**: aplicação Spring Boot.
- **PostgreSQL**: banco de dados relacional.
- **Redis**: armazenamento em cache.
- **RabbitMQ**: broker de mensagens para comunicação assíncrona.
Cada serviço é executado em seu próprio container, comunicando-se através da rede criada automaticamente pelo Docker Compose.
Essa abordagem elimina a necessidade de instalar manualmente cada tecnologia na máquina do desenvolvedor, tornando o ambiente mais simples de configurar e reproduzir.
Para iniciar toda a aplicação basta executar:
```
docker compose up --build
```

# 12. Deploy

O deploy da aplicação foi realizado utilizando a plataforma **Render**, permitindo disponibilizar a API em um ambiente de produção.
Durante o deploy foram configuradas variáveis de ambiente para informações sensíveis, como credenciais do banco de dados, configurações do RabbitMQ, Redis e chave secreta utilizada pelo JWT.
O banco de dados PostgreSQL também foi hospedado na plataforma Render, permitindo que a aplicação permanecesse totalmente disponível na nuvem.
A API pode ser acessada através do endereço:

```
https://controle-estoque-api-mn2e.onrender.com/
```

O processo de deploy é realizado automaticamente sempre que uma nova alteração é enviada para a branch principal do repositório, simplificando a publicação de novas versões da aplicação.

---
# 13. Como executar o projeto

## Pré-requisitos
Antes de executar o projeto é necessário possuir instalado:
- Docker
- Docker Compose
- Git
## Clonar o repositório

```
git clone https://github.com/Arthurecomp/-controle-estoque-api
```

```
cd controle-estoque-api
```

## Configurar as variáveis de ambiente

Criar um arquivo `.env` utilizando como base o arquivo `.env.example`.

## Executar a aplicação

```
docker compose up --build
```

Após a inicialização dos containers, os serviços estarão disponíveis em:

- API: `http://localhost:8080`
- RabbitMQ Management: `http://localhost:15672`
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`

Para utilizar as rotas protegidas da aplicação, basta realizar o login e utilizar o JWT retornado no cabeçalho `Authorization` das requisições.


