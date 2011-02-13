select 'drop table '||table_name||' cascade constraints;' as sql from user_tables;
