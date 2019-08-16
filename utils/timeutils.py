# -*- coding: utf-8 -*-
"""
Created on Thu Aug 15 19:09:40 2019

@author: fxue
"""

"""
datetime转1970年秒数
"""
def get_seconds_from1970(datetime_str):
    import time,datetime
    datetime_format = "%Y-%m-%d %H:%M:%S"
    time1=datetime.datetime.strptime(datetime_str, datetime_format)
    return time.mktime(time1.timetuple())

'''
1970年秒数转datetime
'''
def get_datetime_from_seconds(sec):
    import time
    datetime_format = "%Y-%m-%d %H:%M:%S"
    timeArray = time.localtime(sec)#1970秒数
    #print(timeArray)
    return time.strftime(datetime_format, timeArray)


print(get_seconds_from1970('2016-05-06 05:11:40'))
print(get_datetime_from_seconds(1462482700))
