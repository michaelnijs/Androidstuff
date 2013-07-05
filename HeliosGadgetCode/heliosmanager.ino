#include <SPI.h>
#include <Helios_LEDS.h>
#include <WiFlyHQ.h>
#include <Wire.h>
#include <Helios_Temperature_Sensor_TMP006.h>
#include <Helios_EEPROM_24AA014.h>
#include <Helios_Colour_Sensor_TCS3414.h>
#include <string.h>

Helios_Colour_Sensor_TCS3414 csensor;
Helios_Temperature_Sensor_TMP006 tsensor;
WiFly wifly;
Helios_LEDS leds;

int lednotification = 13;

unsigned int objecttemp;
unsigned int ambienttemp;

const char ssid[] = "2AndroidAP";
const char passkey[] = "michael123";
const char deviceid[] = "Helios-Webserver";

char buf[80];
char action[50];

unsigned int Rood,Groen,Blauw,Wit,Lux,Cct;


void updateTemperatures() {
  objecttemp = tsensor.ReadObject();
  ambienttemp = tsensor.ReadAmbient();
}

void blinkState() {
	digitalWrite(lednotification, HIGH);
	delay(100);
	digitalWrite(lednotification, LOW);
	delay(200);
	digitalWrite(lednotification, HIGH);
	delay(300);
	digitalWrite(lednotification, LOW);
}


void updateLight()
{
  csensor.Read(&Lux,&Cct);
  delay(1000);
  int inttime = 400;
  int gain = 1;
  int presc = 4;
  csensor.ReadLow(inttime,gain,presc,&Rood,&Groen,&Blauw,&Wit,&Lux,&Cct);
}

void setup() {
	
	Serial.begin(9600);
        Wire.begin();
	Serial.println("S");
        leds.Init();
	pinMode(lednotification, OUTPUT);
        Serial1.begin(9600);
        
	if (!wifly.begin(&Serial1, &Serial)) {
          LedWriteText("FB");
          wifly.terminal();
        }
	// First we need to setup WifiHQ
	Serial.println("Sw");
	if (!wifly.isAssociated()) {
                
		wifly.setSSID(ssid);
		LedWriteText("A");
                wifly.setPassphrase(passkey);
                
		wifly.enableDHCP();
		wifly.save();
		
		if (wifly.join()) {
                        Serial.println(ssid);
			blinkState();
		} else {
			wifly.terminal();
		}
		
	}
        LedWriteText("J");
	Serial.println(wifly.getIP(buf, sizeof(buf)));
	
	wifly.setDeviceID(deviceid);
	
	if (wifly.isConnected()) {
        Serial.println("O");
		wifly.close();
    }
	wifly.setProtocol(WIFLY_PROTOCOL_TCP);
	if (wifly.getPort() != 80) {
		wifly.setPort(80);
		wifly.save();
		wifly.reboot();
		delay(3000);
		
	}
        LedWriteText("D");
        
	Serial.println("Sc");
        blinkState();
}

void sendDefault() {
	wifly.println("HTTP/1.1 200 OK");
	wifly.println("Content-Type: text/html");
	wifly.println("Transfer-Encoding: chunked");
	wifly.println();
	
	wifly.sendChunkln("OK");
	wifly.sendChunkln();
        wifly.sendChunkln();
}

void updateAllSensors() {
  updateTemperatures();
  updateLight();
}

void LedWriteText(char* text_to_write) {
  leds.ClearAll();
  leds.LedMatrixScrollSpeed(20);
  leds.LedMatrixWrite(text_to_write);
  delay(5000);
  while(leds.LedMatrixBusy()){}
}

void fetchURL(char* httpbuffer, char* destination) {
  char * part;
  part = strtok(httpbuffer, " ");
  int partcounter = 0;
  char* result = 0;
  while (part != NULL) {
    partcounter++;
    if (partcounter == 2) {
      destination = part;      
    }
    part = strtok(NULL, " ");
  } 

}

void loop() {
	
	if (wifly.available() > 0) {
		updateAllSensors(); // We update all the freaking time :)
		if (wifly.gets(buf, sizeof(buf))) {
			// There is content to be read
			if (strncmp_P(buf, PSTR("GET "), 4) == 0) {
                                fetchURL(buf, action);
				// Get request
				while (wifly.gets(buf, sizeof(buf)) > 0) {
					// Do nothing just wait
				}
				sendDefault();
			}
		}
		
		
	}
        updateAllSensors();
	
}

