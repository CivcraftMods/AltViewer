package com.biggestnerd.altviewer;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class LookupCommand extends CommandBase {

	@Override
	public String getName() {
		return "lookup";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Usage: /lookup <name>";
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0) {
			sendMessage(EnumChatFormatting.RED + getCommandUsage(sender));
			return;
		}
		String name = args[0];
		if(AltViewer.getInstance().getAssociationManager().hasAssociations(name)) {
			Association ass = AltViewer.getInstance().getAssociationManager().getAssociation(name);
			if(ass.getMain().equalsIgnoreCase(name)) {
				StringBuilder altsBuilder = new StringBuilder();
				for(String s : ass.getAlts()) {
					altsBuilder.append(s).append(", ");
				}
				String alts = altsBuilder.toString();
				sendMessage(EnumChatFormatting.GREEN + name + "'s alts are " + alts.substring(0, alts.length() - 2 ));
			} else {
				sendMessage(EnumChatFormatting.GREEN + name + "'s main account is " + ass.getMain());
			}
		} else {
			sendMessage(EnumChatFormatting.YELLOW + name + " is not associated with anyone");
		}
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 1;
	}
	
	@Override
	public boolean canCommandSenderUse(ICommandSender sender) {
		return true;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if(args.length == 0) {
			return AltViewer.getInstance().getAssociationManager().getAllAssociatedNames();
		}
		return AltViewer.getInstance().getAssociationManager().getTabCompleteOptions(args[0]);
	}
	
	private void sendMessage(String message) {
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
	}

}
