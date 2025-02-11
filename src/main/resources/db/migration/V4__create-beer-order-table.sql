drop table if exists beer_order;

create table beer_order (
    id varchar(36) primary key,
    created_date datetime(6),
    customer_ref varchar(255),
    last_modified_date datetime(6),
    version bigint,
    customer_id varchar(36),

    constraint fk_customer foreign key (customer_id) references customer(id)
) engine = InnoDB;
