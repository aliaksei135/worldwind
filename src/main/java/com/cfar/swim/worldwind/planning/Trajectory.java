/**
 * Copyright (c) 2016, Stephan Heinemann (UVic Center for Aerospace Research)
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
package com.cfar.swim.worldwind.planning;

import com.cfar.swim.worldwind.util.Depictable;
import com.cfar.swim.worldwind.util.Depiction;

import gov.nasa.worldwind.Movable;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Path;

/**
 * Realizes a trajectory of waypoints which represents a path in both
 * space and time.
 * 
 * @author Stephan Heinemann
 *
 */
public class Trajectory extends Path implements Depictable {

	/** the depiction of this trajectory */
	private Depiction depiction = null;
	
	/**
	 * Constructs an empty trajectory.
	 * 
	 * @see Path#Path()
	 */
	public Trajectory() {
		super();
	}
	
	/**
	 * Constructs a trajectory between two specified waypoints.
	 * 
	 * @param waypointA the first waypoint
	 * @param waypointB the second waypoint
	 * 
	 * @see Path#Path(gov.nasa.worldwind.geom.Position, gov.nasa.worldwind.geom.Position)
	 */
	public Trajectory(Waypoint waypointA, Waypoint waypointB) {
		super(waypointA, waypointB);
	}
	
	/**
	 * Constructs a trajectory along a series of waypoints.
	 * 
	 * @param waypoints the waypoints
	 * 
	 * @see Path#Path(Iterable)
	 */
	public Trajectory(Iterable<? extends Waypoint> waypoints) {
		super(waypoints);
	}
	
	/**
	 * Moves this trajectory by adding a specified position.
	 * 
	 * @param position the position to be added to this trajectory's position
	 * 
	 * @see Movable#move(Position)
	 */
	@Override
	public void move(Position position) {
		super.move(position);
		if (this.hasDepiction()) {
			this.depiction.move(position);
		}
	}
	
	/**
	 * Moves this trajectory to a specified position.
	 * 
	 * @param position the position to move this trajectory to
	 * 
	 * @see Movable#moveTo(Position)
	 */
	@Override
	public void moveTo(Position position) {
		super.moveTo(position);
		if (this.hasDepiction()) {
			this.depiction.moveTo(position);
		}
	}
	
	/**
	 * Gets the waypoints of this trajectory.
	 * 
	 * @return the waypoints of this trajectory
	 * 
	 * @see Path#getPositions()
	 */
	@SuppressWarnings("unchecked")
	public Iterable<? extends Waypoint> getWaypoints() {
		return (Iterable<Waypoint>) super.getPositions();
	}
	
	/**
	 * Gets the depiction of this trajectory.
	 * 
	 * @return the depiction of this trajectory
	 * 
	 * @see Depictable#getDepiction()
	 */
	@Override
	public Depiction getDepiction() {
		return this.depiction;
	}
	
	/**
	 * Sets the depiction of this trajectory.
	 * 
	 * @param depiction the depiction to be set
	 * 
	 * @see Depictable#setDepiction(Depiction)
	 */
	@Override
	public void setDepiction(Depiction depiction) {
		this.depiction = depiction;
	}
	
	/**
	 * Indicates whether or not this trajectory has a depiction.
	 * 
	 * @return true if this trajectory has a depiction, false otherwise
	 * 
	 * @see Depictable#hasDepiction()
	 */
	@Override
	public boolean hasDepiction() {
		return (null != this.depiction);
	}
	
	/**
	 * Renders this trajectory.
	 * 
	 * @param dc the drawing context
	 * 
	 * @see Renderable#render
	 */
	@Override
	public void render(DrawContext dc) {
		super.render(dc);
		if (null != this.depiction) {
			this.depiction.render(dc);
		}
		if (null != this.getWaypoints()) {
			for (Waypoint waypoint : this.getWaypoints()) {
				waypoint.render(dc);
			}
		}
	}
	
}
