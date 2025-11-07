# s1de's Health Tracker

![thumbnail](./01.png)

# Инструкция

 Как запустить приложение (Linux):

1. Склонируйте репозиторий:
  git clone https://github.com/s1dexdd/healthTracker
2. Скачайте и установите Maven: https://maven.apache.org/install.html.
3. Убедитесь, что у вас установлена версия Java JRE не менее 21.
4. Соберите приложение, находясь в директории склонированного проекта:
  mvn compile
5. Запустите приложение:
  mvn exec:java -D exec.mainClass="com.healthtracker.app.Main"
