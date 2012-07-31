LOAD DATA LOCAL INPATH '%file%' INTO TABLE apilog PARTITION(time='%time%', api='%api%')

