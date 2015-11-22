package com.biggestnerd.altviewer;

import java.util.ArrayList;

public class Association {

	private String main;
	private ArrayList<String> alts;
	
	public Association(String main) {
		this.main = main;
		this.alts = new ArrayList<String>() {
			@Override
			public boolean contains(Object o) {
				if(!(o instanceof String))
					return false;
				String name = (String) o;
				for(String s : this) {
					if(s.equalsIgnoreCase(name)) {
						return true;
					}
				}
				return false;
			}
		};
	}
	
	public String getMain() {
		return main;
	}
	
	public ArrayList<String> getAlts() {
		return alts;
	}
	
	public void addAlt(String username) {
		if(AltViewer.getInstance().getAssociationManager().hasAssociations(username)) {
			return;
		}
		alts.add(username);
	}
	
	public boolean contains(String username) {
		for(String name : alts) {
			if(name.equalsIgnoreCase(username))
				return true;
		}
		return main.equalsIgnoreCase(username);
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Association)) {
			return false;
		}
		Association ass = (Association)o;
		return(ass.getMain().equalsIgnoreCase(main));
	}
	
	public ArrayList<String> getOnlineAccounts() {
		ArrayList<String> online = new ArrayList<String>();
		for(String s : AltViewer.onlinePlayers) {
			if(alts.contains(s) || s.equalsIgnoreCase(main)) {
				online.add(s);
			}
		}
		return online;
	}
	
	public String format() {
		StringBuilder out = new StringBuilder();
		out.append(main).append(",");
		for(String name : alts) {
			out.append(name).append(",");
		}
		String format = out.toString();
		return format.substring(0, format.length() - 1);
	}
}