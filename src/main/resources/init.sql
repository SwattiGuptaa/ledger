-- Create a database
CREATE DATABASE ledgerDb;

-- Switch to the new database
\c ledgerDb;

-- Create a user and grant privileges
-- CREATE USER guest WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE ledgerDb TO guest;