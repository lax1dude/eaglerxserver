package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.PacketImageLoader;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataPauseMenu;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class PauseMenuService<PlayerObject> implements IPauseMenuService<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private IPauseMenuImpl defaultPauseMenu = PauseMenuImplVanilla.INSTANCE;

	public PauseMenuService(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	public void reloadDefaultPauseMenu(File dataDir, ConfigDataPauseMenu pauseMenu) throws IOException {
		defaultPauseMenu = (IPauseMenuImpl) DefaultPauseMenuLoader.loadDefaultPauseMenu(dataDir, pauseMenu, this);
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public IWebViewService<PlayerObject> getWebViewService() {
		return server.getWebViewService();
	}

	@Override
	public ICustomPauseMenu getVanillaPauseMenu() {
		return PauseMenuImplVanilla.INSTANCE;
	}

	@Override
	public ICustomPauseMenu getDefaultPauseMenu() {
		return defaultPauseMenu;
	}

	public SPacketCustomizePauseMenuV4EAG getDefaultPauseMenuUnsafe() {
		return defaultPauseMenu.getPacket();
	}

	@Override
	public void setDefaultPauseMenu(ICustomPauseMenu pauseMenu) {
		if(pauseMenu == null) {
			throw new NullPointerException("pauseMenu");
		}
		defaultPauseMenu = (IPauseMenuImpl) pauseMenu;
	}

	@Override
	public void updateAllPlayersPauseMenu(ICustomPauseMenu pauseMenu) {
		if(pauseMenu == null) {
			throw new NullPointerException("pauseMenu");
		}
		server.forEachEaglerPlayer((player) -> {
			IPauseMenuManager<PlayerObject> mgr = player.getPauseMenuManager();
			if(mgr != null) {
				mgr.updatePauseMenu(pauseMenu);
			}
		});
	}

	@Override
	public IPauseMenuBuilder createPauseMenuBuilder() {
		return new PauseMenuBuilder();
	}

	@Override
	public PacketImageData loadPacketImageData(int[] pixelsARGB8, int width, int height) {
		return PacketImageLoader.loadPacketImageData(pixelsARGB8, width, height);
	}

	@Override
	public PacketImageData loadPacketImageData(BufferedImage bufferedImage, int maxWidth, int maxHeight) {
		return PacketImageLoader.loadPacketImageData(bufferedImage, maxWidth, maxHeight);
	}

	@Override
	public PacketImageData loadPacketImageData(InputStream inputStream, int maxWidth, int maxHeight)
			throws IOException {
		return PacketImageLoader.loadPacketImageData(inputStream, maxWidth, maxHeight);
	}

	@Override
	public PacketImageData loadPacketImageData(File imageFile, int maxWidth, int maxHeight) throws IOException {
		return PacketImageLoader.loadPacketImageData(imageFile, maxWidth, maxHeight);
	}

	public PauseMenuManager<PlayerObject> createPauseMenuManager(EaglerPlayerInstance<PlayerObject> player) {
		if(player.hasCapability(EnumCapabilitySpec.PAUSE_MENU_V0)) {
			PauseMenuManager<PlayerObject> mgr = new PauseMenuManager<>(player, this);
			mgr.updatePauseMenu(defaultPauseMenu);
			return mgr;
		}else {
			return null;
		}
	}

}
