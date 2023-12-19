drop table user_device_cfg;
create table user_device_cfg(
deviceid varchar(100),
userid int,
PRIMARY KEY(deviceid,userid)
);