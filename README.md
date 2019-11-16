# RemoteLight v0.2 - Multifunctional LED Control Software

RemoteLight is a Java based control software for WS2811/WS2812 LEDs (aka Neopixel). It offers a modern, user-friendly UI and a lot of features.

<img src="https://user-images.githubusercontent.com/29163322/67694588-df215580-f9a3-11e9-8666-b8a64ea08465.PNG" width="400"> <img src="https://user-images.githubusercontent.com/29163322/67695256-0b89a180-f9a5-11e9-839e-5b585c7783f2.PNG" width="400">

## Features
- Easy to use
- Supports **Arduino** and **Raspberry Pi**
- Custom colors
- Animations
- Scenes
- Music reactive effects / Visualizer
- Ambilight
- Neopixel simulator

## Hardware
- Windows/Linux/MacOS computer (not required when using Raspberry Pi)
- Arduino or Raspberry Pi
- WS2811, WS2812(B) or similar LED strip

## Software
- [Java](https://java.com)
- [RemoteLight](https://github.com/Drumber/RemoteLightClient/releases/latest)
- [RemoteLightServer](https://github.com/Drumber/RemoteLightServer/releases/latest) (is required when using a Raspberry Pi)

## Quick Start
### Arduino
RemoteLight uses the Glediator protocol for Arduino. Download the [Glediator sketch](http://www.solderlab.de/index.php/downloads/file/33-ws2812-glediator-interface-v1), change the number of pixels and upload it to your Arduino.
> Note: Solderlab.de seems to be offline. You can alternatively download the sketch [here](https://workupload.com/file/vsSx8TBP) or use the [FastLED](https://github.com/marmilicious/FastLED_examples/blob/master/Glediator_test1.ino) variant.

Connect the Arduino to your computer and start RemoteLight. Click '*Add*' to add a new Arduino to the output list. Then select the correct COM port, enter the number of pixels and click '*Save*'.
Now you can activate the output by double clicking on it or by clicking on '*Activate*'.

### Raspberry Pi
*[Youtube Tutorial available](https://youtu.be/3NTMhhIGqto)*  
[RemoteLightServer repository](https://github.com/Drumber/RemoteLightServer)

First of all your Raspberry Pi must be prepared to control WS2811/WS2812 LEDs. There is a well described tutorial on tutorials-raspberrypi.com. [**> Tutorial <**](https://tutorials-raspberrypi.com/connect-control-raspberry-pi-ws2812-rgb-led-strips/)  
After everything works out, you can move on.  
Upload the [RemoteLight-Server.jar](https://github.com/Drumber/RemoteLightServer/releases/latest) onto your Raspberry Pi and start it:
1. Connect to your Raspberry Pi via SSH.
2. Install Java: `sudo apt-get install oracle-java8-jdk`
3. Navigate to the directory where the RemoteLight-Server.jar is located, e.g.: `cd /home/pi/`
4. Start the server: `sudo java -jar RemoteLightServer-pre0.2.0.1.jar`
5. To stop the server: Type `end` or press `Ctrl + c`

Now start RemoteLight on your computer and click on '*Add*' > '*RLServer (Raspberry)*'. Enter the IP address of the Raspberry Pi in the '*Hostname / IP*' field (the Raspberry Pi must be on the same network).
Enter the number of pixels and click Save. If the server is running, you can click on '*Activate*'.
> Note: You can also use the Raspberry Pi without an additional computer. For this you need an OS with a graphical desktop installed on the Raspberry Pi. Start the server (as described above) + the RemoteLight control software, add a new 'RLServer (Raspberry)' output and enter 'localhost' or '127.0.0.1' in the 'Hostname / IP' field.

#### Start server automatically on startup
*[Youtube Tutorial available](https://youtu.be/FYeQfIqytx4)*

1. Install screen: `sudo apt-get install screen`
2. Edit root startup script: `sudo nano /etc/rc.local`
3. Insert the following line before '*exit 0*':  
`su - pi -c "screen -dm -S [name] sudo java -jar /path/to/RemoteLightServer.jar"`
4. Edit the name of the screen and the file path, e.g.:  
*`su - pi -c "screen -dm -S remotelight sudo java -jar /home/pi/RemoteLightServer-pre0.2.0.1.jar"`*

The RemoteLightServer will now start automatically in a new screen when booting.  
To open the screen use `screen -r [name]`. Type `end` or press `Ctrl + C` to stop the server. To leave the screen press `Ctrl + A and D`.

### Simulator
RemoteLight comes with a LED strip simulator in which you can test the effects without a WS2811/WS2812 strip. However, the effects look a thousand times better with a real LED strip 😉.  
To use the simulator you need to go to the output menu and click on '*Open simulator*' in the upper right corner.  
A new window will open. Click on '*Enable*' to activate the simulator. Now go back to the output menu and add a new '*RLServer (Raspberry)*' output. Enter `localhost` or `127.0.0.1` as hostname / IP and the number of pixels you want to simulate. Then click on '*Save*' and activate the output.

## Images
<img src="https://user-images.githubusercontent.com/29163322/67697661-32e26d80-f9a9-11e9-88e2-7f649d96bd36.PNG" width="300"> <img src="https://user-images.githubusercontent.com/29163322/67697662-32e26d80-f9a9-11e9-8863-f4718c65a363.PNG" width="380">

## Video Demo
<a href="https://www.youtube.com/watch?v=u8ptqaTGteg"><img src="https://img.youtube.com/vi/u8ptqaTGteg/0.jpg" width="280"></a>  <a href="https://www.youtube.com/watch?v=-5hKRAyPDT8"><img src="https://img.youtube.com/vi/-5hKRAyPDT8/0.jpg" width="280"></a>

## TODO
- [ ] Create wiki / documentation
- [x] Settings for animations
- [ ] Link / chain several LED strips
- [ ] Improve RemoteLightServer protocol
- [ ] Translate settings

Do you have any other suggestions❔

## Releases
#### pre0.2.0.4
- added settings for animations
- improved start time
- added update checker
- added some new animation modes

#### pre0.2.0.3
First release of the new RemoteLight v0.2 on Github.
- recoded everything
- new UI
- new output system (multiple outputs can be added)
- new animation system + a bunch of new animations
- new MusicSync system + improved sound processing
- improved ScreenColor
- new settings system
- added neopixel simulator
- support for multiple languages (not finished)

#### v0.1.2
- added custom colors
- added support for more than one monitor
- small improvements

#### v0.1.1
- code optimization
- improved sound input window
- save last used sound input
