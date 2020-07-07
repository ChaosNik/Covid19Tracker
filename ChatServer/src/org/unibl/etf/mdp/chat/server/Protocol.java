package org.unibl.etf.mdp.chat.server;

public class Protocol
{
	public static final String SEPARATOR = "#";
	public static final String MESSAGE_TO_CITIZEN = "MESSAGE_TO_CITIZEN" + SEPARATOR;
	public static final String MESSAGE_TO_MEDIC = "MESSAGE_TO_MEDIC" + SEPARATOR;
	public static final String END = "END";
	public static final String LOGIN_CITIZEN = "LOGIN_CITIZEN"  + SEPARATOR;
	public static final String LOGIN_MEDIC = "LOGIN_MEDIC"  + SEPARATOR;
	public static final String START_CHAT = "START_CHAT"  + SEPARATOR;
	public static final String END_CHAT = "END_CHAT"  + SEPARATOR;
	public static final String SEND_N = "SEND_N"  + SEPARATOR;
	public static final String GENERATE_POTENTIAL_CONTACTS = "GENERATE_POTENTIAL_CONTACTS"  + SEPARATOR;
	public static final String OK = "OK";
}
