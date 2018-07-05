/**
 * Copyright (c) 2018, Henrique Ferreira (UVic Center for Aerospace Research)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.cfar.swim.worldwind.ai.prm.fadprm;

import com.cfar.swim.worldwind.ai.prm.faprm.FAPRMWaypoint;

import gov.nasa.worldwind.geom.Position;

/**
 * Realizes a FADPRM waypoint of a trajectory featuring estimates for costs, a
 * list of neighbors, the parameter beta and density. Also it stores the number
 * of the last search that affected this waypoint.
 * 
 * @author Henrique Ferreira
 *
 */
public class FADPRMWaypoint extends FAPRMWaypoint {

	/** the number of the last search that generated this waypoint */
	private int search;

	/**
	 * Constructs a FADPRM waypoint at a specified position.
	 * 
	 * @param position the position in globe coordinates
	 */
	public FADPRMWaypoint(Position position) {
		super(position);
		this.setCost(Double.POSITIVE_INFINITY);
		this.setDistanceToGoal(Double.POSITIVE_INFINITY);
	}

	/**
	 * Gets the number of the last search that generated this FAPRM waypoint.
	 * 
	 * @return the search the number of the last search
	 */
	public int getSearch() {
		return search;
	}

	/**
	 * Sets the number of the last search that generated this FAPRM waypoint.
	 * 
	 * @param search the search to set
	 */
	public void setSearch(int search) {
		this.search = search;
	}

	/**
	 * Gets the parent FAPRM waypoint of this FAPRM waypoint.
	 * 
	 * @return the parent FAPRM waypoint of this FAPRM waypoint
	 */
	public FADPRMWaypoint getParent() {
		return (FADPRMWaypoint) super.getParent();
	}
}
