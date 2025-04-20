package net.lax1dude.eaglercraft.backend.eaglermotd.base.frame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.lax1dude.eaglercraft.backend.eaglermotd.base.util.BitmapUtil;
import net.lax1dude.eaglercraft.backend.eaglermotd.base.util.GsonUtil;
import net.lax1dude.eaglercraft.backend.server.api.IComponentHelper;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;

public class PipelineLoader {

	public static List<IFrameUpdater> loadPipeline(IEaglerXServerAPI<?> server, BitmapUtil bitmapLoader, List<JsonObject> frame) throws IOException {
		ImmutableList.Builder<IFrameUpdater> frames = ImmutableList.builder();
		PipelineLoader loader = new PipelineLoader(server, bitmapLoader);
		for(JsonObject json : frame) {
			frames.add(loader.next(json));
		}
		return frames.build();
	}

	public interface IFramePipelineUpdater extends IFrameUpdater {

		default boolean update(IMOTDConnection connection) {
			update0(connection);
			return true;
		}

		void update0(IMOTDConnection connection);

	}

	private final IEaglerXServerAPI<?> server;
	private final BitmapUtil bitmapLoader;

	private BitmapUtil.Bitmap bitmap = null;
	private int spriteX = 0;
	private int spriteY = 0;
	private boolean flipX = false;
	private boolean flipY = false;
	private int rotate = 0;
	private float[] color = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
	private float[] tint = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };

	private PipelineLoader(IEaglerXServerAPI<?> server, BitmapUtil bitmapLoader) {
		this.server = server;
		this.bitmapLoader = bitmapLoader;
	}

	private IFrameUpdater next(JsonObject frame) throws IOException {
		IComponentHelper helper = server.getComponentHelper();
		ImmutableList.Builder<IFrameUpdater> builder = ImmutableList.builder();
		JsonElement v = frame.get("online");
		if(v != null) {
			if(v.isJsonPrimitive() && ((JsonPrimitive)v).isNumber()) {
				int vv = v.getAsInt();
				builder.add((IFramePipelineUpdater) (motd) -> motd.setPlayerTotal(vv));
			}else {
				builder.add((IFramePipelineUpdater) (motd) -> motd.setPlayerTotal(motd.getDefaultPlayerTotal()));
			}
		}
		v = frame.get("max");
		if(v != null) {
			if(v.isJsonPrimitive() && ((JsonPrimitive)v).isNumber()) {
				int vv = v.getAsInt();
				builder.add((IFramePipelineUpdater) (motd) -> motd.setPlayerMax(vv));
			}else {
				builder.add((IFramePipelineUpdater) (motd) -> motd.setPlayerMax(motd.getDefaultPlayerMax()));
			}
		}
		v = frame.get("players");
		if(v != null) {
			if(v.isJsonArray()) {
				List<String> players = new ArrayList<>();
				JsonArray vv = (JsonArray) v;
				for(int i = 0, l = vv.size(); i < l; ++i) {
					players.add(helper.translateAlternateColorCodes('&', vv.get(i).getAsString()));
				}
				builder.add((IFramePipelineUpdater) (motd) -> motd.setPlayerList(players));
			}else {
				builder.add((IFramePipelineUpdater) (motd) -> motd.getDefaultPlayerList());
			}
		}
		String line = GsonUtil.optString(frame.get("text0"), GsonUtil.optString(frame.get("text"), null));
		if(line != null) {
			List<String> motdList = new ArrayList<>(2);
			int ix = line.indexOf('\n');
			if(ix != -1) {
				motdList.add(helper.translateAlternateColorCodes('&', line.substring(0, ix)));
				motdList.add(helper.translateAlternateColorCodes('&', line.substring(ix + 1)));
			}else {
				motdList.add(helper.translateAlternateColorCodes('&', line));
			}
			line = GsonUtil.optString(frame.get("text1"), null);
			if(line != null) {
				if(motdList.size() <= 0) {
					motdList.add(line);
				}else {
					motdList.add(helper.translateAlternateColorCodes('&', line));
				}
			}
			builder.add((IFramePipelineUpdater) (motd) -> motd.setServerMOTD(motdList));
		}
		boolean shouldRenderIcon = false;
		JsonElement icon = frame.get("icon");
		if(icon != null) {
			String asString = (icon.isJsonPrimitive() && ((JsonPrimitive)icon).isString()) ? icon.getAsString() : null;
			shouldRenderIcon = true;
			if(icon.isJsonNull() || asString == null || asString.equalsIgnoreCase("none")
					|| asString.equalsIgnoreCase("null") || asString.equalsIgnoreCase("color")) {
				bitmap = null;
			}else {
				bitmap = bitmapLoader.getCachedIcon(asString);
			}
			spriteX = spriteY = rotate = 0;
			flipX = flipY = false;
			color = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
			tint = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		}
		int sprtX = GsonUtil.optInt(frame.get("icon_spriteX"), -1) * 64;
		if(sprtX >= 0 && sprtX != spriteX) {
			shouldRenderIcon = true;
			spriteX = sprtX;
		}
		int sprtY = GsonUtil.optInt(frame.get("icon_spriteY"), -1) * 64;
		if(sprtY >= 0 && sprtY != spriteY) {
			shouldRenderIcon = true;
			spriteY = sprtY;
		}
		sprtX = GsonUtil.optInt(frame.get("icon_pixelX"), -1);
		if(sprtX >= 0 && sprtX != spriteX) {
			shouldRenderIcon = true;
			spriteX = sprtX;
		}
		sprtY = GsonUtil.optInt(frame.get("icon_pixelY"), -1);
		if(sprtY >= 0 && sprtY != spriteY) {
			shouldRenderIcon = true;
			spriteY = sprtY;
		}
		JsonElement flip = frame.get("icon_flipX");
		if(flip != null) {
			shouldRenderIcon = true;
			if(flip.isJsonPrimitive() && ((JsonPrimitive)flip).isBoolean()) {
				flipX = flip.getAsBoolean();
			}else {
				flipX = false;
			}
		}
		flip = frame.get("icon_flipY");
		if(flip != null) {
			shouldRenderIcon = true;
			if(flip.isJsonPrimitive() && ((JsonPrimitive)flip).isBoolean()) {
				flipY = flip.getAsBoolean();
			}else {
				flipY = false;
			}
		}
		int rot = GsonUtil.optInt(frame.get("icon_rotate"), -1);
		if(rot >= 0) {
			shouldRenderIcon = true;
			rotate = rot % 4;
		}
		JsonArray colorF = GsonUtil.optJSONArray(frame.get("icon_color"));
		if(colorF != null && colorF.size() > 0) {
			shouldRenderIcon = true;
			color[0] = colorF.get(0).getAsFloat();
			color[1] = colorF.size() > 1 ? colorF.get(1).getAsFloat() : color[1];
			color[2] = colorF.size() > 2 ? colorF.get(2).getAsFloat() : color[2];
			color[3] = colorF.size() > 3 ? colorF.get(3).getAsFloat() : 1.0f;
		}
		colorF = GsonUtil.optJSONArray(frame.get("icon_tint"));
		if(colorF != null && colorF.size() > 0) {
			shouldRenderIcon = true;
			tint[0] = colorF.get(0).getAsFloat();
			tint[1] = colorF.size() > 1 ? colorF.get(1).getAsFloat() : tint[1];
			tint[2] = colorF.size() > 2 ? colorF.get(2).getAsFloat() : tint[2];
			tint[3] = colorF.size() > 3 ? colorF.get(3).getAsFloat() : 1.0f;
		}
		if(shouldRenderIcon) {
			int[] newIcon = null;
			if(bitmap != null) {
				newIcon = bitmap.getSprite(spriteX, spriteY);
			}
			if(newIcon == null) {
				newIcon = new int[64*64];
			}
			newIcon = BitmapUtil.applyTint(newIcon, tint[0], tint[1], tint[2], tint[3]);
			if(color[3] > 0.0f) {
				newIcon = BitmapUtil.applyColor(newIcon, color[0], color[1], color[2], color[3]);
			}
			if(bitmap != null) {
				if(flipX) {
					newIcon = BitmapUtil.flipX(newIcon);
				}
				if(flipY) {
					newIcon = BitmapUtil.flipY(newIcon);
				}
				if(rotate != 0) {
					newIcon = BitmapUtil.rotate(newIcon, rotate);
				}
			}
			byte[] newIconBytes = BitmapUtil.toBytes(newIcon);
			builder.add((IFramePipelineUpdater) (motd) -> motd.setServerIcon(newIconBytes));
		}
		List<IFrameUpdater> ret = builder.build();
		if(ret.size() == 0) {
			return NOPFrameUpdater.INSTANCE;
		}else if(ret.size() == 1) {
			return ret.get(0);
		}else {
			return new CompoundFrameUpdater(ret);
		}
	}

}
