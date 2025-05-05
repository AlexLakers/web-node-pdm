# web-node-pdm
This app is one node of product data management(PDM) system which describes the specification and the details in it. The access rules depend on user role.

## How can you use it?
This app can be used as a base for product data managment system(PDM). You need also to use it as a Hierarchical Data Storage with authentication and authorization functions.
As a client of this app you can use web intarface with html pages for registartion a new user, for log in,for specifications or details data managment.
Possible, you will want to use data from this service in your app then you need to pay attention to REST-API for getting data formatted .JSON between different system.
 
## More datails 
This app has ***MVC*** part:
The main page of this app allows you go to any page using header buttons.
![alt text](https://github.com/AlexLakers/ParserJsonCsvToXml/blob/master/WinFormsCsvJsonXml/App_Data/pictures/5.png?raw=true)

For example, if you are not authenticated user then you can click on 'Registartion' page and finish registartions operation.
Similary, you can go to login page,logout, profile of current user and so on.
![alt text](https://github.com/AlexLakers/ParserJsonCsvToXml/blob/master/WinFormsCsvJsonXml/App_Data/pictures/1.png?raw=true)

After authentication you can add new parent node(Specification) to database using special form.
You can also find available specifications using specific params for search.For example, you can 
set field and direction of sort. What about pagination then it is available for you too.
of course you can update or delete your specifications. Similary , you can manage set details in parent specification.
![alt text](https://github.com/AlexLakers/ParserJsonCsvToXml/blob/master/WinFormsCsvJsonXml/App_Data/pictures/6.png?raw=true)

As I sad early you can save new specification or detail:
![alt text](https://github.com/AlexLakers/ParserJsonCsvToXml/blob/master/WinFormsCsvJsonXml/App_Data/pictures/11.png?raw=true)

Also if you have role 'ADMIN' then you have access to ***Admin page***(***see navigation menu):
![alt text](https://github.com/AlexLakers/ParserJsonCsvToXml/blob/master/WinFormsCsvJsonXml/App_Data/pictures/10.png?raw=true)


You can also update,create and delete user for this app:
![alt text](https://github.com/AlexLakers/ParserJsonCsvToXml/blob/master/WinFormsCsvJsonXml/App_Data/pictures/9.png?raw=true)

As I sad early you can use ***REST-API*** too. More information about REST endpoints 'src/main/resources/api-docs.json'.

```json
/api/v1/specifications": {
      "get": {
        "tags": [
          "rest-specification-controller"
        ],
        "operationId": "findAll_1",
        "parameters": [
          {
            "name": "specificationSearchDto",
            "in": "query",
            "required": true,
            "schema": {
              "$ref": "#/components/schemas/SpecificationSearchDto"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/PageSpecificationDto"
                }
              }
            }
          }
        }
      }
.....
```

![alt text](https://github.com/AlexLakers/ParserJsonCsvToXml/blob/master/WinFormsCsvJsonXml/App_Data/pictures/32.png?raw=true)



## Security
I used spring-security to configure authentication and authorization process.
I override defaults security configurations bean-'SecurityFilterChain' two times with different order.
I did it to realize different authentication methods. I mean the fallowing methods:
for REST I used the httpBasic authorization,for web intarface I apply my own login form with 'UsernamePasswordAuthFilter' and 'OAUTH2.0AuthenticationFilter' at your choice.
I want to say several words about my using ***OAUTH2.0*** in this app. As a provider I used Google AuthServer, therefore client of this app
should be registrated in Google. When the Client will visit login form and click corresponding button  after it client will be redirected to GoogleAuthServer
for finished authentication procedure and getting a specific authorization code. After it client totransfer this code to AuthClient-App,then app to confirm this code
and get access token,***JWT-token***.Unfortunately, we need to build our own realization OidcUser by roles from database(not Google).I did it with wrapper, I ***merge 'UserDetails' and 'OidcUser'.***

## About database and migration schema
As a database I used PostgreSQL v17.
The main tables:detail,specifcation,users.
Additional tables for autditing: detail_aud,specification_aud,users_aud,log_Mesage.
As a migration framework I chosed ***'Liquibase'.***

![alt text](https://github.com/AlexLakers/ParserJsonCsvToXml/blob/master/WinFormsCsvJsonXml/App_Data/pictures/4.png?raw=true)

## About the the main components 
  > - I used Spring-Boot v3.4.1(Data-Jpa,Jdbc,Web,Security,Validation,Tymeleaf,Test,Envers....)
  > - As a template engine I used Tymeleaf.
  > - As a database is choosed 'PostgreSQL' V 17.0
  > - As a building system I used 'Maven' V 3.6.3.
  > - As a system of conteiners I used 'Docker' 27.3.1 and wirted my own Dockerfile and compose.yaml.
  > - java version 22.
  > - Mapstruct framework for mapping.
  
## Logging system and error vizualization
I used default logging system in Spring -Logback with different level.
Additionally,some errors to save in the table 'log_message ' of database.
It occurs in separate transaction with propagation level 'Required_new'.
***Error in the database:***
![alt text](https://github.com/AlexLakers/ParserJsonCsvToXml/blob/master/WinFormsCsvJsonXml/App_Data/pictures/13.png?raw=true)

***Error in view:***
![alt text](https://github.com/AlexLakers/ParserJsonCsvToXml/blob/master/WinFormsCsvJsonXml/App_Data/pictures/12.png?raw=true)

## Auditing system
I created corresponding tables with postfix '_aud' for auditing system.
I used Spring-Data-Envers
![alt text](https://github.com/AlexLakers/ParserJsonCsvToXml/blob/master/WinFormsCsvJsonXml/App_Data/pictures/14.png?raw=true)

## How to build this app?
Firstly, you need to build JAR-file using maven:
```
./mvnw clean package
```
You can use several methods fot it:
> - You can build ***Dockerfile manually*** using docker build command.The Dockerfile is located in the fallowing path: '/web-node-pdm/docker/Dockerfile'
You should create your own network and volume to improve container intercation:
```
docker network create backend-net
docker volume create db_data
```
You can build image by above Docker file using command:
```
docker build --build-arg BASE_DIR: /web-node-pdm --build-arg BUILD_PATH: target/*.jar .
docker tag imageId web-node-pdm:1.0
```
Then, you need to create and run two conteiners. ***The database-container:***
```
docker run --name database --mount type=volume,source=db_data,target=/var/lib/postgresql/data --network=backend-net -p 5435:5432 -e POSTGRES_USER=adminnodepdm -e POSTGRES_DB=node_pdm_storage -e POSTGRES_PASSWORD=AdminNodePdmPassword -d postgres:17
```
The ***app-container***(by Dockerfile-own image by alpine):
```
docker run --name app -p 8085:8080 -e SPRING_DATASOURCE_USERNAME=adminnodepdm -e  SPRING_DATASOURCE_PASSWORD=AdminNodePdmPassword -e SPRING_PROFILES_ACTIVE=dev -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/node_pdm_storage-d --network=backend-net web-node-pdm:1.0
```
> - Also you can use ***docker compose*** which allow to build Dockerfile,create container and run it. The compose.yaml is located in the fallowing path: '/web-node-pdm/docker/Dockerfile'
```
docker compose up -d
```

## About test
For tests I used 'TestContextFramework' in 'Spring-boot-starter-test' because it allows to write integration test out of the box.
Also I wrote lots of unit tests because I believe in conseption which represents pyramid of testing.
What about interaction with real database I used 'TestConteiners' because it allows me to test repository level with the same database as a production
