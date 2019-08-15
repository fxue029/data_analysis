#!/usr/bin/python
# -*- coding: utf-8 -*-
# 
from flask import Flask, jsonify
import logging  
import logging.handlers  
import codecs 
# import redis
import sys
# reload(sys)
# sys.setdefaultencoding('utf-8')
import importlib
importlib.reload(sys)

###How to run#######
###控制台执行下面语句
###set FLASK_APP=sztInfoPlay.py
###flask run


#日志
LOG_FILE = './sztInfoPlay.log'
handler = logging.handlers.RotatingFileHandler(LOG_FILE, maxBytes = 1024*10240, backupCount = 100) # 实例化handler   
fmt = '%(asctime)s - %(filename)s:%(lineno)s - %(name)s - %(message)s'

formatter = logging.Formatter(fmt)   # 实例化formatter  
handler.setFormatter(formatter)      # 为handler添加formatter  
  
logger = logging.getLogger('szt')    # 获取名为szt的logger  
logger.addHandler(handler)           # 为logger添加handler  
logger.setLevel(logging.DEBUG) 

#redis
# r = redis.StrictRedis(host='localhost', port=6379, db=1)
# if r.get("all#requestTime") == None:
# 	r.set("all#requestTime", 1)

app = Flask(__name__)
#app.config['JSON_AS_ASCII'] = False

#第三张图，key站点为起点或终点的线路分布
#19	大剧院	1-2	26	(罗湖,411,463,664,1)|(黄贝岭,352,320,733,5)|(老街,340,215,344,3)|....
#20	留仙洞	5	27	(深圳北,695,621,1217,4-5)|(五和,595,639,1552,5)|(灵芝,549,557,714,5)|...
file3 = codecs.open('./data/pic3.txt', mode='r', encoding='utf-8')
globalData3 = {}
while 1:
	line = file3.readline()
#	logger.debug("%s", line)
	if not line:
		break
	arr1 = line.split("\t")
	if len(arr1) != 5:
		break
	infoArr = arr1[4].replace("(","").replace(")","").split("|")
	stationLineNo = arr1[2].split("-") # 1-2表示什么？1号线和2号线的换乘车站么？
	arrInfo = [] #从这个站点上车的人都到了哪些站点下车
	diffLineStation = [] #从这个站点上车，下车的时候换地铁线路了，这些换线后的下车站点是哪些
	for info in infoArr:
		infoD = info.split(",")
		if len(infoD) != 5:
			break
		tmpInfo = {'stationName': infoD[0], 'beginPer1W': int(infoD[1]),\
				   'endPer1W' : int(infoD[2]), 'distance' : int(infoD[3]), \
				   'lineNo' : infoD[4]} #beginPer1W和endPer1W、distance表示什么？
		arrInfo.append(tmpInfo)
		#判断是否为同一条线路，不是的话则标记出来
		lineNoArr = infoD[4].split("-")
		isCommLine = False
		for no in lineNoArr:
			if no in stationLineNo:
				isCommLine = True
				break
		if isCommLine == False:
			diffLineStation.append(infoD[0])
	globalData3[arr1[1]] = {"lineNo": arr1[2], "stationNumber": len(arrInfo),"stations": arrInfo, "diffLineStation": diffLineStation}
	# if r.get(arr1[1]+"#"+"requestTime") == None:
	# 	r.set(arr1[1]+"#"+"requestTime", 1)
#	logger.debug("key=%s, value=%s", arr1[1], globalData3[arr1[1]] )

logger.debug("globalData3.len=%d", len(globalData3))

# for key,value in globalData3.items():
# 	logger.debug("key=%s, value=%s", key, value )
# 	print "key = " + key
# 	print "value = " + value["lineNo"]

