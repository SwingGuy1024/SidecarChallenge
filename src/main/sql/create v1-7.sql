# Use this script for MySQL versions 7 or lower.

create database if not exists pizza;
use pizza;

# Only works with MySQL version 7 or lower:
create user pizza identified by 'pizza';

grant all on pizza.* to pizza;
