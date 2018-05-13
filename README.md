# Nami_AirDetector
空气检测仪APP: nami 空探
包括android APP源码以及nodeMCU源码
<br>nodeMCU 位nodeMCU的源码
系统架构为：
1. 空气检测仪采集数据：CO2, PM2.5, 温度，湿度， 甲醛浓度
2. 后端程序
3. APP部分

空气检测仪架构：
1. stm32控制屏幕和传感器，nodeMCU接受stm32的数据并通过wifi传给后台。
2. nodeMCU与APP通过smartConfig技术 获得wifi的ssid和密码

APP:
折线图：使用了helloCharts
<br>详见 /activity/historyActivity
网络： 使用了OKHttp
<br>详见 /activity/loginActivity， /Network/OKHttpUtil
