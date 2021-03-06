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
package com.projectlibre1.pm.graphic.graph;

import java.awt.geom.GeneralPath;

/**
 *
 */
public abstract class LinkRouting {

	protected float lx0,lx1,lx2,ly0,ly1,ly2,fx0,fx1,fy0,fy1;
	protected int nbPoints;
	protected GeneralPath path;
	protected void addLinkPoint(double x,double y){
		addLinkPoint(x,y,true);
	}
	protected void addLinkPoint(double x,double y,boolean line){
		lx2=lx1;
		ly2=ly1;
		lx1=lx0;
		ly1=ly0;
		lx0=(int)Math.round(x);
		ly0=(int)Math.round(y);
		if (nbPoints==0){
			fx0=lx0;
			fy0=ly0;
			path.moveTo(lx0,ly0);
		}else{
			if (nbPoints==1){
				fx1=lx0;
				fy1=ly0;
			}
			if (line)path.lineTo(lx0,ly0);
		}
		nbPoints++;
	}
	protected void resetLinkPoints(){
		nbPoints=0;
		lx0=-1;
		lx1=-1;
		ly0=-1;
		ly1=-1;
		fx0=-1;
		fx1=-1;
		fy0=-1;
		fy1=-1;
		path.reset();
	}
	protected void line(){
		path.lineTo(lx0,ly0);
	}
	protected void quad(){
		path.quadTo(lx1,ly1,lx0,ly0);
	}
	protected void curve(){
		path.curveTo(lx2,ly2,lx1,ly1,lx0,ly0);
	}
	
	//public abstract void routePath(GeneralPath path,double x0,double y0,double x1,double y1,double[] extraPoints, int type);
	
	
	
	public float getFirstX() {
		return fx0;
	}
	public float getFirstY() {
		return fy0;
	}
	public float getLastX() {
		return lx0;
	}
	public float getLastY() {
		return ly0;
	}
	
	public double getFirstAngle() {
		return Math.atan2(fy1-fy0,fx0-fx1);
	}
	public double getLastAngle() {
		return Math.atan2(ly1-ly0,lx0-lx1);
	}
}
