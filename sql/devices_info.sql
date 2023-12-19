drop table devices_info;
create table devices_info(
deviceid varchar(100),
devicemac varchar(100),
PRIMARY KEY(deviceid,devicemac)
);