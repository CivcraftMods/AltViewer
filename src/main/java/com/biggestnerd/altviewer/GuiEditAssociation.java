package com.biggestnerd.altviewer;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;

public class GuiEditAssociation extends GuiScreen {

	private GuiScreen parent;
	private GuiButton addButton;
	private GuiButton deleteButton;
	private GuiButton changeNameButton;
	private ArrayList<String> alts;
	private AltList altListContainer;
	private Association ass;
	private int selected = -1;
	private GuiTextField addAltField;
	private TabComplete complete = null;

	public GuiEditAssociation(Association ass, GuiScreen parent) {
		alts = ass.getAlts();
		this.ass = ass;
		this.parent = parent;
	}
	
	public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(addButton = new GuiButton(1, this.width / 2 + 49, this.height - 63, 50, 20, "Add"));
		this.buttonList.add(deleteButton = new GuiButton(2, this.width / 2 - 100, this.height - 42, 99, 20, "Delete"));
		this.buttonList.add(changeNameButton = new GuiButton(5, this.width / 2 + 1, this.height -42, 99, 20, "Change Name"));
		this.buttonList.add(new GuiButton(100, this.width / 2 - 100, this.height - 21, "Done"));
		this.altListContainer = new AltList(this.mc);
		this.altListContainer.registerScrollButtons(4, 5);
		addAltField = new GuiTextField(1, fontRendererObj, this.width / 2 - 98, this.height - 61, 145, 16);
		deleteButton.enabled = false;
		addButton.enabled = false;
		changeNameButton.enabled = false;
	}
	
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.altListContainer.handleMouseInput();
	}
	
	protected void actionPerformed(GuiButton button) {
		if(button.enabled) {
			if(button.id == 1) {
				ass.addAlt(addAltField.getText().trim());
				AltViewer.getInstance().saveAltList();
				addAltField.setText("");
				complete = null;
			}
			if(button.id == 2) {
				this.alts.remove(selected);
				AltViewer.getInstance().saveAltList();
			}
			if(button.id == 5) {
				
			}
			if(button.id == 100) {
				mc.displayGuiScreen(parent);
			}
		}
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.altListContainer.drawScreen(mouseX, mouseY, partialTicks);
		addAltField.drawTextBox();
		this.drawCenteredString(this.fontRendererObj, ass.getMain() + ": ", this.width / 2, 20, Color.WHITE.getRGB());
		this.drawString(fontRendererObj, AltViewer.onlinePlayers.contains(ass.getMain()) ? "Online" : "Offline", 
						this.width / 2 + (fontRendererObj.getStringWidth(ass.getMain() + ": ") /2), 20, 
						AltViewer.onlinePlayers.contains(ass.getMain()) ? Color.GREEN.getRGB() : Color.RED.getRGB());
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	public void updateScreen() {
		this.alts = ass.getAlts();
		addButton.enabled = addAltField.getText().trim().length() > 0;
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		try {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		addAltField.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	public void keyTyped(char keyChar, int keyCode) {
		if(addAltField.isFocused()) {
			addAltField.textboxKeyTyped(keyChar, keyCode);
			if(keyCode == Keyboard.KEY_RETURN) {
				actionPerformed(addButton);
			}
			if(keyCode == Keyboard.KEY_TAB) {
				if(complete == null) {
					complete = new TabComplete(addAltField.getText().trim());
					complete.addOptions(AltViewer.onlinePlayers);
					complete.removeOptions(ass.getAlts());
				}
				addAltField.setText(complete.nextOption());
			}
		}
		if(keyCode == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(parent);
		}
	}
	
	class AltList extends GuiSlot {

		public AltList(Minecraft mcIn) {
			super(mcIn, GuiEditAssociation.this.width, GuiEditAssociation.this.height, 32, GuiEditAssociation.this.height - 64, 14);
		}
		
		public int getSize() {
			return GuiEditAssociation.this.alts.size();
		}
		
		protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
			GuiEditAssociation.this.selected = slotIndex;
			boolean isValidSlot = slotIndex >= 0 && slotIndex < getSize();
			GuiEditAssociation.this.deleteButton.enabled = isValidSlot;
		}
		
		public boolean isSelected(int slotIndex) {
			return slotIndex == GuiEditAssociation.this.selected;
		}
		
		public int getContentHeight() {
			return getSize() * 14;
		}
		
		public void drawBackground() {
			GuiEditAssociation.this.drawDefaultBackground();
		}
		
		public void drawSlot(int entryId, int x, int y, int par4, int par5, int par6) {
			String alt = GuiEditAssociation.this.alts.get(entryId);
			Association ass = GuiEditAssociation.this.ass;
			GuiEditAssociation.this.drawString(mc.fontRendererObj, alt, x + 1, y + 1, Color.WHITE.getRGB());
			boolean online = ass.getOnlineAccounts().contains(alt);
			GuiEditAssociation.this.drawString(mc.fontRendererObj, online ? "Online" : "Offline", x + 215 - mc.fontRendererObj.getStringWidth("Offline"), y + 1, online ? Color.GREEN.getRGB() : Color.RED.getRGB());
		}
	}
}
