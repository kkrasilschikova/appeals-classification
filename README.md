# appeals-classification
Automatic classification of citizens' appeals.  
The task from https://datamasters.ru/ was the following: based on text (appeal from citizen) define category, theme and executor of the service.

Naive Bayes classifier was used to create 3 models (category, theme and executor) using Spark MLlib, TF-IDF feature vectorization method was used to define the importance of terms.

### How to run
1) `git clone https://github.com/kkrasilschikova/appeals-classification.git`
2) start
    - start using console: wth installed sbt 1.2.8 go to project folder and type `sbt run`
    - alternatively start main method in IDE
3) after successful start you should see in logs  
> REST interface bound to /0:0:0:0:0:0:0:0:8080
4) to check the service use get request that returns project name   
`curl http://127.0.0.1:8080/name`

### Examples
Using curl (or any other client like Postman) send request to server  

`curl -H 'Content-Type:multipart/form-data' -F text="на улице не горит фонарь" http://127.0.0.1:8080/text`  

*"на улице не горит фонарь" = "a lantern is not working on the street"*  

The first request may take 30 seconds, the rest no more than 2 seconds.  
Response example
> {"category":"Городская территория","executor":"Управление благоустройства города","theme":"Не работает уличное освещение"}  

*"Городская территория" = "Territory of the city", "Управление благоустройства города" = "Department of city development", "Не работает уличное освещение" = "Street lighting doesn't work"*