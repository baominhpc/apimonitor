create table IF NOT EXISTS apilog(
fulltime string,
elapsetime string,
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