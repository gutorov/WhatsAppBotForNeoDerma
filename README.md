запуск ngrok
ngrok http 8080


тонкая настройка модели

загрузить файл с настройками
пост запрос(проверить в контроллере верный путь к актуальному файлу)
http://localhost:8080/llm/upload_training_file
-> получить файл айди

пост запрос на начало обуча
http://localhost:8080/llm/create_fine_tuning_job?fileId=file-S8IFzfBLWa24QFUYw0SDGnmu
-> запомнить из ответа айди обуча "id": "ftjob-sz9dhtwfWD7OIXMYMdbIzkaA"

через пару минут - првоерить статус обуча
гет-запрос
http://localhost:8080/llm/get_status_fine_tuning_job?fineTuningJobId=ftjob-sz9dhtwfWD7OIXMYMdbIzkaA

ЗАПОМНИТЬ "fine_tuned_model": -> исползовать его для создание модели через LC4J

