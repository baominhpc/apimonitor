create table IF NOT EXISTS apilog_pro(
fulltime string,
elapsetime INT,
status string,
host string,
port string,
email string,
language string,
token string,
appid string,
appversion string,
ip string,
useragent string
)
PARTITIONED BY(time string, api string)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ',';

---

create table IF NOT EXISTS api_report(
api string,
count int,
max int,
min int,
avg int
)
PARTITIONED BY(time string)


from apilog_pro insert into table api_report PARTITION(time='20120725')select api, count(*), max(elapsetime), min(elapsetime),avg(elapsetime) where time='20120725' group by api;

alter table api_report drop partition(time='20120727s');