#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include<SoftwareSerial.h>
#include<ESP8266WiFiMulti.h>
#include<ESP8266HTTPClient.h>
#include<ArduinoJson.h>
/*
* 程序开发工具Arduino 1.8.4
* 开发板 nodeMCU
*/
WiFiUDP Udp;
IPAddress appUdpIP(255, 255, 255, 255);
SoftwareSerial stm32(D7, D8);   //stm32的串口
ESP8266WiFiMulti wifiMulti;
unsigned int localUdpPort = 7001;  // local port to listen on
unsigned int appUdpPort = 18266;  //target port to send reply packet
boolean IFWIFI;   // 是否连接到了WIFI
char tim[20];     // 初试时间, eg:201804281600
char user[20];    // 用户ID
char data[100];  // 保存传感器数据
int index;       // 传感器数据下标

void setup() {
    Serial.begin(115200);
    Serial.print( "\n\n" );   
    IFWIFI = false;
    smartConfig();
    if(IFWIFI){
      char tmp[15];
      sprintf(tmp, "T%s", tim);
      stm32.write("W1");
    }else{
      stm32.write("W0");
    }
}

void loop() {
    if(IFWIFI == true){
      index = 0;
      while(stm32.available > 0){
        readData(stm32.read());
      }
      uploadData();
    }
}

// 从串口读取数据
void readData(unsigned char tmp){
    data[index++] = tmp;
}

// 上传数据到服务器。注意设置上传间隔
void uploadData(){
  if(data == NULL){
    return;
  }
  Serial.print(data);
  if(WiFiMulti.run() == WL_CONNECTED){
    HTTPClient http;
    http.begin("http://196.168.1.1/test.com");
    http.addHeader("userkey", "myKey");
    http.POST(data);
    http.end();
  }
}

//**接受App发送的Udp包，获取用户信息并返回ACK */
boolean receiveUdp(){
  char incomingPacket[255];  // buffer for incoming packets
  char  replyPacket[100];    // a reply string to send back, json字符串，表示成功配对
  sprintf(replyPacket, "{'smartConfig':101}");
  int packetSize = Udp.parsePacket();
  if (packetSize){
    // receive incoming UDP packets
    Serial.printf("Received %d bytes from %s, port %d\n", packetSize, Udp.remoteIP().toString().c_str(), Udp.remotePort());
    int len = Udp.read(incomingPacket, 255);
    if (len > 0){
      incomingPacket[len] = 0;
    }
    Serial.printf("UDP packet contents: %s, Send Reply Packet: %s\n", incomingPacket, replyPacket);
    // send back a reply, to the IP address and port we got the packet from
    Udp.beginPacket(Udp.remoteIP(), appUdpPort);
    Udp.write(replyPacket);
    Udp.endPacket();

    // obtain the json msg
    StaticJsonBuffer<200> jsonBuf;
    JsonObject& root = jsonBuf.parse(incomingPacket);
    if(root.success()){
      strcpy(user, root["user"]);
      strcpy(tim, root["time"]);
    }
    return true;
  }
  return false;
}

//**通过广播包发送本机mac地址和IP到APP端*/
void sendACK(){
  //**通过UDP广播的方式把本机MAC地址发送给APP**//
  byte mac[6];
  char mac_char[18];
  char data[200];
  
  Udp.begin(localUdpPort);
  WiFi.macAddress(mac);

  int index=0;
  for(int i=5;i>=0;i--){
    byte tmp = mac[i];
    mac_char[index++] = int2hex(tmp/16);
    mac_char[index++] = int2hex(tmp%16);
    mac_char[index++] = ':';
  }
  mac_char[17]=0;
  
  sprintf(data, "{'BSSID':'%s', 'IP':'%s'}", mac_char, WiFi.localIP().toString().c_str());
  Serial.println(data);
  
  Udp.beginPacket(appUdpIP, appUdpPort);
  Udp.write(data);
  Udp.endPacket();
  Udp.stop();
}

//**把十进制int转换为十六进制字符*/
char int2hex(int a){
  if( a<10){
    return a+'0';
  }else{
    return a-10+'A';
  }
}

//** 连接到WiFi 并接受APP发送过来的时间、服务器IP等信息*/
void smartConfig(){  
    // 設定模式為 STA ( MODE 只能是 STA ) 才能使用 smartconfig
    WiFi.mode( WIFI_STA );
    if( WiFi.SSID() = "" ) {
        Serial.print( "Begining Smart Config\n" );
        WiFi.beginSmartConfig();
        int count = 0;
        while( WiFi.status() != WL_CONNECTED ) {
            Serial.print( "." );
            delay( 500 );
            count++;
            if( WiFi.smartConfigDone() ) {
                Serial.println( "Done!" );
                WiFi.stopSmartConfig();
                IFWIFI = true;
                break;
            }
            // 超时设置60个循环，即30S
            if(count >= 60){
                IFWIFI = false;
                Serial.print("SamrtConfig is timeout!\n");
                break;
            }
        }
    } else {
            Serial.print( "Using SSID： ");
            Serial.println( WiFi.SSID() );
    }
    //如果SmartConfig成功
    if(IFWIFI == true){
      // 连接至ESO-touch所输入的SSID 和 PASSWORD
       printf( "Connecting.." );
       WiFi.begin();
       while( WiFi.status() != WL_CONNECTED ) {
           Serial.print( "." );
           delay( 500 );
        }
        Serial.println( "OK" );
        // 顯示連線知道所分配的 local IP 位址
        Serial.print( "\nLocal IP: ");
        Serial.println( WiFi.localIP() );
        //发送局域网广播包，广播MAC地址和IP
        Udp.begin(localUdpPort);  
        delay(1000);
        sendACK(); 
        Serial.printf("Now listening at IP %s, UDP port %d\n", WiFi.localIP().toString().c_str(), localUdpPort);
        Udp.begin(localUdpPort);
        int count = 0;
        while(!receiveUdp()){
            delay(500);
            count++;
            if(count > =60){
              IFWIFI = false;
              Serial.print("SmartConfig:receiveUdp is Timeout!");
              break;
            }
        };
        Udp.stop();
    }
}
