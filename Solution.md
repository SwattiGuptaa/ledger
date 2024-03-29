
## Current Solution

![](./src/main/resources/static/Current.png)

![](./src/main/resources/static/AsyncProces.png)

With `ledger.write.enabled` property we can set to run the application in READ or WRITE mode, allowing the ability to scale the system independently.

Scheduler used to populate data in wallet_ledger(READ) table. Reconciliation done daily for each wallet.

* DB - Postgres
* MessageBroker - RabbitMq

### API documentation
* http://localhost:8080/swagger-ui/index.html

## Improved Solution
![](./src/main/resources/static/Improved.png)

## To run application
* uses docker compose feature of spring boot, so docker containers for dba nd mq are started automatically
* Update property `ledger.write.enabled` to ensure REad/write mode
* update server.port for running mutiple instances of application
* tables will be created at server start up (just for dev)
* [init.sql](./src/main/resources/init.sql) for postgres db setup
* [data.sql](./src/main/resources/data.sql) this script to be executed after db container is up and running

## Improvements 

* Transaction Outbox pattern to avoid dual writes
* Auditing and monitoring
* Authentication and authorization
* Load balancing and more
* Optimistic concurrency -> before actually transferring balance
* Read part of application can use NOSql db and can be populated asynchronously using Mq
* POST API can be updated to send back URL to check progress.


