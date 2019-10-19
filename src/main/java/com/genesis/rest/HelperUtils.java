package com.genesis.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelperUtils {

	final public static SimpleDateFormat printableDateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	final public static SimpleDateFormat jsonDateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	public static String printableDate(Date date) {
		return printableDateFormatter.format(date);
	}

	public static Date toUtilDate(String source) {
		try {
			return jsonDateFormatter.parse(source);
		} catch (ParseException e) {

			return new Date();
		}
	}

}
