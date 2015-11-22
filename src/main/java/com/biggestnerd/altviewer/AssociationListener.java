package com.biggestnerd.altviewer;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.discovery.ModDiscoverer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class AssociationListener {

	private Minecraft mc;
	
	public AssociationListener(AltViewer instance) {
		mc = Minecraft.getMinecraft();
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(mc.theWorld != null) {
			ArrayList<String> newOnlinePlayers = new ArrayList<String>();
			for(Object o : mc.getNetHandler().func_175106_d()) {
				if(o instanceof NetworkPlayerInfo) {
					NetworkPlayerInfo info = (NetworkPlayerInfo)o;
					newOnlinePlayers.add(info.getGameProfile().getName());
				}
			}
			AltViewer.onlinePlayers = (ArrayList) newOnlinePlayers.clone();
		}	
	}
	
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if(AltViewer.bind.isKeyDown()) {
			mc.displayGuiScreen(new GuiAssociationManager(mc.currentScreen));
		}
	}
}
