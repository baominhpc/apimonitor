from apilog_tmp tmp
INSERT into TABLE apilog PARTITION(time='%time_value%',api)
select tmp.elapsetime, tmp.status, tmp.host, tmp.port, tmp.email, tmp.language, tmp.token, tmp.appid, tmp.appversion, tmp.ip, tmp.useragent,tmp.api