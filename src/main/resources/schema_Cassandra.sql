CREATE  KEYSPACE IF NOT EXISTS test_keyspace
   WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : '1' };

USE test_keyspace;

CREATE TABLE IF NOT EXISTS test_keyspace.employees(
   id int PRIMARY KEY,
   firstName text,
   lastname text
   );


INSERT INTO employees (id, firstName, lastName) VALUES (1, 'NomeTEste', 'SobreomeTeste');