#第5和第6张图，某站点为起点和终点的人流指数
#2	黄贝岭	(华强北,10452,0.504209720627631)|(大剧院,9008,0.4287300177619893)|,,,	(布吉,12217,0.4548579847753131)|(下水径,8299,0.5348837209302325)|...
file5and6 = codecs.open('./data/pic5and6.txt', mode='r', encoding='utf-8')
globalData5and6 = {}
while 1:
	line = file5and6.readline()
	if not line:
		break
	arr1 = line.split("\t")
	if len(arr1) != 4:
		break
	inArr = arr1[2].replace("(","").replace(")","").split("|")
	inArrData = []
	for info in inArr:
		infoD = info.split(",")
		if len(infoD) != 3:
			break
		tmpInfo = { 'stationName':infoD[0],'renliuzhishu':int(infoD[1]),'returnPer1W':int(float(infoD[2])*10000) } #returnPer1W表示什么？
		inArrData.append(tmpInfo)
	outArr = arr1[3].replace("(","").replace(")","").split("|")
	outArrData = []
	for info in outArr:
		infoD = info.split(",")
		if len(infoD) != 3:
			break
		tmpInfo = { 'stationName':infoD[0],'renliuzhishu':int(infoD[1]),'returnPer1W':int(float(infoD[2])*10000) }
		outArrData.append(tmpInfo)
	globalData5and6[arr1[1]] = { "inStation": inArrData, "outStation": outArrData }

logger.debug("globalData5and6.len=%d", len(globalData5and6))

#第7张图，记录某站点工作日24h进出站人流指数
#1	永湖	(37,20)|(38,326)|(39,618)|(40,1215)|...	(38,9)|(39,14)|(40,363)|(41,603)|(42,825)|...
file7 = codecs.open('./data/pic7.txt', mode='r', encoding='utf-8')
globalData7 = {}
while 1:
	line = file7.readline()
	if not line:
		break
	arr1 = line.split("\t")
	if len(arr1) != 4:
		break
	#(37,20)|(38,326)|(39,618)|(40,1215)|....
	inArr = arr1[2].replace("(","").replace(")","").split("|")
	inArrData = []
	for info in inArr:
		infoD = info.split(",")
		if len(infoD) != 2:
			break
		tmpInfo = { 'timeIndex':int(infoD[0]),'renliuzhishu':int(infoD[1]) }
		inArrData.append(tmpInfo)
	#(38,9)|(39,14)|(40,363)|(41,603)|(42,825)|...
	outArr = arr1[3].replace("(","").replace(")","").split("|")
	outArrData = []
	for info in outArr:
		infoD = info.split(",")
		if len(infoD) != 2:
			break
		tmpInfo = { 'timeIndex':int(infoD[0]),'renliuzhishu':int(infoD[1]) }
		outArrData.append(tmpInfo)
	globalData7[arr1[1]] = { "inStation": inArrData, "outStation": outArrData }

logger.debug("globalData7.len=%d", len(globalData7))

@app.route('/sztInfoPlay/<key>')  
def get_run(key): 
	result = {}
	# r.incr("all#requestTime")
	# r.incr(key+"#"+"requestTime", 1)
	# r.save
	logger.debug("key = %s", key)
#	logger.debug("key = %s", key.encode('utf-8'))
	result['key'] = key
	# result['key_requestTime'] = r.get(key+"#"+"requestTime")
	# result['key_requestTime_total'] = r.get("all#requestTime")

	if key in globalData3 and key in globalData5and6 and key in globalData7:
		result['lineNo'] = globalData3[key]['lineNo']
		result['pic3'] = globalData3[key]
		result["pic5"] = globalData5and6[key]["inStation"]
		result["pic6"] = globalData5and6[key]["outStation"]
		result["pic7_in"] = globalData7[key]["inStation"]
		result["pic7_out"] = globalData7[key]["outStation"]
		result['code'] = 0
		result['msg'] = 'succ'
		result['result'] = True
	else:
		result['code'] = -1
		result['msg'] = 'key is wrong'
		result['result'] = False
	return jsonify(result)

if __name__=='__main__':
	app.run(host='0.0.0.0',port=8003,debug=True,threaded=True)     