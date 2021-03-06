/*******************************************************************************
 * The contents of this file are subject to the Common Public Attribution License 
 * Version 1.0 (the "License"); you may not use this file except in compliance with 
 * the License. You may obtain a copy of the License at 
 * http://www.projectlibre.com/license . The License is based on the Mozilla Public 
 * License Version 1.1 but Sections 14 and 15 have been added to cover use of 
 * software over a computer network and provide for limited attribution for the 
 * Original Developer. In addition, Exhibit A has been modified to be consistent 
 * with Exhibit B. 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the 
 * specific language governing rights and limitations under the License. The 
 * Original Code is ProjectLibre. The Original Developer is the Initial Developer 
 * and is ProjectLibre Inc. All portions of the code written by ProjectLibre are 
 * Copyright (c) 2012-2019. All Rights Reserved. All portions of the code written by 
 * ProjectLibre are Copyright (c) 2012-2019. All Rights Reserved. Contributor 
 * ProjectLibre, Inc.
 *
 * Alternatively, the contents of this file may be used under the terms of the 
 * ProjectLibre End-User License Agreement (the ProjectLibre License) in which case 
 * the provisions of the ProjectLibre License are applicable instead of those above. 
 * If you wish to allow use of your version of this file only under the terms of the 
 * ProjectLibre License and not to allow others to use your version of this file 
 * under the CPAL, indicate your decision by deleting the provisions above and 
 * replace them with the notice and other provisions required by the ProjectLibre 
 * License. If you do not delete the provisions above, a recipient may use your 
 * version of this file under either the CPAL or the ProjectLibre Licenses. 
 *
 *
 * [NOTE: The text of this Exhibit A may differ slightly from the text of the notices 
 * in the Source Code files of the Original Code. You should use the text of this 
 * Exhibit A rather than the text found in the Original Code Source Code for Your 
 * Modifications.] 
 *
 * EXHIBIT B. Attribution Information for ProjectLibre required
 *
 * Attribution Copyright Notice: Copyright (c) 2012-2019, ProjectLibre, Inc.
 * Attribution Phrase (not exceeding 10 words): 
 * ProjectLibre, open source project management software.
 * Attribution URL: http://www.projectlibre.com
 * Graphic Image as provided in the Covered Code as file: projectlibre-logo.png with 
 * alternatives listed on http://www.projectlibre.com/logo 
 *
 * Display of Attribution Information is required in Larger Works which are defined 
 * in the CPAL as a work which combines Covered Code or portions thereof with code 
 * not governed by the terms of the CPAL. However, in addition to the other notice 
 * obligations, all copies of the Covered Code in Executable and Source Code form 
 * distributed must, as a form of attribution of the original author, include on 
 * each user interface screen the "ProjectLibre" logo visible to all users. 
 * The ProjectLibre logo should be located horizontally aligned with the menu bar 
 * and left justified on the top left of the screen adjacent to the File menu. The 
 * logo must be at least 144 x 31 pixels. When users click on the "ProjectLibre" 
 * logo it must direct them back to http://www.projectlibre.com. 
 *******************************************************************************/
package com.projectlibre1.configuration;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

import org.apache.commons.digester.Digester;

import com.projectlibre1.graphic.configuration.ActionLists;
import com.projectlibre1.graphic.configuration.BarFormat;
import com.projectlibre1.graphic.configuration.BarStyle;
import com.projectlibre1.graphic.configuration.BarStyles;
import com.projectlibre1.graphic.configuration.CellStyles;
import com.projectlibre1.graphic.configuration.SpreadSheetFieldArray;
import com.projectlibre1.grouping.core.transform.TransformList;
import com.projectlibre1.grouping.core.transform.ViewConfiguration;
import com.projectlibre1.pm.calendar.WorkCalendar;
import com.projectlibre1.script.ContextStore;
import com.projectlibre1.scripting.FormulaFactory;
import com.projectlibre1.strings.Messages;
import com.projectlibre1.util.ClassUtils;
import com.projectlibre1.util.Environment;

/**
 * A hash table of hashtables which allows finding an object by category and
 * name. Used with parsed xml view config
 */
