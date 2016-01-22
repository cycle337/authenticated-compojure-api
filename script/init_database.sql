-- allow low-level commands on a remote DB
CREATE EXTENSION IF NOT EXISTS dblink;
CREATE EXTENSION IF NOT EXISTS citext;

-- ensure required user has been created
DO
$body$
BEGIN
   IF NOT EXISTS (
      SELECT *
      FROM   pg_catalog.pg_user
      WHERE  usename = 'auth_user') THEN

      CREATE ROLE auth_user LOGIN PASSWORD 'password1';
   END IF;
END
$body$;

-- ensure required databases have been created
DO
$doDev$
BEGIN

IF EXISTS (SELECT 1 FROM pg_database WHERE datname = 'auth') THEN
   RAISE NOTICE 'Database auth already exists';
ELSE
   PERFORM dblink_exec('dbname=' || current_database()  -- current db
                     , 'CREATE DATABASE auth OWNER auth_user');
END IF;

END
$doDev$;

-- \c auth;
-- CREATE EXTENSION citext;


DO
$doTest$
BEGIN

IF EXISTS (SELECT 1 FROM pg_database WHERE datname = 'auth_test') THEN
   RAISE NOTICE 'Database auth_test already exists';
ELSE
   PERFORM dblink_exec('dbname=' || current_database()  -- current db
                     , 'CREATE DATABASE auth_test OWNER auth_user');
END IF;

END
$doTest$;

-- \c auth;
-- CREATE EXTENSION citext;

GRANT ALL PRIVILEGES ON DATABASE auth to auth_user;
GRANT ALL PRIVILEGES ON DATABASE auth_test to auth_user;
