package com.biggestnerd.altviewer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

@Mod(modid="altviewer", name="Alt Viewer", version="v2.0")
public class AltViewer {

	private Minecraft mc;
	private static AltViewer instance;
	private AssociationManager assMan;
	private AssociationListener assListener;
	public static ArrayList<String> onlinePlayers;
	private File altsFile;
	private File dir;
	public static KeyBinding bind;
	public static long lastExportTime = 0;
	public static long lastChangeTime = 1;
	public static boolean nameScaling;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		mc = Minecraft.getMinecraft();
		instance = this;
		assListener = new AssociationListener(this);
		FMLCommonHandler.instance().bus().register(assListener);
		MinecraftForge.EVENT_BUS.register(assListener);
		ClientCommandHandler.instance.registerCommand(new LookupCommand());
		
		dir = new File(mc.mcDataDir, "AltViewer");
		if(!dir.isDirectory()) {
			dir.mkdir();
		}
		altsFile = new File(dir, "association.json");
		if(!altsFile.isFile()) {
			try {
				altsFile.createNewFile();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			assMan = new AssociationManager();
			assMan.save(altsFile);
		} else {
			assMan = AssociationManager.load(altsFile);
			if(assMan == null) {
				assMan = new AssociationManager();
			}
			assMan.save(altsFile);
		}
		bind = new KeyBinding("Manage Associations", Keyboard.KEY_U, "AltViewer");
		ClientRegistry.registerKeyBinding(bind);
	}
	
	public static AltViewer getInstance() {
		return instance;
	}
	
	public AssociationManager getAssociationManager() {
		return assMan;
	}
	
	public void exportAlts() {
		File altCSV = new File(dir, "tulpas.csv");
		try {
			altCSV.createNewFile();
			PrintWriter pw = new PrintWriter(new FileWriter(altCSV));
			for(Association ass : assMan.getAssociations()) {
				pw.println(ass.format());
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveAltList() {
		lastChangeTime = System.currentTimeMillis();
		assMan.save(altsFile);
	}
}
