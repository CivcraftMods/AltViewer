package com.biggestnerd.altviewer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AssociationManager {

	private ArrayList<Association> associations;
	
	public AssociationManager() {
		associations = new ArrayList<Association>();
	}
	
	public static AssociationManager load(File associationFile) {
		try {
			Gson gson = new Gson();
			return (AssociationManager)gson.fromJson(new FileReader(associationFile), AssociationManager.class);
		} catch (Exception ex) {
			
		}
		return new AssociationManager();
	}
	
	public void save(File associationFile) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			String json = gson.toJson(this);

			FileWriter writer = new FileWriter(associationFile);
			writer.write(json);
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean hasAssociations(String username) {
		return getAssociation(username) != null;
	}
	
	public ArrayList<Association> getAssociations() {
		return associations;
	}
	
	public Association getAssociation(String username) {
		for(Association ass : associations) {
			if(ass.contains(username)) {
				return ass;
			}
		}
		return null;
	}
	
	public boolean areAssociated(String name1, String name2) {
		if(hasAssociations(name1) && hasAssociations(name2)) {
			Association ass1 = getAssociation(name1);
			Association ass2 = getAssociation(name2);
			if(ass1.getMain().equals(ass2.getMain())) {
				return true;
			}
		}
		return false;
	}
	
	public void add(Association ass) {
		synchronized(associations) {
			if(hasAssociations(ass.getMain())) {
				return;
			} else {
				associations.add(ass);
			}
		}
	}
	
	public void remove(Association ass) {
		synchronized(associations) {
			for(Association a : associations) {
				if(a.getMain().equalsIgnoreCase(ass.getMain())) {
					associations.remove(a);
				}
			}
		}
	}

	public ArrayList<String> getAllAssociatedNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(Association ass : associations) {
			names.add(ass.getMain());
			names.addAll(ass.getAlts());
		}
		return names;
	}
	
	public ArrayList<String> getTabCompleteOptions(String arg) {
		arg = arg.toLowerCase();
		ArrayList<String> names = new ArrayList<String>();
		for(String name : getAllAssociatedNames()) {
			if(name.toLowerCase().startsWith(arg)) {
				names.add(name);
			}
		}
		return names;
	}
}