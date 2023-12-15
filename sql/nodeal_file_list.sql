drop table nodeal_file_list;
create table nodeal_file_list(
filename varchar(200),
md5str varchar(50),
devFromWhere varchar(1000),
filePath varchar(500),
backupFlag int,
chkSign int,
PRIMARY KEY(filename,devFromWhere,filePath)
);