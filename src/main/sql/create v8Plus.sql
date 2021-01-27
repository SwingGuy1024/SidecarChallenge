# Use this script for MySQL versions 7 or lower.

# This script creates the database and a user.
create database if not exists pizza;
use pizza;

# Only works with MySQL version 8 or higher:
create user if not exists pizza identified WITH mysql_native_password by 'pizza';

grant all on pizza.* to pizza;
