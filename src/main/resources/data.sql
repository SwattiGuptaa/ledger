insert into Organization(id, org_name) values (1, 'Org-1');

insert into Client  (id, client_name) values (1, 'Client-1');
insert into Client  (id, client_name) values (2, 'Client-2');
insert into Client  (id, client_name) values (3, 'Client-3');

insert into Account (id, org_id, client_id, account_name, account_status) values(1, 1, 1, 'Account-Client-1', 'OPEN');
insert into Account (id, org_id, client_id, account_name, account_status) values(2, 1, 2, 'Account-Client-2', 'OPEN');

insert into Wallet (id, account_id, balance, asset_type, wallet_name)values(1, 1, 100, 'FIAT_CURRENCY_GBP','fiat_curreny_GBP');
insert into Wallet (id, account_id, balance, asset_type, wallet_name)values(2, 1, 100, 'FIAT_CURRENCY_USD', 'fiat_curreny_USD');
insert into Wallet (id, account_id, balance, asset_type, wallet_name)values(3, 2, 100, 'FIAT_CURRENCY_GBP','fiat_curreny_GBP');
insert into Wallet (id, account_id, balance, asset_type, wallet_name)values(4, 2, 100, 'FIAT_CURRENCY_USD', 'fiat_curreny_USD');

insert into Posting_Command (correlation_id, wallet_id, amount, description, created_Timestamp, posting_status) values
('Cash', 1, 100.00, 'Cash deposited', CURRENT_TIMESTAMP, 'CLEARED' );
insert into Posting_Command (correlation_id, wallet_id, amount, description, created_Timestamp, posting_status) values
('Cash', 2, 100.00, 'Cash deposited', CURRENT_TIMESTAMP, 'CLEARED' );

insert into Posting_Query (correlation_id, wallet_id, amount, description, created_Timestamp, posting_status) values
('Cash', 1, 100.00, 'Cash deposited', CURRENT_TIMESTAMP, 'CLEARED' );
insert into Posting_Query (correlation_id, wallet_id, amount, description, created_Timestamp, posting_status) values
('Cash', 2, 100.00, 'Cash deposited', CURRENT_TIMESTAMP, 'CLEARED' );

------------------------
--Function to update table for reads
---------------------------

CREATE OR REPLACE FUNCTION daily_wallet_ledger_reconciliation() RETURNS VOID AS
$$
DECLARE
    yesterday_date DATE := CURRENT_DATE - INTERVAL '1 day';
BEGIN

    -- Populate the materialized view with reconciled data
    insert
	into
	wallet_ledger (wallet_id,
	wallet_name,
	balance,
	reconcileDate )
    select
	w.wallet_name,
	w.wallet_id,
	sum(pc.amount) as balance,
	yesterday_date AS reconcileDate
from
	posting_command pc
left join wallet w on
	pc.wallet_id = w.id
where
	DATE(pc.created_timestamp) = yesterday_date
	and pc.posting_status = 'CLEARED'
group by
	pc.wallet_id;

END;
$$
LANGUAGE plpgsql;