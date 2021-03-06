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
package com.cfar.swim.worldwind.ai.astar;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

import com.cfar.swim.worldwind.ai.AbstractPlanner;
import com.cfar.swim.worldwind.ai.Planner;
import com.cfar.swim.worldwind.aircraft.Aircraft;
import com.cfar.swim.worldwind.aircraft.Capabilities;
import com.cfar.swim.worldwind.geom.precision.PrecisionPosition;
import com.cfar.swim.worldwind.planning.Environment;
import com.cfar.swim.worldwind.planning.PlanningGrid;
import com.cfar.swim.worldwind.planning.PlanningRoadmap;
import com.cfar.swim.worldwind.planning.Trajectory;
import com.cfar.swim.worldwind.planning.Waypoint;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.Path;

/**
 * Realizes a basic forward A* planner that plans a trajectory of an aircraft
 * in an environment considering a local cost and risk policy.
 * 
 * @author Stephan Heinemann
 *
 */
public class ForwardAStarPlanner extends AbstractPlanner {
	
	/** the priority queue of expandable waypoints */
	private PriorityQueue<Waypoint> open = new PriorityQueue<Waypoint>();
	
	/** the set of expanded waypoints */
	private Set<Waypoint> closed = new HashSet<Waypoint>();
	
	/** the start waypoint */
	private Waypoint start = null;
	
	/** the goal waypoint */
	private Waypoint goal = null;
	
	/** the last computed path */
	private LinkedList<Waypoint> plan = new LinkedList<Waypoint>();
	
	/**
	 * Constructs a basic forward A* planner for a specified aircraft and
	 * environment using default local cost and risk policies.
	 * 
	 * @param aircraft the aircraft
	 * @param environment the environment
	 */
	public ForwardAStarPlanner(Aircraft aircraft, Environment environment) {
		super(aircraft, environment);
	}
	
	/**
	 * Gets a copy of the last computed plan.
	 * 
	 * @return a copy of last computed plan
	 * 
	 * @see Planner#getPlan()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<? extends Waypoint> getPlan() {
		return (List<Waypoint>) this.plan.clone();
	}
	
	/**
	 * Computes a trajectory of specified waypoints.
	 * 
	 * @param waypoint the waypoints
	 * 
	 * @return the trajectory of waypoints
	 */
	protected Trajectory computeTrajectory(Waypoint waypoint) {
		this.plan.clear();
		
		while ((null != waypoint)) {
			this.plan.addFirst(waypoint);
			waypoint = waypoint.getParent();
		}
		
		return new Trajectory(this.getPlan());
	}
	
	/**
	 * Updates the estimated cost of a specified target waypoint when reached
	 * via a specified source waypoint.
	 * 
	 * @param source the source waypoint in globe coordinates
	 * @param target the target waypoint in globe coordinates
	 */
	protected void updateWaypoint(Waypoint source, Waypoint target) {
		double gOld = target.getG();
		this.computeCost(source, target);
		if (target.getG() < gOld) {
			if (!this.open.contains(target)) {
				target.setH(this.getEnvironment().getNormalizedDistance(target, this.goal));
				this.open.add(target);
			} else {
				// priority queue requires re-insertion of modified object
				this.open.remove(target);
				this.open.add(target);
			}
		}
	}
	
	/**
	 * Computes the estimated cost of a specified target waypoint when reached
	 * via a specified source waypoint.
	 * 
	 * @param source the source waypoint in globe coordinates
	 * @param target the target waypoint in globe coordinates
	 */
	protected void computeCost(Waypoint source, Waypoint target) {
		Path leg = new Path(source, target);
		Capabilities capabilities = this.getAircraft().getCapabilities();
		Globe globe = this.getEnvironment().getGlobe();
		// TODO: catch IllegalArgumentException (incapable) and exit
		ZonedDateTime end = capabilities.getEstimatedTime(leg, globe, source.getEto());
		
		double cost = this.getEnvironment().getStepCost(
				source, target,
				source.getEto(), end,
				this.getCostPolicy(), this.getRiskPolicy());
		
		if ((source.getG() + cost) < target.getG()) {
			target.setParent(source);
			target.setG(source.getG() + cost);
			target.setEto(end);
		}
	}
	
