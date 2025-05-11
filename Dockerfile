# Etapa 1: Build do projeto com Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Compila o projeto e gera o JAR (sem rodar os testes)
RUN mvn clean package -DskipTests

# Etapa 2: Imagem enxuta para execução
FROM eclipse-temurin:17-jdk-alpine

# Define diretório de trabalho
WORKDIR /app

# Copia o JAR gerado na etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponha a porta padrão do Spring Boot
EXPOSE 8080

# Comando para executar o app
ENTRYPOINT ["java", "-jar", "app.jar"]
