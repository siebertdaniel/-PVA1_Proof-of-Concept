CREATE DATABASE if not exists "Example_App";
CREATE TABLE if not exists "users"
(
    user_ID BigSerial PRIMARY KEY,
    user_email     varchar(50)  NOT NULL UNIQUE,
    user_pw_hash   varchar(255) NOT NULL
);
