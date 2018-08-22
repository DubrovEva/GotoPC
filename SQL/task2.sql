INSERT INTO table1 VALUES(6, 'Ben', 19, 620);

INSERT INTO table2 VALUES(1, 433443, 2), (2, 111111, 1), (3, 123456, 7), (4, 123568, 0), (5, 191919, 1), (6, 121212, 1);

UPDATE table2 SET years_of_experience='9' WHERE
Exists (select * from table1 
       where table1.id = table2.id AND name='Max')

select * from table2
select * from table1 WHERE age >= '23' AND age < '27' AND salary='1000';
select * from table1 WHERE name = 'Mary' OR name = 'John';
select phone_number from table2 WHERE
Exists (select * from table1 
       where table1.id = table2.id AND name='John')

select * from table1 WHERE
Exists (select * from table2 
       where table1.id = table2.id AND phone_number/100000='1')

select * from table1 WHERE
Exists (select * from table2 
       where table1.id = table2.id AND years_of_experience>'2')
