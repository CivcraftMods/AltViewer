package com.biggestnerd.altviewer;

import java.util.ArrayList;
import java.util.List;

public class TabComplete {

	private String startString;
	private List<String> possibleOptions;
	int index = -1;
	
	public TabComplete(String startString) {
		this.startString = startString;
		possibleOptions = new ArrayList<String>();
	}
	
	public void addOptions(List<String> options) {
		for(String s : options) {
			if(s.toLowerCase().startsWith(startString.toLowerCase()) && !possibleOptions.contains(s)) {
				possibleOptions.add(s);
			}
		}
	}
	
	public void removeOptions(List<String> options) {
		possibleOptions.removeAll(options);
	}
	
	public String nextOption() {
		if(possibleOptions.size() == 0 || possibleOptions == null) {
			return startString;
		}
		if(index == possibleOptions.size() - 1) {
			index = 0;
		} else {
			index++;
		}
		return possibleOptions.get(index);
	}
}
