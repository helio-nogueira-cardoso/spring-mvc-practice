drop table if exists category;
drop table if exists beer_category;

create table category (
    id varchar(36) not null primary key,
    description varchar(50),
    created_date timestamp,
    last_modified_date datetime(6) default null,
    version bigint default null
) engine = InnoDB;

create table beer_category (
    beer_id varchar(36) not null,
    category_id varchar(36) not null,

    constraint pk_category primary key (beer_id, category_id),
    constraint fk_beer_join foreign key (beer_id) references beer(id),
    constraint fk_category_join foreign key (category_id) references category(id)
) engine = InnoDB;