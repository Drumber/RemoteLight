package de.lars.remotelightcore;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Random;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.remotelightserver.RemoteLightServer;
import de.lars.remotelightcore.utils.UpdateChecker;

public class CoreTest {
	
	@DisplayName("Before Initialization Test")
	@BeforeAll
	static void beforeTest() {
		System.out.println("Before Init Test");
		assertThrows(IllegalStateException.class, () -> RemoteLightCore.getInstance());
	}

	@DisplayName("RemoteLightCore Initialization Test")
	@Test
	public void initTest() {
		System.out.println("Init Test");
		RemoteLightCore core = new RemoteLightCore(null, false);
		assertTrue(RemoteLightCore.isHeadless() == false);
		assertTrue(core.getEffectManagerHelper().getAllManagers().length > 0);
		assertTrue(core.getSettingsManager().getSettings().size() > 0);
		
		if(core.getOutputManager().getActiveOutput() != null) {
			assertTrue(RemoteLightCore.getLedNum() >= 1, "LED Number should be minimum 1 when active output is not null");
		}
	}
	
	@DisplayName("RemoteLight Run Test")
	@Test
	public void runTest() {
		System.out.println("Run Test");
		assumeTrue(RemoteLightCore.getInstance() != null);
		RemoteLightCore core = RemoteLightCore.getInstance();
		
		// register new output
		String testDevice = "_Test_" + new Random().nextInt(900) + 100;
		RemoteLightServer device = new RemoteLightServer(testDevice, "localhost");
		int pixels = new Random().nextInt(300) + 10;
		device.setPixels(pixels);
		core.getDeviceManager().addDevice(device);
		
		assumeTrue(core.getDeviceManager().isIdUsed(testDevice));
		assertAll(() -> core.getOutputManager().setActiveOutput(device));
		assertEquals(pixels, RemoteLightCore.getLedNum());
		
		// start animation
		Animation animation = core.getAnimationManager().getAnimations().get(0);
		core.getAnimationManager().start(animation);
		assertTrue(core.getAnimationManager().isActive());
		assertTrue(core.getEffectManagerHelper().getActiveManager() instanceof AnimationManager);
		assertEquals(core.getAnimationManager().getActiveAnimation().getName(), animation.getName());
		
		// stop animation
		assertAll(() -> core.getAnimationManager().stop());
		assertEquals(core.getEffectManagerHelper().getActiveManager(), null);
		
		// stop device
		core.getOutputManager().deactivate(device);
		assertFalse(device.getState() == ConnectionState.CONNECTED);
		
		// remove device
		assertTrue(core.getDeviceManager().removeDevice(device));
		assertTrue(core.getDeviceManager().getDevice(testDevice) == null);
	}
	
	@DisplayName("Version compare test")
	@Test
	public void versionTest() {
		System.out.println("Version Test");
		assertFalse(UpdateChecker.isVersionNewer("0.2.4", "0.2.3"));
		assertFalse(UpdateChecker.isVersionNewer("0.2.4", "0.1"));
		assertTrue(UpdateChecker.isVersionNewer("0.2.4", "0.2.4.1"));
		assertTrue(UpdateChecker.isVersionNewer("v0.1.9", "v0.1.10"));
		assertTrue(UpdateChecker.isVersionNewer("v0.3-SNAPSHOT", "v0.3"));
		assertFalse(UpdateChecker.isVersionNewer("v0.2", "v0.2.0"));
	}
	
	@DisplayName("RemoteLight Close Test")
	@AfterAll
	static void closeTest() {
		System.out.println("Close Test");
		assumeTrue(RemoteLightCore.getInstance() != null);
		RemoteLightCore core = RemoteLightCore.getInstance();
		core.close(false);
		assertTrue(core.getEffectManagerHelper().getActiveManager() == null);
	}
	
}
