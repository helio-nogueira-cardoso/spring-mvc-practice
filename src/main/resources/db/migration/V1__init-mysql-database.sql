
    drop table if exists customer;

    create table customer (
        version integer not null,
        created_date datetime(6) not null,
        last_modified_date datetime(6) not null,
        id varchar(36) not null,
        name varchar(50) not null,
        primary key (id)
    ) engine=InnoDB;
