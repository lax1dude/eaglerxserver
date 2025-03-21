package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.PacketImageLoader;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class PauseMenuService<PlayerObject> implements IPauseMenuService<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private IPauseMenuImpl defaultPauseMenu;

	public PauseMenuService(EaglerXServer<PlayerObject> server) {
		this.server = server;
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
		return VanillaPauseMenu.INSTANCE;
	}

	@Override
	public ICustomPauseMenu getDefaultPauseMenu() {
		return defaultPauseMenu;
	}

	@Override
	public void setDefaultPauseMenu(ICustomPauseMenu pauseMenu) {
		defaultPauseMenu = (IPauseMenuImpl) pauseMenu;
	}

	@Override
	public void updateAllPlayersPauseMenu(ICustomPauseMenu pauseMenu) {
		server.forEachEaglerPlayer((player) -> {
			IPauseMenuManager<PlayerObject> mgr = player.getPauseMenuManagerOrNull();
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

}
