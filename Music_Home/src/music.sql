drop database if exists music;
create database music;
use music;

drop table if exists `user`;
create table user(id int primary key auto_increment,username varchar(20),password varchar(200));


drop table if exists music;
create table music(id int primary key auto_increment,title varchar(50) not null,singer varchar(30),time varchar(15) not null,
                    url varchar(1000) not null,userid int not null);

drop table if exists lovemusic;
create table lovemusic(id int primary key auto_increment,userid int not null,musicid int not null);