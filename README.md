# exchanger_system
Hello, this is a simple currency exchange API developed in `Kotlin`
with `Javalin`. Made with `Ubuntu 20.10`.

All app related dependencies are listed in `pom.xml`.

to run the app, execute `Main.kt`.

## Database
In this system, the chosen database was a Postgres v13.4. I used a `docker` 
to set it up and get it running and `docker compose` to simplify configuration.

With a docker installation and a connection to `docker hub` simply download
the postgres image and them run the `docker-compose up` command in the 
`docker-compose.yml` file level

## API
After running the app, the local port 7000 will be ready to receive requests.

### Conversion endpoint
Send a `POST` request to `localhost:7000/transaction` with the following pattern:
```json
{
  "key": <STRING>,
  "from": <STRING>,
  "to": <STRING>,
  "value": <NUMBER>
}
```
For example, if you want to convert 149,23 BRL to EUR try:
```json
{
    "key": "user1",
    "from": "BRL",
    "to": "EUR",
    "value": 149.23
}
```
The `key` field is used to identify the user that made the request

### Transaction history endpoint

Send a `GET` request to `localhost:7000/transaction/:key` to get the history
of transactions from that user. The parameter `key` is used for identification. 

For example, to get the history of transactions of the user wit key 
`josuke_higashikata` send a `GET` request to:

```
    localhost:7000/transaction/josuke_higashikata
```
