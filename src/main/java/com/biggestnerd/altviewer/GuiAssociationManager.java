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

public class GuiAssociationManager extends GuiScreen {

	private final GuiScreen parent;
	private ArrayList<Association> associationList;
	private int selected = -1;
	private GuiButton editButton;
	private GuiButton deleteButton;
	private GuiButton importButton;
	private GuiButton exportButton;
	private AssociationList associationListContainer;
	private GuiTextField searchField;
	private GuiButton searchButton;
	private TabComplete completer = null;
	
	public GuiAssociationManager(GuiScreen parent) {
		this.parent = parent;
	}
	
	public void initGui() { 
		this.buttonList.clear();
		this.buttonList.add(editButton = new GuiButton(2, this.width / 2 - 100, this.height - 42, 49, 20, "Edit"));
		this.buttonList.add(importButton = new GuiButton(1, this.width / 2 - 50, this.height - 42, 49, 20, "Import"));
		this.buttonList.add(exportButton = new GuiButton(4, this.width / 2 + 1, this.height - 42, 49, 20, "Export"));
		this.buttonList.add(deleteButton = new GuiButton(5, this.width / 2 + 51, this.height -42, 49, 20, "Delete"));
		this.buttonList.add(new GuiButton(100, this.width / 2 - 100, this.height - 21, "Done"));
		this.associationListContainer = new AssociationList(this.mc);
		this.associationListContainer.registerScrollButtons(4, 5);
		searchField = new GuiTextField(2, fontRendererObj, this.width / 2 - 98, this.height - 61, 125, 16);
		this.buttonList.add(searchButton = new GuiButton(3, this.width / 2 + 29, this.height - 63, 70, 20, "Search/Add"));
		searchButton.enabled = false;
		editButton.enabled = false;
		deleteButton.enabled = false;
		exportButton.enabled = AltViewer.lastChangeTime > AltViewer.lastExportTime;
		this.associationList = AltViewer.getInstance().getAssociationManager().getAssociations();
	}
	
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.associationListContainer.handleMouseInput();
	}
	
	protected void actionPerformed(GuiButton button) {
		if(button.enabled) {
			if(button.id == 1) {
				mc.displayGuiScreen(new GuiImportAssociations(this));
			}
			if(button.id == 2) {
				mc.displayGuiScreen(new GuiEditAssociation(associationList.get(selected), this));
			}
			if(button.id == 5) {
				AltViewer.getInstance().getAssociationManager().remove(associationList.get(selected));
				AltViewer.getInstance().saveAltList();
			}
			if(button.id == 3) {
				String searchName = searchField.getText().trim();
				if(AltViewer.getInstance().getAssociationManager().hasAssociations(searchName)) {
					Association ass = AltViewer.getInstance().getAssociationManager().getAssociation(searchName);
					mc.displayGuiScreen(new GuiEditAssociation(ass, this));
					completer = null;
				} else {
					Association ass = new Association(searchName);
					AltViewer.getInstance().getAssociationManager().add(ass);
					searchField.setText("");
					AltViewer.getInstance().saveAltList();
					completer = null;
					mc.displayGuiScreen(new GuiEditAssociation(ass, this));
				}
			}
			if(button.id == 4) {
				AltViewer.getInstance().exportAlts();
				AltViewer.lastExportTime = System.currentTimeMillis();
			}
			if(button.id == 100) {
				mc.displayGuiScreen(parent);
			}
		}
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.associationListContainer.drawScreen(mouseX, mouseY, partialTicks);
		searchField.drawTextBox();
        this.drawCenteredString(this.fontRendererObj, "Association List", this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
	
	public void updateScreen() {
		this.associationList = AltViewer.getInstance().getAssociationManager().getAssociations();
		searchButton.enabled = (searchField.getText().trim().length() > 0);
		editButton.enabled = selected >= 0 && selected < associationList.size();
		deleteButton.enabled = selected >= 0 && selected < associationList.size();
		exportButton.enabled = AltViewer.lastChangeTime > AltViewer.lastExportTime;
	}
	
	public void mouseClicked(int x, int y, int button) {
		try {
			super.mouseClicked(x, y, button);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		searchField.mouseClicked(x, y, button);
	}
	
	public void keyTyped(char keyChar, int keyCode) {
		if(searchField.isFocused()) {
			searchField.textboxKeyTyped(keyChar, keyCode);
			if(keyCode == Keyboard.KEY_RETURN) {
				actionPerformed(searchButton);
			}
			if(keyCode == Keyboard.KEY_TAB) {
				if(completer == null) {
					completer = new TabComplete(searchField.getText().trim());
					completer.addOptions(AltViewer.getInstance().getAssociationManager().getAllAssociatedNames());
					completer.addOptions(AltViewer.getInstance().onlinePlayers);
				}
				searchField.setText(completer.nextOption());
			}
		}
		if(keyCode == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(parent);
		}
	}
	
	class AssociationList extends GuiSlot {
		
		public AssociationList(Minecraft mc) {
			super(mc, GuiAssociationManager.this.width, GuiAssociationManager.this.height, 52, GuiAssociationManager.this.height - 64, 26);
		}
		
		protected int getSize() {
			return GuiAssociationManager.this.associationList.size();
		}
		
		protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
			GuiAssociationManager.this.selected = slotIndex;
			boolean isValidSlot = slotIndex >= 0 && slotIndex < getSize();
			if(isDoubleClick && isValidSlot) {
				mc.displayGuiScreen(new GuiEditAssociation(GuiAssociationManager.this.associationList.get(slotIndex), GuiAssociationManager.this));
			}
			GuiAssociationManager.this.editButton.enabled = isValidSlot;
			GuiAssociationManager.this.deleteButton.enabled = isValidSlot;
		}
		
		protected boolean isSelected(int slotIndex) {
			return slotIndex == GuiAssociationManager.this.selected;
		}
		
		protected int getContentHeight() {
			return getSize() * 26;
		}
		
		protected void drawBackground() {
			GuiAssociationManager.this.drawDefaultBackground();
		}
		
		protected void drawSlot(int entryId, int par2, int par3, int par4, int par5, int par6) {
			Association ass = GuiAssociationManager.this.associationList.get(entryId);
			GuiAssociationManager.this.drawString(mc.fontRendererObj, ass.getMain(), par2 + 1, par3 + 1, Color.WHITE.getRGB());
			String onlineAccts = ass.getOnlineAccounts().size() + " online";
			GuiAssociationManager.this.drawString(mc.fontRendererObj, onlineAccts, par2 + 1, par3 + 13, 
					ass.getOnlineAccounts().size() == 0 ? Color.RED.getRGB() : Color.GREEN.getRGB());
			String numAlts = ass.getAlts().size() + " alts";
			GuiAssociationManager.this.drawString(mc.fontRendererObj, numAlts, par2 + 215 - mc.fontRendererObj.getStringWidth(numAlts), par3 + 1,Color.RED.getRGB());
		}
	}
}
