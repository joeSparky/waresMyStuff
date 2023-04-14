package com.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.security.ExceptionCoding;

public class DispatchRunStuff {
	public String runName;
	public String buttonName;
	public ArrayList<DispatchRunStuff> children = null;
	public DispatchRunStuff parent = null;
	private static int id = 0;
	/**
	 * html index for each node
	 */
	public int thisId = 0;

	public DispatchRunStuff(String runName, String buttonName) throws ExceptionCoding {
		this.runName = runName;
		this.buttonName = buttonName;
		thisId = ++id;
		children = new ArrayList<DispatchRunStuff>();
		if (buttonName == null || buttonName.isEmpty())
			throw new ExceptionCoding("empty button name");
	}

	public void addChildren(DispatchRunStuff child) {
		for (DispatchRunStuff thisChild : child.children) {
			thisChild.parent = this;
		}
		child.parent = this;
		this.children.add(child);
		Collections.sort(children, new SearchComparator());
	}

	public class SearchComparator implements Comparator<DispatchRunStuff> {

		@Override
		public int compare(DispatchRunStuff arg0, DispatchRunStuff arg1) {
			return arg0.buttonName.compareTo(arg1.buttonName);
		}
	}

	

	public static Set<String> getAllButtonNames(DispatchRunStuff startingPoint) {
		Set<String> ret = new HashSet<String>();
		ret.add(startingPoint.buttonName);
		for (DispatchRunStuff thisStuff : startingPoint.children)
			ret.addAll(getAllButtonNames(thisStuff));
		return ret;
	}

	public static int countAllNodes(DispatchRunStuff startingPoint) {
		// for the parent
		int soFar = 1;
		for (DispatchRunStuff thisStuff : startingPoint.children)
			soFar += countAllNodes(thisStuff);
		return soFar;
	}

	public static void findDuplicateButtonName(DispatchRunStuff startingPoint, Set<String> soFar) throws Exception {

		if (!soFar.add(startingPoint.buttonName))
			throw new Exception("duplicate button name " + startingPoint.buttonName);
		for (DispatchRunStuff thisStuff : startingPoint.children)
			findDuplicateButtonName(thisStuff, soFar);
	}

	public static void findDuplicateClassName(DispatchRunStuff startingPoint, Set<String> soFar) throws Exception {

		if (startingPoint.runName != null && !startingPoint.runName.isEmpty() && !soFar.add(startingPoint.runName))
			throw new Exception("duplicate class name " + startingPoint.runName);
		for (DispatchRunStuff thisStuff : startingPoint.children)
			findDuplicateClassName(thisStuff, soFar);
	}
//	private static HashMap<String, Integer> goToHash = new HashMap<String, Integer>();
//	public static String getMyGoTo(SmartForm thisClass) {
//		String className = thisClass.getClass().getName();
//		if (goToHash.containsKey(className))
//			return "a" + goToHash.get(className);
//		goToHash.put(className, goToHash.size());
//		return "a" + (goToHash.size() - 1);
//	}
}
