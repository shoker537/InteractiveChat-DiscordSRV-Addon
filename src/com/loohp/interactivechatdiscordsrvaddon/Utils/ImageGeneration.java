package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.map.MapPalette;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ItemMapWrapper.MapIcon;

@SuppressWarnings("deprecation")
public class ImageGeneration {
	
	private static final int MAP_ICON_PER_ROLE = 16;
	private static final int SPACING = 36;
	private static final String FULL_BODY_IMAGE_KEY = "FullBodyImage";
	private static final String PLAYER_HEAD_KEY = "PlayerHeadImage";
	
	public static BufferedImage getItemStackImage(ItemStack item) throws IOException {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		BufferedImage background = new BufferedImage(36, 36, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = background.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		if (item == null || item.getType().equals(Material.AIR)) {
			return background;
		}
		
		BufferedImage itemImage = getRawItemImage(item);
		
		if (itemImage != null) {
			g.drawImage(itemImage, 0, 0, null);
		}
		g.dispose();
		
		return background;
	}
	
	public static BufferedImage getInventoryImage(Inventory inventory) throws IOException {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.incrementAndGet();
		int rows = inventory.getSize() / 9;
		BufferedImage background = InteractiveChatDiscordSrvAddon.plugin.getGUITexture(rows + "_rows");
		
		BufferedImage target = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = target.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(background, 0, 0, null);
		
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null || item.getType().equals(Material.AIR)) {
				continue;
			}
			
			BufferedImage itemImage = getRawItemImage(item);
			
			if (itemImage != null) {
				g.drawImage(itemImage, 18 + (SPACING * (i % 9)), 18 + (SPACING * (i / 9)), null);
			}
		}
		g.dispose();
		
