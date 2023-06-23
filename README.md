# BE Test Project

### How to run
- Download `links.csv`, `sentences.csv`, `sentences_with_audio.csv` and store them in `resources/files` folder
- `mvn clean install`
- `docker-compose build`
- `docker-compose up -d`

### How to test
- init `en_v_translation.csv` file firstly: <br/>
``curl --location --request POST 'localhost:8080/api/translations/prepare-translation-file'``
- save data from file to db: <br/>
  ``curl --location --request POST 'localhost:8080/api/translations/save-data'``
- Test API paging: <br/>
  ``curl --location 'localhost:8080/api/translations?page=1&size=10'``

