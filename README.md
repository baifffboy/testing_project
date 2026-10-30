# testing_project

## Запуск
mvn clean package # с тестами
mvn clean package -DskipTests # без тестов, если нужно быстро собрать JAR

docker compose up -d --build

## Ручное тестирование

http://localhost:8080/swagger-ui.html