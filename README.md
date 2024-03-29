# Демопроект Vespa

---

Установка Vespa: 

1. Запустить контейнер с конфигурационным сервером
    ```shell 
    docker-compose up -d --build --force-recreate
    ```

2. Собрать проект
    ```shell
    mvn -DskipTests=true clean install
    ```

3. Развернуть конфиг на сервер
    ```shell
    vespa deploy ./vespa-config --wait 600
    ```