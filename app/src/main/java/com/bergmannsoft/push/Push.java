package com.bergmannsoft.push;

import android.os.Bundle;

public class Push {

	public static final int TYPE_FEED = 2;
	public static final int TYPE_NOTIFICATION = 3;
	public static final int TYPE_SELECT_PLAYERS = 4;
	public static final int TYPE_BIO = 5;
	public static final int TYPE_CONTEST = 6;
	public static final int TYPE_POST = 7;
	public static final int TYPE_TIMELINE = 100;

	private Bundle bundle;

	public Push(Bundle bundle) {
		super();
		this.bundle = bundle;
	}

	public int getInt(String key) {
		return Integer.parseInt(bundle.getString(key));
	}

	public String getString(String key) {
		return bundle.getString(key);
	}

	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(bundle.getString(key));
	}

	public double getDouble(String key) {
		return Double.parseDouble(bundle.getString(key));
	}

	public long getLong(String key) {
		return Long.parseLong(bundle.getString(key));
	}

	public Bundle getBundle() {
		return bundle;
	}

	@Override
	public String toString() {
		return bundle.toString();
	}

}