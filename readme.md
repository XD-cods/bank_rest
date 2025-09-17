## Запуск приложения:

1. Переименовать `.env example` в `.env`
2. Выполнить команду

```bash
make up
``` 

или если не установлен make:

```bash
docker-compose up -d --build
```

3. Дождаться полного запуска и потом через swagger-ui
   можно будет выполнять запросы. Swagger-ui доступен по пути: `http://localhost:8082/swagger-ui/index.html`