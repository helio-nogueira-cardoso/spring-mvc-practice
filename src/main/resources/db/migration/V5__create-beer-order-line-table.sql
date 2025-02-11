drop table if exists beer_order_line;

create table beer_order_line (
    id varchar(36) primary key,
    beer_id varchar(36),
    created_date datetime(6),
    last_modified_date datetime(6),
    order_quantity int,
    quantity_allocated int,
    version bigint,
    beer_order_id varchar(36),

    constraint fk_beer foreign key (beer_id) references beer(id)
) engine = InnoDB;
