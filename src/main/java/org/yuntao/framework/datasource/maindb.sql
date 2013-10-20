create table shard_database (
   id bigint(20) not null auto_increment,
   name varchar(100) not null,
   url varchar(200) not null,
   username varchar(100) not null,
   password varchar(200) not null,
   PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table shard_relation (
   uid bigint(20) not null,
   shard_id bigint(20) not null, 
   PRIMARY KEY (subscriber_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
