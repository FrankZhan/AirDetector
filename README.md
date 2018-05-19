# Nami_AirDetector
空气检测仪APP: nami 空探
包括android APP源码以及nodeMCU源码
<br>nodeMCU 位nodeMCU的源码
系统架构为：
1. 空气检测仪采集数据：CO2, PM2.5, 温度，湿度， 甲醛浓度
2. 后端程序：https://github.com/appli456/air-detector-stat
3. APP部分

空气检测仪架构：
1. stm32控制屏幕和传感器，nodeMCU接受stm32的数据并通过wifi传给后台。
2. nodeMCU与APP通过smartConfig技术 获得wifi的ssid和密码

APP:
折线图：使用了helloCharts
<br>详见 /activity/historyActivity
网络： 使用了OKHttp
<br>详见 /activity/loginActivity， /Network/OKHttpUtil
<br>获取天气信息：聚合数据

<br>nodeMCU环境的搭建（Arduino IDE）：
1. http://www.windworkshop.cn/?p=758
2. https://blog.csdn.net/u012388993/article/details/70139147
3. nodeMCU库：https://github.com/esp8266/Arduino

<br>空气检测仪参考：
1. https://post.smzdm.com/p/540069/
2. http://ruten-proteus.blogspot.com/2016/11/ESP8266ArduinoQA-01.html

<br>SmartConfig（貌似官方叫ESPtouch）参考资料：
1. 官方APP: https://github.com/EspressifApp/EsptouchForAndroid
2. nodeMCU：就两行代码，直接看我的程序就行


