create table table1 (id INT, name VARCHAR(100), age INT, salary INT);

INSERT INTO table1 VALUES (1, 'John', 23, 400);
INSERT INTO table1 VALUES(2, 'Mary', 20, 450);
INSERT INTO table1 VALUES(3, 'Max', 29, 1200);
INSERT INTO table1 VALUES (4, 'Felix', 18, 350);
INSERT INTO table1 VALUES(5, 'Groover', 22, 570);
INSERT INTO table1 VALUES(6, 'Ben', 19, 620);



select id, name, age, salary from table1 WHERE id='6';
select id, name, age, salary from table1 WHERE salary>'500';
INSERT INTO table1 VALUES(7, 'Denis', 23, 600);
DELETE FROM table1 WHERE name = 'Max';
select * from table1;
select id, name, age, salary from table1 WHERE salary>'500' AND salary<'1000';
