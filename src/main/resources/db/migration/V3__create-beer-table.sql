drop table if exists beer;

create table beer (
      id varchar(36) primary key,
      beer_name varchar(50),
      beer_style smallint,
      created_date datetime(6),
      price decimal(38, 2),
      quantity_on_hand int,
      upc varchar(255),
      update_date datetime(6),
      version int
) engine = InnoDB;