package com.biggestnerd.altviewer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiImportAssociations extends GuiScreen {

	private final GuiScreen parent;
	private GuiButton importButton;
	private GuiTextField importField;
	
	public GuiImportAssociations(GuiScreen parent) {
		this.parent = parent;
	}
	
	public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(importButton = new GuiButton(1, this.width / 2 - 100, this.height /2 - 25, "Import"));
		importField = new GuiTextField(0, fontRendererObj, this.width / 2 - 100, this.height /2 - 50, 200, 20);
		importButton.enabled = false;
		importField.setMaxStringLength(256);
		importField.setFocused(true);
	}
	
	public void actionPerformed(GuiButton button) {
		if(button.enabled) {
			if(button.id == 1) {
				new ImportThread().start();
			}
		}
	}
	
	public void keyTyped(char keyChar, int keyCode) {
		if(keyCode == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(parent);
		}
		if(keyCode == Keyboard.KEY_RETURN) {
			actionPerformed(importButton);
		}
		if(importField.isFocused()) {
			importField.textboxKeyTyped(keyChar, keyCode);
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		importField.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	public void updateScreen() {
		importButton.enabled = importField.getText().trim().length() > 0;
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		importField.drawTextBox();
		drawCenteredString(fontRendererObj, "Import Alt List", this.width / 2, 20, Color.WHITE.getRGB());
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	class ImportThread extends Thread {
		@Override
		public void run() {
			String importUrl = importField.getText().trim();
			try {
				URL url = new URL(importUrl);
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				
				String line = "";
				while((line = reader.readLine()) != null) {
					line = line.trim();
					if(line.isEmpty())
						continue;
					String[] names = line.split(",");
					if(AltViewer.getInstance().getAssociationManager().hasAssociations(names[0])) {
						Association ass = AltViewer.getInstance().getAssociationManager().getAssociation(names[0]);
						for(int i = 1; i < names.length; i++) {
							ass.addAlt(names[i]);
						}
					} else {
						Association ass = new Association(names[0]);
						for(int i = 1; i < names.length; i++) {
							ass.addAlt(names[i]);
						}
						AltViewer.getInstance().getAssociationManager().add(ass);
					}
				}
				AltViewer.getInstance().saveAltList();
			} catch (Exception ex) {
				ex.printStackTrace();
				AltViewer.getInstance().saveAltList();
			}
			mc.displayGuiScreen(parent);
		}
	}
}