public class Dictionary implements ProvidesDigesterEvents {
	private static Dictionary instance = null;
	public static synchronized Dictionary getInstance() {
		if (instance == null) {
			instance = new Dictionary();
			String [] files = Messages.getMetaString("DictionaryFiles").split(";");
			for (String file : files)
				ConfigurationReader.read(file, instance) ;
			if (Environment.isClientSide()) // this screws up on server and is not needed anyway
 				precompileCommonFormulas();


		}
		return instance;
	}

	public Dictionary() {
	}

	private Hashtable mainMap = new Hashtable();

	public static void add(NamedItem namedItem) {
		add(namedItem,false);
	}
	public static void add(NamedItem namedItem, boolean replace) {
		String categories[] = namedItem.getCategory().split(";"); // can belong to more than one if separated by ;

		for (int i = 0; i < categories.length; i++) {
			String category = categories[i];
			Hashtable subMap = (Hashtable) getInstance().mainMap.get(category);
			if (subMap == null) {
				subMap = new Hashtable();
				getInstance().mainMap.put(category, subMap);
			}
			if (!subMap.contains(namedItem)) {
				subMap.put(namedItem.getName(), namedItem);
			} else {
				if (replace)
					subMap.put(namedItem.getName(), namedItem);

//this is actually normal if overriding with another xml file				ConfigurationReader.log.warn("named item " + namedItem + " already in category " + category);
			}
		}
	}

	public static void remove(NamedItem namedItem) {
		String categories[] = namedItem.getCategory().split(";"); // can belong to more than one if separated by ;

		for (int i = 0; i < categories.length; i++) {
			String category = categories[i];
			Hashtable subMap = (Hashtable) getInstance().mainMap.get(category);
			subMap.remove(namedItem.getName());
		}

	}
	public static NamedItem get(Object category, String name) {
		Hashtable subMap = (Hashtable) getInstance().mainMap.get(category);
		if (subMap == null)
			return null;
		return (NamedItem) subMap.get(name);
	}

	public static Object[] getAll(Object category) {
		Hashtable subMap = (Hashtable) getInstance().mainMap.get(category);
		Object[] array = subMap.values().toArray();
		Arrays.sort(array,namedItemComparator);
		return array;

	}

	public static Object[] allCalendars() {
		return getAll(WorkCalendar.CALENDAR_CATEGORY);
	}

	public static WorkCalendar findCalendar(String name) {
		if (name == null)
			return null;
		return (WorkCalendar) get(WorkCalendar.CALENDAR_CATEGORY,name);
	}

	/* (non-Javadoc)
	 * @see com.projectlibre1.configuration.ProvidesDigesterEvents#addDigesterEvents(org.apache.commons.digester.Digester)
	 */
	public void addDigesterEvents(Digester digester) {
		SpreadSheetFieldArray.addDigesterEvents(digester);
		BarFormat.addDigesterEvents(digester);
		BarStyles.addDigesterEvents(digester);
		TransformList.addDigesterEvents(digester);
		CellStyles.addDigesterEvents(digester);
		ActionLists.addDigesterEvents(digester);
		ViewConfiguration.addDigesterEvents(digester);
		ReportDefinition.addDigesterEvents(digester);
		ContextStore.addDigesterEvents(digester);
		ChartDefinition.addDigesterEvents(digester);
	}

	private static Comparator namedItemComparator = new NamedItemComparator();
	private static class NamedItemComparator implements Comparator {
		public int compare(Object first, Object second) {
			return ((NamedItem)first).getName().compareTo(((NamedItem)second).getName());
		}
	}
	private static void precompileCommonFormulas() {
		FormulaFactory.precompileClass(BarStyle.FORMULA_PREFIX+Messages.getString("Styles.Bar.standard"));
	}

	public static String generateUniqueName(NamedItem namedItem) {
		String name = namedItem.getName();
		while (Dictionary.get(namedItem.getCategory(),name) != null)
			name += "*";
		return name;
	}

	public static void rename(NamedItem namedItem, String newName) {
		remove(namedItem);
		ClassUtils.setSimpleProperty(namedItem,"name",newName); // call setName if any
		add(namedItem);
	}
	public static String getCategoryText(String category) {
		return Messages.getString("Category."+category);
	}
}
