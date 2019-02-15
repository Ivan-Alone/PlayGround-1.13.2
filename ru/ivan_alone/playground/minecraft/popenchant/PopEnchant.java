package ru.ivan_alone.playground.minecraft.popenchant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import ru.ivan_alone.playground.minecraft.config.PGConfig;

public class PopEnchant {
	private PopEnchant() {
		
	}
	
	public static PopEnchant getPopEnchant() {
		if (PopEnchant.INSTANCE == null) {
			PopEnchant.INSTANCE = new PopEnchant();
		}
		return PopEnchant.INSTANCE;
	}
	
	private boolean enabled = true;
	private boolean showBooks = true;
	private static float remainingHighlightTicks;
	private static ItemStack highlightingItemStack;
	private static long ticks;
	private static PopEnchant INSTANCE = null;
	
	private String getEnchantmentTranslation(String ench, int lvl, TextFormatting returnColor) {
		Enchantment e = IRegistry.ENCHANTMENT.get(new ResourceLocation(ench));
		TextFormatting prefColor = returnColor;
		if (e.isCurse()) {
			prefColor = TextFormatting.RED;
		}
		TextComponentTranslation text = new TextComponentTranslation(e.getName(), new Object[0]);
		text.appendText(" ");
		text.appendSibling(new TextComponentTranslation("enchantment.level." + lvl, new Object[0]));

		return prefColor.toString() + text.getFormattedText() + returnColor.toString();
	}
	
	public void render(int screenWidth, int screenHeight) {
		if (!PGConfig.getConfig().getValueBool("a.popenchant.enabled")) return;
		
		Minecraft mc = Minecraft.getInstance();

		if (mc.player != null && (enabled || showBooks)) {
			ItemStack items = mc.player.inventory.getCurrentItem();

			if (items != null && items.hasTag() && (items.getItem().isEnchantable(items) || items.getItem() == Items.ENCHANTED_BOOK || PGConfig.getConfig().getValueBool("a.popenchant.showall"))) {
				int transparency = (int) (remainingHighlightTicks * 256.0F / 10.0F);

				if (transparency > 255) {
					transparency = 255;
				}

				if (transparency > 0) {
					String testString = "";
					String[] displayStrings = { "", "", "", "" };
					TextFormatting color = TextFormatting.AQUA;

					boolean hidden = false;
					int lines = 1;
					int enchantCount = 0;
					int entriesPerLine = 0;

					if (items.getTag().hasUniqueId("HideFlags")) {
						hidden = (items.getTag().getInt("HideFlags") & 1) != 0;
					}
					
					NBTTagList tagList = null;
					if (enabled) {
						tagList = items.getEnchantmentTagList();
					}

					if ((items.getItem() instanceof ItemEnchantedBook) && showBooks) {
						tagList = ItemEnchantedBook.getEnchantments(items);
						color = TextFormatting.YELLOW;
					}

					if (!hidden && tagList != null) {
						FontRenderer fontrenderer = mc.fontRenderer;
						for (int i = 0; i < tagList.size(); ++i) {
							if (fontrenderer.getStringWidth(testString) > screenWidth - 120) {
								lines++;
								testString = "";
							}
							String enchantmentId = tagList.getCompound(i).getString("id");
							short enchantmentLevel = tagList.getCompound(i).getShort("lvl");
							testString = testString + (testString != "" ? ", " : "");
							testString = testString + getEnchantmentTranslation(enchantmentId, enchantmentLevel, color);
						}
						entriesPerLine = (int) Math.ceil(tagList.size() / lines);

						for (int i = 0; i < tagList.size(); ++i) {
							String enchantmentId = tagList.getCompound(i).getString("id");
							short enchantmentLevel = tagList.getCompound(i).getShort("lvl");

							int currentLine = 0;
							if (enchantCount == entriesPerLine) {
								if (displayStrings[3] != "" && fontrenderer.getStringWidth(displayStrings[3]) < screenWidth - 120) {
									currentLine = 3;
								} else if (displayStrings[2] != "" && fontrenderer.getStringWidth(displayStrings[2]) < screenWidth - 120) {
									currentLine = 2;
								} else if (displayStrings[1] != "" && fontrenderer.getStringWidth(displayStrings[1]) < screenWidth - 120) {
									currentLine = 1;
								} else {
									displayStrings[3] = displayStrings[2];
									displayStrings[2] = displayStrings[1];
									displayStrings[1] = displayStrings[0];
									displayStrings[0] = "";
									enchantCount = 0;
								}
							}
							displayStrings[currentLine] += (displayStrings[currentLine] != "" ? ", " : "");
							displayStrings[currentLine] += getEnchantmentTranslation(enchantmentId, enchantmentLevel, color);
							enchantCount++;
							currentLine = 0;
						}

						int[] x = { 0, 0, 0, 0 };
						int[] y = { 0, 0, 0, 0 };
						int j = 0;
						
						if (Minecraft.getInstance().playerController.isNotCreative()) {
							j = 1;
						}
						for (int i = 0; i <= 3; i++) {
							displayStrings[i] = color + displayStrings[i];
							x[i] = (screenWidth - fontrenderer.getStringWidth(displayStrings[i])) / 2;
							y[i] = screenHeight - 59 - (14 * (i + j));
						}

						GlStateManager.pushMatrix();
						GlStateManager.enableBlend();
						GlStateManager.blendFuncSeparate(770, 771, 1, 0);
						for (int i = 0; i <= 3; i++) {
							fontrenderer.drawStringWithShadow(displayStrings[i], (float) x[i], (float) y[i], 0xFFFFFF + (transparency << 24));
						}
						GlStateManager.disableBlend();
						GlStateManager.popMatrix();
					}
				}
			}
		}
	}

	public void tick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
		if (inGame && minecraft.player != null && clock) {
			ItemStack items = minecraft.player.inventory.getCurrentItem();

			if (items == null) {
				remainingHighlightTicks = 0.0F;
			} else if (highlightingItemStack != null && items.getItem() == highlightingItemStack.getItem()
					&& ItemStack.areItemStackTagsEqual(items, highlightingItemStack)
					&& (items.isDamageable() || items.getDamage() == highlightingItemStack.getDamage())) {
				if (remainingHighlightTicks > 0.0F) {
					remainingHighlightTicks = (float) (40L - (System.currentTimeMillis() - ticks) / 50L);
				}
			} else {
				remainingHighlightTicks = 40.0F;
				ticks = System.currentTimeMillis();
			}

			highlightingItemStack = items;
		}
	}

}