		return target;
	}
	
	public static BufferedImage getPlayerInventoryImage(Inventory inventory, Player player) throws Exception {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.incrementAndGet();
		BufferedImage background = InteractiveChatDiscordSrvAddon.plugin.getGUITexture("player_inventory");
		
		BufferedImage target = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = target.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(background, 0, 0, null);
		
		int i = 0;
		//hotbar
		for (; i < 9; i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null || item.getType().equals(Material.AIR)) {
				continue;
			}
			
			BufferedImage itemImage = getRawItemImage(item);
			
			if (itemImage != null) {
				g.drawImage(itemImage, 18 + (SPACING * (i % 9)), 286 + (SPACING * (i / 9)), null);
			}
		}
		
		//inv
		for (; i < 36; i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null || item.getType().equals(Material.AIR)) {
				continue;
			}
			
			BufferedImage itemImage = getRawItemImage(item);
			
			if (itemImage != null) {
				g.drawImage(itemImage, 18 + (SPACING * (i % 9)), 170 + (SPACING * ((i - 9) / 9)), null);
			}
		}
		
		//boots
		ItemStack boots = inventory.getItem(i);
		if (boots == null || boots.getType().equals(Material.AIR)) {
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getItemTexture("empty_armor_slot_boots"), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
		} else {
			BufferedImage itemImage = getRawItemImage(boots);			
			if (itemImage != null) {
				g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
			}
		}
		i++;
		
		//leggings
		ItemStack leggings = inventory.getItem(i);
		if (leggings == null || leggings.getType().equals(Material.AIR)) {
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getItemTexture("empty_armor_slot_leggings"), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
		} else {
			BufferedImage itemImage = getRawItemImage(leggings);			
			if (itemImage != null) {
				g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
			}
		}
		i++;
		
		//chestplate
		ItemStack chestplate = inventory.getItem(i);
		if (chestplate == null || chestplate.getType().equals(Material.AIR)) {
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getItemTexture("empty_armor_slot_chestplate"), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
		} else {
			BufferedImage itemImage = getRawItemImage(chestplate);			
			if (itemImage != null) {
				g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
			}
		}
		i++;
		
		//helmet
		ItemStack helmet = inventory.getItem(i);
		if (helmet == null || helmet.getType().equals(Material.AIR)) {
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getItemTexture("empty_armor_slot_helmet"), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
		} else {
			BufferedImage itemImage = getRawItemImage(helmet);			
			if (itemImage != null) {
				g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
			}
		}
		i++;
		
		//offhand
		ItemStack offhand = inventory.getItem(i);
		if (offhand == null || offhand.getType().equals(Material.AIR)) {
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getItemTexture("empty_armor_slot_shield"), 162, 126, 32, 32, null);
		} else {				
			BufferedImage itemImage = getRawItemImage(offhand);				
			if (itemImage != null) {
				g.drawImage(itemImage, 162, 126, null);
			}
		}
		
		//puppet
		BufferedImage puppet = getFullBodyImage(player);
		g.drawImage(puppet, 71, 28, null);
		
		g.dispose();
		
		return target;
	}
	
	private static BufferedImage getFullBodyImage(Player player) {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(SkinUtils.getSkinJsonFromProfile(player));
			String value = ((String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url")).replace("http://textures.minecraft.net/texture/", "");
			
			BufferedImage image;
			Cache<?> cache = Cache.getCache(player.getUniqueId().toString() + value + FULL_BODY_IMAGE_KEY);
			if (cache == null) {
				String url = "https://mc-heads.net/player/" + value + "/61";
				image = ImageIO.read(new URL(url));
				Cache.putCache(player.getUniqueId().toString() + value + FULL_BODY_IMAGE_KEY, image, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
			} else {
				image = (BufferedImage) cache.getObject();
			}
			return CustomImageUtils.copyImage(image);
		} catch (Throwable e) {
			return InteractiveChatDiscordSrvAddon.plugin.getPuppetTexture("default");
		}
	}
	
	private static BufferedImage getRawItemImage(ItemStack item) throws IOException {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		int amount = item.getAmount();
		XMaterial xMaterial = XMaterial.matchXMaterial(item);
		String key = xMaterial.name().toLowerCase();
		
		BufferedImage itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture(key);
		if (itemImage == null) {
			itemImage = InteractiveChatDiscordSrvAddon.plugin.getBlockTexture(key);
			if (itemImage == null) {
				return null;
			}
		}
		
		if (xMaterial.isOneOf(Arrays.asList("CONTAINS:Banner"))) {
			BufferedImage banner = BannerUtils.generateBannerImage(item);
			
			BufferedImage sizedBanner = new BufferedImage(13, 24, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = sizedBanner.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.drawImage(banner, 0, 0, 13, 24, null);
			g2.dispose();
			
			BufferedImage shearBanner = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g3 = shearBanner.createGraphics();
			g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			AffineTransform t = AffineTransform.getShearInstance(0, 2.5 / 13.0 * -1);
			g3.setTransform(t);
			g3.drawImage(sizedBanner, 0, 3, null);
			g3.dispose();
			
			Graphics2D g4 = itemImage.createGraphics();
			g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g4.drawImage(shearBanner, 10, 2, null);
			g4.dispose();
		} else if (xMaterial.equals(XMaterial.SHIELD)) {
			BufferedImage banner = BannerUtils.generateShieldImage(item);
			itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("shield_banner");
			
			BufferedImage sizedBanner = new BufferedImage(11, 24, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = sizedBanner.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.drawImage(banner, 0, 0, 11, 24, null);
			g2.dispose();
			
			BufferedImage shearBanner = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g3 = shearBanner.createGraphics();
			g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			AffineTransform t = AffineTransform.getShearInstance(1.5 / 24.0 * -1, 2.5 / 11.0);
			g3.setTransform(t);
			g3.drawImage(sizedBanner, 3, 2, null);
			g3.dispose();
			
			Graphics2D g4 = itemImage.createGraphics();
			g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g4.drawImage(shearBanner, 8, 0, null);
			g4.dispose();
		} else if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
			try {
				String base64 = SkullUtils.getSkinValue(item.getItemMeta());
				if (base64 != null) {
					Cache<?> cache = Cache.getCache(base64 + PLAYER_HEAD_KEY);
					if (cache == null) {
						JSONObject json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(base64)));
						String value = ((String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url")).replace("http://textures.minecraft.net/texture/", "");
						String url = "https://mc-heads.net/head/" + value + "/96";
						BufferedImage newSkull = ImageIO.read(new URL(url));
						
						BufferedImage newImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
						Graphics2D g2 = newImage.createGraphics();
						g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
						g2.drawImage(newSkull, 5, 3, 24, 28, null);
						g2.dispose();
						
						Cache.putCache(base64 + PLAYER_HEAD_KEY, newImage, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
						itemImage = CustomImageUtils.copyImage(newImage);
					} else {
						itemImage = CustomImageUtils.copyImage((BufferedImage) cache.getObject());
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (item.getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			Color color = new Color(meta.getColor().asRGB());
			BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.getItemTexture(xMaterial.name().toLowerCase() + "_overlay");
			BufferedImage colorOverlay = CustomImageUtils.changeColorTo(CustomImageUtils.copyImage(itemImage), color);
			
			itemImage = CustomImageUtils.multiply(itemImage, colorOverlay);
			
			Graphics2D g2 = itemImage.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.drawImage(armorOverlay, 0, 0, null);
			g2.dispose();
		} else if (xMaterial.equals(XMaterial.ELYTRA)) {
			int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
			if (durability <= 1) {
				itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("broken_elytra");
			}
		} else if (ItemMapWrapper.isFilledMap(item)) {
			BufferedImage filled = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("filled_map_markings");
			CustomImageUtils.xor(itemImage, filled, 200);
		}
		
		boolean tintedPotion = false;
		if (item.getItemMeta() instanceof PotionMeta) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			PotionType potiontype = InteractiveChat.version.isOld() ? Potion.fromItemStack(item).getType() : meta.getBasePotionData().getType();
			BufferedImage potionOverlay = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("potion_overlay");
			
			Color color;
			try {
				if (meta.hasColor()) {
					color = new Color(meta.getColor().asRGB());
				} else {
					color = PotionUtils.getPotionBaseColor(potiontype);
				}
			} catch (Throwable e) {
				color = PotionUtils.getPotionBaseColor(PotionType.WATER);
			}
			
			BufferedImage colorOverlay = CustomImageUtils.changeColorTo(CustomImageUtils.copyImage(potionOverlay), color);
			potionOverlay = CustomImageUtils.multiply(potionOverlay, colorOverlay);
			
			Graphics2D g2 = itemImage.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.drawImage(potionOverlay, 0, 0, null);
			g2.dispose();
			
			if (potiontype != null) {
				if (!(potiontype.name().equals("WATER") || potiontype.name().equals("AWKWARD") || potiontype.name().equals("MUNDANE") || potiontype.name().equals("THICK") || potiontype.name().equals("UNCRAFTABLE"))) {
					tintedPotion = true;
				}
			}
		}
		
		if (xMaterial.equals(XMaterial.ENCHANTED_GOLDEN_APPLE) || xMaterial.equals(XMaterial.ENCHANTED_BOOK) || item.getEnchantments().size() > 0 || tintedPotion) {
			BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.getMiscTexture("enchanted_item_glint");
			BufferedImage tintImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);				
			Graphics2D g3 = tintImage.createGraphics();
			g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g3.drawImage(tint_ori, 0, 0, 128, 128, null);
			g3.dispose();
			
			itemImage = CustomImageUtils.additionNonTransparent(itemImage, tintImage);
		}
		
		if (item.getType().getMaxDurability() > 0) {
			int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
			int maxDur = item.getType().getMaxDurability();
			double percentage = ((double) durability / (double) maxDur);
			if (percentage < 1) {
				int hue = (int) (125 * percentage);
				int length = (int) (26 * percentage);
				Color color = Color.getHSBColor((float) hue / 360, 1, 1);
				
				Graphics2D g4 = itemImage.createGraphics();
				g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g4.setColor(Color.black);
				g4.fillPolygon(new int[] {4, 30, 30, 4}, new int[] {26, 26, 30, 30}, 4);
				g4.setColor(color);
				g4.fillPolygon(new int[] {4, 4 + length, 4 + length, 4}, new int[] {26, 26, 28, 28}, 4);
				g4.dispose();
			}
		}
		
		if (amount > 1) {
			String tenth = String.valueOf(amount / 10);
			String single = String.valueOf(amount % 10);
			
			BufferedImage firstChar_ori = InteractiveChatDiscordSrvAddon.plugin.getFontTexture(tenth);
			BufferedImage secondChar_ori = InteractiveChatDiscordSrvAddon.plugin.getFontTexture(single);
			
			BufferedImage newItemImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g4 = newItemImage.createGraphics();
			g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g4.drawImage(itemImage, 0, 0, null);
			if (amount > 9) {
				g4.drawImage(CustomImageUtils.darken(CustomImageUtils.copyImage(firstChar_ori), 180), 10, 21, null);
				g4.drawImage(firstChar_ori, 8, 19, null);
			}
			g4.drawImage(CustomImageUtils.darken(CustomImageUtils.copyImage(secondChar_ori), 180), 22, 21, null);
			g4.drawImage(secondChar_ori, 20, 19, null);
			g4.dispose();
			
			itemImage = newItemImage;
		}
		
		return itemImage;
	}
	
	public static BufferedImage getMapImage(ItemStack item) throws Exception {
		if (!ItemMapWrapper.isFilledMap(item)) {
			throw new IllegalArgumentException("Provided item is not a filled map");
		}
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		
		BufferedImage image = new BufferedImage(280, 280, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getGUITexture("map_background"), 0, 0, 280, 280, null);
		g.dispose();
		
		ItemMapWrapper data = new ItemMapWrapper(item);
		for (int widthOffset = 0; widthOffset < 128; widthOffset++) {
			for (int heightOffset = 0; heightOffset < 128; heightOffset++) {
				byte index = data.getColors()[widthOffset + heightOffset * 128];
				if (MapPalette.TRANSPARENT != index) {
					Color color = MapPalette.getColor(index);
					image.setRGB(widthOffset * 2 + 12, heightOffset * 2 + 12, color.getRGB());
					image.setRGB(widthOffset * 2 + 13, heightOffset * 2 + 12, color.getRGB());
					image.setRGB(widthOffset * 2 + 12, heightOffset * 2 + 13, color.getRGB());
					image.setRGB(widthOffset * 2 + 13, heightOffset * 2 + 13, color.getRGB());
				}
			}
		}
		
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		BufferedImage asset = InteractiveChatDiscordSrvAddon.plugin.getGUITexture("map_icons");
		
		for (MapIcon icon : data.getMapIcons()) {
			int x = icon.getX() + 128 + 12;
			int y = icon.getY() + 128 + 12;
			double rotation = (360.0 / 16.0 * (double) icon.getRotation()) + 180.0;
			int type = icon.getType().ordinal();
			
			//String name
			BufferedImage iconImage = CustomImageUtils.copyAndGetSubImage(asset, type % MAP_ICON_PER_ROLE * 16, type / MAP_ICON_PER_ROLE * 16, 16, 16);
			BufferedImage iconImageBig = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g3 = iconImageBig.createGraphics();
			g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g3.drawImage(iconImage, 4, 4, null);
			g3.dispose();
			iconImage = iconImageBig;
			
			BufferedImage iconCan = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
			
			AffineTransform at = new AffineTransform();
            at.rotate(Math.toRadians(rotation), iconImage.getWidth() / 2, iconImage.getHeight() / 2);
            Graphics2D g2d = iconCan.createGraphics();
            g2d.drawImage(iconImage, at, null);
            g2d.dispose();
            
            g2.drawImage(iconCan, x - (iconCan.getWidth() / 2), y - (iconCan.getHeight() / 2), 24, 24, null);
		}
		g2.dispose();
		
		return image;
	}

}
