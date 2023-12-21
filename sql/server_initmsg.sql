drop table server_initmsg;
create table server_initmsg(
    system_code varchar(1000),
    init_code varchar(1000),
    init_value varchar(1000),
    init_attribute varchar(1000),
    PRIMARY KEY(system_code,init_code,init_value)
);