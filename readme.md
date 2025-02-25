English Learning Bot

Бот для изучения английских слов. Слова размещаются в файле words.txt, в формате: английское слово|перевод|0. Каждая строка соответствует изучаемому слову. При запуске бота новым пользователем, файл words.txt копируется с именем id_чата_пользователя.txt.

Публикация

Для публикации бота на VPS воспользуемся утилитой scp, для запуска – ssh.

Настройка VPS

1. Создать виртуальный сервер (Ubuntu), получить: ip-адрес, пароль для root пользователя
2. Подключиться к серверу по SSH используя команду ssh root@45.132.18.3 и введя пароль J0m0YkAbTy
3. Обновить установленные пакеты командами apt update и apt upgrade
4. Устанавливаем JDK коммандой apt install default-jdk
5. Убедиться что JDK установлена командой java --version 

Публикация и запуск

1. Соберем shadowJar командой ./gradlew shadowJar 
2. Копируем jar на наш VPS переименуя его одновременно в bot.jar: scp build/libs/KotlinTelegramBot-1.0-SNAPSHOT-all.jar root@45.132.18.3:/root/bot.jar 
3. Копируем words.txt на VPS: scp word.txt root@45.132.18.3:/root/word.txt 
4. Подключиться к серверу по SSH используя команду ssh root@45.132.18.3 и введя пароль 
5. Запустить бота в фоне командой nohup java -jar bot.jar 7520156009:AAGuGUZQNnJGNXxgHZgRKu5epgodiqHE9fI & 
6. Проверить работу бота scp build/libs/WordsTelegramBot-1.0-SNAPSHOT-all.jar root@45.132.18.3:/root/bot.jar

доп команды
1. Откройте файл nohup.out, чтобы увидеть, были ли какие-либо ошибки во время запуска бота:
 cat /root/nohup.out
2. Это покажет вам процессы Java, которые запущены на сервере.
   ps aux | grep java