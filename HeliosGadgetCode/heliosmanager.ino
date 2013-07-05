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

byte lednotification = 13;

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
  byte inttime = 400;
  byte gain = 1;
  byte presc = 4;
  csensor.ReadLow(inttime,gain,presc,&Rood,&Groen,&Blauw,&Wit,&Lux,&Cct);
}

void setup() {
	
	Serial.begin(9600);
        Wire.begin();

	pinMode(lednotification, OUTPUT);
        Serial1.begin(9600);
        
	if (!wifly.begin(&Serial1, &Serial)) {
          digitalWrite(lednotification, HIGH);
          delay(10000);
          wifly.terminal();
        }
	// First we need to setup WifiHQ
	if (!wifly.isAssociated()) {
                
		wifly.setSSID(ssid);
                wifly.setPassphrase(passkey);
		wifly.enableDHCP();
		wifly.save();
		
		if (!wifly.join()) {
                        digitalWrite(lednotification, HIGH);
                        delay(10000);
                      	wifly.terminal();
		}
		
	}

	wifly.setDeviceID(deviceid);
	
	if (wifly.isConnected()) {
		wifly.close();
        }
        
	wifly.setProtocol(WIFLY_PROTOCOL_TCP);
	if (wifly.getPort() != 80) {
		wifly.setPort(80);
		wifly.save();
		wifly.reboot();
		delay(3000);
		
	}
        blinkState();
}

void sendDefault(char* data) {
	wifly.println("HTTP/1.1 200 OK");
	wifly.println("Content-Type: text/html");
	wifly.println("Transfer-Encoding: chunked");
	wifly.println();
	
	wifly.sendChunkln(data);
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

void fetchURL(char* httpbuffer) {
  char * part;
  part = strtok(httpbuffer, " ");
  byte partcounter = 0;
  byte spartcounter = 0;
  char * spart;
  char * uri;
  char * arg;
  while (part != NULL) {
    partcounter++;
    if (partcounter == 2) {
        // now we split on ?
        spart = strtok(part, "?");
        while (spart != NULL) {
          spartcounter++;
          if (spartcounter==1) {
            // the URI is here
            uri = spart;
          } else {
            // the arguments
            arg = spart;
          }
          spart = strtok(NULL, "?");
        }
        
        runActions(uri, arg);
    }
    part = strtok(NULL, " ");
  } 
}

void runActions(char* uri, char* arg) {
  if (uri == "/getTemp") {
    // The arg parameter is not used nor important
    char result[10];
    sprintf(result,"%d;%d", objecttemp, ambienttemp);
    sendDefault(result);
  }
}

void loop() {
	
	if (wifly.available() > 0) {
		updateAllSensors(); // We update all the freaking time :)
		if (wifly.gets(buf, sizeof(buf))) {
			// There is content to be read
			if (strncmp_P(buf, PSTR("GET "), 4) == 0) {
                                fetchURL(buf);
				// Get request
				while (wifly.gets(buf, sizeof(buf)) > 0) {
					// Do nothing just wait
				}
			}
		}
		
		
	}
        updateAllSensors();
	
}
