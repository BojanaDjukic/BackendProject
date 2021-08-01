package com.IKTpreobuka.restExample.egradebook.security;

public class Views {
	public static class Public{}
	public static class Private extends Public{} //ucenik i roditelj
	public static class Teacher extends Private{}
	public static class Admin extends Private {}
}
