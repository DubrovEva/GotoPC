create table table1 (id INT, name VARCHAR(100), age INT, salary INT, PRIMARY KEY(id));
create table table2 (id INT, phone_number INT, years_of_experience INT);

INSERT INTO table1 VALUES (1, 'John', 23, 400);
INSERT INTO table1 VALUES(2, 'Mary', 20, 450);
INSERT INTO table1 VALUES(3, 'Max', 29, 1200);
INSERT INTO table1 VALUES (4, 'Felix', 18, 350);
INSERT INTO table1 VALUES(5, 'Groover', 22, 570);
INSERT INTO table1 VALUES(6, 'Ben', 19, 620);

INSERT INTO table2 VALUES(1, 433443, 2), (2, 111111, 1), (3, 123456, 7), (4, 123568, 0), (5, 191919, 1), (6, 121212, 1);

UPDATE table2 SET years_of_experience='9' WHERE
Exists (select * from table1 
       where table1.id = table2.id AND name='Max')

select * from table2
