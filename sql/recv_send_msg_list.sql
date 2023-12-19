drop table recv_send_msg_list;
create table recv_send_msg_list(
reqmsg varchar(100),
rspmsg varchar(100),
PRIMARY KEY(reqmsg,rspmsg)
);