	/**
	 * Plans a trajectory from an origin to a destination at a specified
	 * estimated time of departure.
	 * 
	 * @param origin the origin in globe coordinates
	 * @param destination the destination in globe coordinates
	 * @param etd the estimated time of departure
	 * 
	 * @return the planned trajectory from the origin to the destination with
	 *         the estimated time of departure
	 * 
	 * @see Planner#plan(Position, Position, ZonedDateTime)
	 */
	@Override
	public Trajectory plan(Position origin, Position destination, ZonedDateTime etd) {
		this.open.clear();
		this.closed.clear();
		this.plan.clear();
		
		this.start = new Waypoint(origin);
		this.start.setG(0);
		this.start.setH(this.getEnvironment().getNormalizedDistance(origin, destination));
		this.start.setEto(etd);
		
		this.goal = new Waypoint(destination);
		this.goal.setH(0);
		// if a goal is not a waypoint in the environment, then its
		// goal region has to be determined for the final expansion
		Set<PrecisionPosition> goalRegion = this.getEnvironment().getAdjacentWaypoints(destination)
				.stream()
				.map(PrecisionPosition::new)
				.collect(Collectors.toSet());
		
		this.open.add(this.start);
		
		while (null != this.open.peek()) {
			Waypoint source = this.open.poll();
			this.setWaypoint(source);
			if (source.equals(this.goal)) {
				return this.computeTrajectory(source);
			}
			this.closed.add(source);
			
			Set<Position> neighbors = this.getEnvironment().getNeighbors(source);
			// if a start has no neighbors, then it is not a waypoint in the
			// environment and its adjacent waypoints have to be determined for
			// initial expansion
			if (neighbors.isEmpty()) {
				neighbors = this.getEnvironment().getAdjacentWaypoints(source);
			}
			// expand a goal region position towards the goal
			if (goalRegion.contains(source.getPrecisionPosition())) {
				neighbors.add(destination);
			}
			
			for (Position neighbor : neighbors) {
				Waypoint target = new Waypoint(neighbor);
				if (!closed.contains(target)) {
					if (open.contains(target)) {
						Waypoint visited = open.stream().filter(s -> s.equals(target)).findFirst().get();
						this.updateWaypoint(source, visited);
					} else {
						this.updateWaypoint(source, target);
					}
				}
			}
		}
		
		// if no plan could be found, return an empty trajectory
		return new Trajectory();
	}
	
	/**
	 * Plans a trajectory from an origin to a destination along waypoints at a
	 * specified estimated time of departure.
	 * 
	 * @param origin the origin in globe coordinates
	 * @param destination the destination in globe coordinates
	 * @param waypoints the waypoints in globe coordinates
	 * @param etd the estimated time of departure
	 * 
	 * @return the planned trajectory from the origin to the destination along
	 *         the waypoints with the estimated time of departure
	 * 
	 * @see Planner#plan(Position, Position, List, ZonedDateTime)
	 */
	@Override
	public Trajectory plan(Position origin, Position destination, List<Position> waypoints, ZonedDateTime etd) {
		LinkedList<Waypoint> plan = new LinkedList<Waypoint>();
		Waypoint currentOrigin = new Waypoint(origin);
		ZonedDateTime currentEtd = etd;
		
		ArrayList<Waypoint> destinations = waypoints
				.stream().map(Waypoint::new).collect(Collectors.toCollection(ArrayList::new));
		destinations.add(new Waypoint(destination));
		Iterator<Waypoint> destinationsIterator = destinations.iterator();
		
		while (destinationsIterator.hasNext()) {
			Waypoint currentDestination = destinationsIterator.next();
			
			if (!(currentOrigin.equals(currentDestination))) {
				Trajectory part = this.plan(currentOrigin, currentDestination, currentEtd);
				
				for (Waypoint waypoint : part.getWaypoints()) {
					if ((!plan.isEmpty()) &&  (null == waypoint.getParent())) {
						plan.pollLast();
						waypoint.setParent(plan.peekLast());
					}
					plan.add(waypoint);
				}
				
				if (plan.peekLast().equals(currentOrigin)) {
					// if no plan could be found, return an empty trajectory
					this.plan.clear();
					return new Trajectory();
				} else {
					currentOrigin = plan.peekLast();
					currentEtd = currentOrigin.getEto();
				}
			}
		}
		
		this.plan.clear();
		this.plan.addAll(plan);
		return new Trajectory(plan);
	}

	/**
	 * Post-processes a previously expanded waypoint upon its own expansion.
	 * This method is intended to be overridden by lazy evaluation algorithms.
	 * 
	 * @param waypoint the expanded waypoint
	 */
	protected void setWaypoint(Waypoint waypoint) {}
	
	/**
	 * Indicates whether or not this forward A* planner supports a specified
	 * environment.
	 * 
	 * @param environment the environment
	 * 
	 * @return true if the environment is a planning grid or roadmap,
	 *         false otherwise
	 *         
	 * @see PlanningGrid
	 * @see PlanningRoadmap
	 */
	@Override
	public boolean supports(Environment environment) {
		boolean supports = super.supports(environment);
		
		if (supports) {
			supports = (environment instanceof PlanningGrid) ||
					(environment instanceof PlanningRoadmap);
		}
		
		return supports;
	}
	
}
