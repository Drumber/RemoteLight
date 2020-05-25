package de.lars.remotelightclient.musicsync.sound.nativesound;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import org.tinylog.Logger;

import com.xtaudio.xt.*;

import de.lars.remotelightclient.utils.DirectoryUtil;

public class NativeSound {

	public static String LIB_CLASSPATH = "/resources/lib/xtaudio.zip";
	public static final int[] SAMPLERATES = {11025, 22050, 44100, 48000, 96000, 192000};
	public static final String LIB_NAME = "xt-core";
	
	private List<XtService> listServices;
	private boolean initialized = false;
	private XtStream stream;

	public NativeSound() {
		this(false);
	}

	public NativeSound(boolean init) {
		if (init)
			init();
	}

	public void init() {
		if (!initialized) {
			boolean isWin = System.getProperty("os.name").contains("Windows");

			boolean libInstalled;
			try {
				XtAudio audio = new XtAudio(null, null, null, null);
				libInstalled = true;
				initialized = true;
				Logger.info("XtAudio native library already installed.");
				audio.close();
			} catch(UnsatisfiedLinkError e) {
				libInstalled = false;
			}
			
			// copy lib folder from classpath to .RemoteLight/lib
			if(!libInstalled) {
				Logger.info("Installing XtAudio native library...");
				try {
					File libPath = copyLibs();
					String path = libPath.getAbsolutePath() + File.separator;
					
					String libPathProp = path + File.pathSeparator + System.getProperty("java.library.path");
					System.setProperty("jna.library.path", libPathProp);
					
					initialized = true;
					XtAudio audio = new XtAudio(null, null, null, null);
					Logger.info("Loaded native sound library for " + (isWin ? "Windows" : "Linux") + " from classpath.");
					audio.close();
				} catch (IOException | NullPointerException | UnsatisfiedLinkError e) {
					initialized = false;
					Logger.error(e, "Could not load libraries folder.");
					return;
				}
			}
		}
		this.setServices();
	}

	public File copyLibs() throws IOException {
		File filePath = new File(DirectoryUtil.getDataStoragePath() + "lib");
		filePath.mkdirs();
		filePath.deleteOnExit();
		//DirectoryUtil.copyFolderFromJar(LIB_CLASSPATH, filePath, true);
		DirectoryUtil.copyZipFromJar(LIB_CLASSPATH, filePath);
		return filePath;
	}
	
	public boolean isInitialized() {
		return initialized;
	}

	public void close() {
		closeDevice();
	}

	
	private void setServices() {
		listServices = new ArrayList<>();
		for (int s = 0; s < XtAudio.getServiceCount(); s++) {
			XtService service = XtAudio.getServiceByIndex(s);
			listServices.add(service);
		}
	}
	
	public List<XtService> getServices() {
		return listServices;
	}

	public List<XtDevice> getDevices(XtService service) {
		List<XtDevice> listDevices = new ArrayList<>();
		for (int d = 0; d < service.getDeviceCount(); d++) {
			try (XtDevice device = service.openDevice(d)) {
				listDevices.add(device);
			}
		}
		return listDevices;
	}
	
	public List<String> getDeviceNames(XtService service) {
		List<String> listNames = new ArrayList<>();
		for (int d = 0; d < service.getDeviceCount(); d++) {
			try (XtDevice device = service.openDevice(d)) {
				listNames.add(device.getName());
			}
		}
		return listNames;
	}

	public List<Integer> getSupportedDevicesIndex(XtService service, XtFormat format) {
		List<Integer> listDevices = new ArrayList<>();
		for (int d = 0; d < service.getDeviceCount(); d++) {
			try (XtDevice device = service.openDevice(d)) {
				if (device != null && device.supportsFormat(format)) {
					listDevices.add(d);
				}
			}
		}
		return listDevices;
	}
	
	public boolean isDeviceSupported(XtService service, int deviceIndex, XtFormat format) {
		try (XtDevice device = service.openDevice(deviceIndex)) {
			if (device != null && device.supportsFormat(format)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Open a new stream from specified device
	 * @param service service that opens the device
	 * @param deviceIndex Device index of the specified XtService
	 * @param format format must be supported by the device
	 * @param callback stream callback
	 */
	public void openDevice(XtService service, int deviceIndex, XtFormat format, XtStreamCallback callback) {
		if(stream != null) {
			throw new IllegalStateException("There is already a stream running. Close active stream before calling again.");
		}
		try (XtDevice device = service.openDevice(deviceIndex)) {
			if (device == null || !device.supportsFormat(format)) {
				Logger.error(String.format("Device '%s' or format '%s Hz, %s, %s channels' not supported!",
						device.getName(), format.mix.rate, format.mix.sample, format.inputs));
				return;
			}
			
			StreamContext context = new StreamContext();
			XtBuffer buffer = device.getBuffer(format);
			double bufferSize = XtAudio.isWin32() ? buffer.current : 10.0;
			
			stream = device.openStream(format, true, false, bufferSize, callback, null, context);
			int sampleSize = XtAudio.getSampleAttributes(format.mix.sample).size;
			context.buffer = ByteBuffer.allocate(stream.getFrames() * format.inputs * sampleSize).order(ByteOrder.LITTLE_ENDIAN);
			stream.start();
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Close active device and stream
	 */
	public void closeDevice() {
		if(stream != null) {
			try(XtAudio audio = new XtAudio(null, null, null, null)) {
				stream.stop();
				stream.close();
				stream = null;
			}
		}
	}

}
