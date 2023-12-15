drop table nodeal_tmpfile_list;
create table nodeal_tmpfile_list(
filename varchar(200),
tmpfileNum int,
tmpfilePath varchar(1000),
tmpfileNm varchar(500),
tmpfileStatus int,
PRIMARY KEY(filename,tmpfilePath,tmpfileNm)
);