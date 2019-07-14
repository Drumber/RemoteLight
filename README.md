# RemoteLightClient
A Java based control software with a simple UI to control WS2812B/WS2811 led strips (aka Neopixel) or RGB leds/strips.
RemoteLight supports both Arduino and Raspberry Pi!  
[More info on my Blog](https://remotelight-software.blogspot.com/)

<img src="https://user-images.githubusercontent.com/29163322/61181838-bad77e00-a62b-11e9-962f-6f5cf1acdaf9.PNG" width="200">

# Functions
**MusicSync (LED Music Visualizer)**  
 *Due to java, it is only possible to use input devices as sound source, such as microphones, line-in or other software ([Voicemeeter](https://www.vb-audio.com/Voicemeeter/ "Voicemeeter")).*  
**ScreenColor (Ambilight)**  
**Scenes**  
**Animations**  
 *More animations will be added soon..*

# Installation
You can find a [documentation on my blog](https://remotelight-software.blogspot.com/p/documentation_20.html "documentation on my blog").
### Arduino
[YouTube Tutorial](https://youtu.be/NUhIEzkLeKA "YouTube Tutorial")  
Download the [GlediatorProtocol sketch](http://www.solderlab.de/index.php/downloads/file/33-ws2812-glediator-interface-v1 "GlediatorProtocol sketch"), edit the number of pixels and upload it to your Arduino.
Connect your WS2812(B) or WS2811 led strip.

<img src="https://lh3.googleusercontent.com/d6hwbkXUIova36OHqi38XihnpJgDsVl6RQqKW-mIvtX3DQ3h2tiPPwOJvBqoVHxhOk0IpW57p-0=s500" width="300">

Open RemoteLightClient and select *WS281x Strip (Arduino)* mode in settings. Don't forget to open the ComPort.

### Raspberry Pi
I recommend this [tutorial](https://tutorials-raspberrypi.com/connect-control-raspberry-pi-ws2812-rgb-led-strips/ "tutorial").
After this, upload the [RemoteLight Server](https://remotelight-software.blogspot.com/p/downloads.html "RemoteLight Server") to your RaspberryPi.  
Start Server:  `sudo java -jar RemoteLight-RPi-0.1.x.jar`  
Stop Server: Type `end` or use `Ctrl +  C`  

Your PC should be in the same network as the RaspberryPi. Open the RemoteLightClient software on your PC and select ‘WS281x Strip (RPi)’ mode in settings. Then enter the IP of the RaspberryPi in the field of the main gui. Click ‘connect’, the client should connect to the server if everything is right.

**Run Server automatically on startup**  
Install screen: `sudo apt-get install screen`  
Edit root startup script: `sudo nano /etc/rc.local`  
Add this line befor *exit 0* : `su - pi -c "screen -dm -S remotelight sudo java -jar /path/to/RemoteLightServer"`  
Save (Ctrl + O) and close (Ctrl + X)  
Reboot: `sudo reboot`  
The server should have started in a detached screen.  
You can open the screen using this command: `screen -r remotelight`
Use ‘Ctrl + A and D’ to leave the screen.

