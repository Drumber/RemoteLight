package de.lars.remotelightcore.devices.link.multi;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.tinylog.Logger;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class MultiOutput extends Device {
	private static final long serialVersionUID = 5961337662461972542L;
	
	private transient List<Device> devices;
	private List<String> deviceIds;
	private DividingMethod processingMethod;

	public MultiOutput(String id) {
		super(id, 0);
		devices = new ArrayList<Device>();
		deviceIds = new ArrayList<String>();
		processingMethod = DividingMethod.CUT_OVERHANGING;
	}

	public List<Device> getDevices() {
		return devices;
	}
	
	public void addDevices(Device... devices) {
		for(Device d : devices) {
			Objects.requireNonNull(d, "Device should not be null.");
			this.devices.add(d);
			this.deviceIds.add(d.getId());
		}
		updatePixelNum();
	}
	
	public void setProcessingMethod(DividingMethod processingMethod) {
		this.processingMethod = processingMethod;
		updatePixelNum();
	}
	
	public DividingMethod getProcessingMethod() {
		return processingMethod;
	}
	
	public void removeDevice(Device d) {
		devices.remove(d);
		deviceIds.remove(d.getId());
		updatePixelNum();
	}
	
	public void clearDevices() {
		devices.clear();
		deviceIds.clear();
	}
	
	public void updatePixelNum() {
		if(devices.size() == 0) {
			setPixels(0);
			return;
		}
		
		if(	processingMethod == DividingMethod.CUT_OVERHANGING ||
			processingMethod == DividingMethod.CUT_OVERHANGING_CENTER)
		{
			// find largest pixel number
			int pix = 0;
			for(Device d : devices) {
				if(d.getPixels() > pix) {
					pix = d.getPixels();
				}
			}
			setPixels(pix);
			
		} else if(	processingMethod == DividingMethod.BLACK_OVERHANGING ||
					processingMethod == DividingMethod.BLACK_OVERHANGING_CENTER)
		{
			// find smallest pixel number
			int pix = Integer.MAX_VALUE;
			for(Device d : devices) {
				if(d.getPixels() < pix) {
					pix = d.getPixels();
				}
			}
			setPixels(pix);
		}
	}
	

	@Override
	public ConnectionState connect() {
		for(Device d : devices) {
			d.connect();
		}
		return getConnectionState();
	}

	@Override
	public ConnectionState disconnect() {
		for(Device d : devices) {
			d.disconnect();
		}
		return getConnectionState();
	}

	@Override
	public ConnectionState getConnectionState() {
		boolean disconnected = false;
		for(Device d : devices) {
			if(d.getConnectionState() == ConnectionState.FAILED) {
				disconnect();
				return ConnectionState.FAILED;
			}
			if(d.getConnectionState() == ConnectionState.DISCONNECTED) {
				disconnected = true;
			}
		}
		return disconnected ? ConnectionState.DISCONNECTED : ConnectionState.CONNECTED;
	}

	@Override
	public void onLoad() {
		if(devices == null)
			devices = new ArrayList<Device>();
		if(deviceIds == null)
			deviceIds = new ArrayList<String>();
		
		List<String> listNotFound = new ArrayList<String>();
		// try to find saved devices by id
		for(String id : deviceIds) {
			Device d = RemoteLightCore.getInstance().getDeviceManager().getDevice(id);
			if(d != null) {
				devices.add(d);
			} else {
				listNotFound.add(id);
			}
		}
		if(listNotFound.size() > 0) {
			// remove all not found devices and show error message
			deviceIds.removeAll(listNotFound);
			String notFound = String.join(", ", listNotFound);
			Logger.warn("[MultiOutput] Could not find devices: " + notFound);
			RemoteLightCore.getInstance().showNotification(
					new Notification(NotificationType.WARN, getId() + " (MultiOutput)", "Could not find the following devices: " + notFound));
		}
	}

	@Override
	public void send(Color[] pixels) {
		if(pixels.length >= super.getPixels()) {
			if(pixels.length != super.getPixels())
				pixels = Arrays.copyOfRange(pixels, 0, super.getPixels());
			
			for(Device d : devices) {
				d.onOutput(processPixels(d, pixels));
			}
		} else {
			Logger.error("Wrong output packet lenght! Expected " + super.getPixels() + ", got " + pixels.length);
		}
	}
	
	
	private Color[] processPixels(Device d, Color[] pixels) {
		if(processingMethod == DividingMethod.CUT_OVERHANGING && d.getPixels() < pixels.length) {
			return Arrays.copyOfRange(pixels, 0, d.getPixels());
		} else if(processingMethod == DividingMethod.CUT_OVERHANGING_CENTER && d.getPixels() < pixels.length) {
			int diff = pixels.length - d.getPixels();
			int from = diff / 2;
			return Arrays.copyOfRange(pixels, from, pixels.length - (diff - from));
			
		} else if(processingMethod == DividingMethod.BLACK_OVERHANGING && d.getPixels() > pixels.length) {
			Color[] newArr = PixelColorUtils.colorAllPixels(Color.BLACK, d.getPixels());
			System.arraycopy(pixels, 0, newArr, 0, pixels.length);
			return newArr;
		} else if(processingMethod == DividingMethod.BLACK_OVERHANGING_CENTER && d.getPixels() > pixels.length) {
			int diff = d.getPixels() - pixels.length;
			Color[] newArr = PixelColorUtils.colorAllPixels(Color.BLACK, d.getPixels());
			System.arraycopy(pixels, 0, newArr, diff / 2, pixels.length);
			return newArr;
			
		}
		return pixels;
	}

}
