# Assumptions while building this service

* One to one mapping between client and account
* One Entity(Organization) can have multiple accounts
* None of the data models could be deleted but status updated to closed etc.
* Considered single service - in production system obviously following DDD separate service for different domains

## Account 
* Account has below statuses
    * PENDING - when Account creation request is made
    * OPEN - when all checks and approvals are done, account status is changed to this
    * CLOSED - when account close request is made
    * FROZEN - when any fraudulent activity seen on account, this is temporary and can be reverted to Open.
* For scope of this application - just inserted few account rows in db in OPEN status. Pre-populated some tables.

## Wallet
* Considered **only** fiat_currency wallet based on currency, like GBP, INR etc
* Though wallet can have multiple types and business logic will vary for transfer between them
* Assumed cash is deposited to the fiat_currency wallets as initial balance, and thus corresponding entries in posting_command table with status cleared
* For this app purpose used balance in wallet to check it before money transfer (ideally the balance should be retrieved from reconciled balance and Posting_command table)


## Postings (Transfer b/w wallets)
* Transfer between wallets can be done only if they belong to same Account and Account status is OPEN
* Posting has below statuses
  * PENDING - when initial tranfer/posting request is made
  * CLEARED - when transfer is done successfully
  * FAILED - when transfer failed for any reason
* In scope - Client can transfer money b/w fiat_curreny wallets **Note:** for simplicity just allowed transfer without any rate conversion. RateConversion service to be used  
* Client can transfer between wallets within an Account. No inter account transfers allowed
* Transfer from one fiat_currency wallet to multiple fiat_currency wallets can be done
* When posting request is made by client an `CreatePostingEvent` is created and published to RabbitMq. These events are recorded in 
Posting_Event_Store table which can act as event sourcing for read replica.

## General
* For simplicity created Read replica of Posting Command table as Posting Query table while inserting rows to write table only.
* wallet_ledger table contains the reconciled balance. This table is scheduled to be populated every day for each wallet, such that for a single wallet there will be 365 rows in a year.
Materialized views could also be used here using incremental refresh.
* Created exchange and related queues for client1 only to broadcast message when balance is updated in any wallet





