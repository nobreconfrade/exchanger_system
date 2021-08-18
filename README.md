# exchanger_system
Hello, this is a simple currency exchange convert API developed in `Kotlin`
with `Javalin`. Made with `Ubuntu 20.10`.


## Database
In this system, the chosen database was a Postgres v13.4. I used a `docker`
to set it up and get it running.

With a docker installation and a connection to `docker hub` simply 
paste the following code in your `bash`:
```
    docker run --name exchange_postgres -e POSTGRES_PASSWORD=<your password> -d postgres
```