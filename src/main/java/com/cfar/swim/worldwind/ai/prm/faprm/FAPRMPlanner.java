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
package com.cfar.swim.worldwind.ai.prm.faprm;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.cfar.swim.worldwind.ai.AbstractPlanner;
import com.cfar.swim.worldwind.ai.AnytimePlanner;
import com.cfar.swim.worldwind.ai.OnlinePlanner;
import com.cfar.swim.worldwind.ai.Planner;
import com.cfar.swim.worldwind.aircraft.Aircraft;
import com.cfar.swim.worldwind.aircraft.Capabilities;
import com.cfar.swim.worldwind.connections.Datalink;
import com.cfar.swim.worldwind.geom.Box;
import com.cfar.swim.worldwind.planning.DesirabilityZone;
import com.cfar.swim.worldwind.planning.Edge;
import com.cfar.swim.worldwind.planning.Environment;
import com.cfar.swim.worldwind.planning.FAPRMEdge;
import com.cfar.swim.worldwind.planning.SamplingEnvironment;
import com.cfar.swim.worldwind.planning.Trajectory;
import com.cfar.swim.worldwind.planning.Waypoint;

import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.Path;

/**
 * Realizes a Flexible Anytime Probabilistic Roadmap planner (FAPRM) that plans
 * a trajectory of an aircraft in an environment considering a local cost and
 * risk policy.
 * 
 * @author Henrique Ferreira
 *
 */
public class FAPRMPlanner extends AbstractPlanner implements AnytimePlanner, OnlinePlanner {

	/** the maximum number of neighbors a waypoint can be connected to */
	private int maxNeighbors;

	/** the maximum distance between two neighboring waypoints */
	private double maxDistance;

	/** the bias of the sampling algorithm towards goal */
	private int bias;

	/** the priority queue of expandable FAPRM waypoints */
	protected PriorityQueue<FAPRMWaypoint> open = new PriorityQueue<>();

	/** the set of expanded FAPRM waypoints */
	protected Set<FAPRMWaypoint> closed = new HashSet<>();

	/** the start FAPRM waypoint */
	private FAPRMWaypoint start = null;

	/** the goal FAPRM waypoint */
	private FAPRMWaypoint goal = null;

	/** the last computed plan */
	protected final LinkedList<FAPRMWaypoint> plan = new LinkedList<>();

	/** the initial beta factor used to sort the open list */
	private double initialBeta;

	/** the final beta factor used to sort the open list */
	private double finalBeta;

	/** the current beta factor used to sort the open list */
	private double beta;

	/** the increase amount to be applied to the current beta */
	private double stepBeta;

	/** the desirability zones built by the user */
	private ArrayList<DesirabilityZone> desirabilityZones = new ArrayList<DesirabilityZone>();

	/** the parameter lambda that weights the desirability influence on the cost */
	private double lambda;

	/**
	 * Constructs a FAPRM planner for a specified aircraft and environment using
	 * default local cost and risk policies.
	 * 
	 * @param aircraft the aircraft
	 * @param environment the environment
	 * 
	 * @see AbstractPlanner#AbstractPlanner(Aircraft, Environment)
	 */
	public FAPRMPlanner(Aircraft aircraft, Environment environment) {
		super(aircraft, environment);
		maxNeighbors = 15;
		maxDistance = 400d;
		bias = 5;
		initialBeta = 0d;
		finalBeta = 1d;
		stepBeta = 0.1;
		lambda = 0.5;
		onlineStatus = false;
		positionThreshold = 2d;
	}

	/**
	 * Gets the maximum number of neighbors a waypoint can have
	 * 
	 * @return the maximum number of neighbors a waypoint can have
	 */
	public int getMaxNeighbors() {
		return maxNeighbors;
	}

	/**
	 * Sets the maximum number of neighbors a waypoint can have
	 * 
	 * @param maxNeighbors the maximum number of neighbors a waypoint can have
	 */
	public void setMaxNeighbors(int maxNeighbors) {
		this.maxNeighbors = maxNeighbors;
	}

	/**
	 * Gets the maximum distance between two connected waypoints
	 * 
	 * @return the maximum distance between two connected waypoints
	 */
	public double getMaxDistance() {
		return maxDistance;
	}

	/**
	 * Sets the maximum distance between two connected waypoints
	 * 
	 * @param maxDistance maximum distance between two connected waypoints
	 */
	public void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}

	/**
	 * Gets the bias of sampling towards the goal of this FA PRM planner.
	 * 
	 * @return the bias of sampling towards the goal
	 */
	public int getBias() {
		return bias;
	}

	/**
	 * Sets the bias of sampling towards the goal of this FA PRM planner.
	 * 
	 * @param the bias of sampling towards the goal
	 */
	public void setBias(int bias) {
		this.bias = bias;
	}

	/**
	 * Gets the sampling environment of this planner
	 * 
	 * @return the sampling environment
	 */
	public SamplingEnvironment getEnvironment() {
		return (SamplingEnvironment) super.getEnvironment();
	}

	/**
	 * Gets the list of already sampled FAPRM waypoints
	 * 
	 * @return the list of waypoints
	 */
	@SuppressWarnings("unchecked")
	public List<? extends FAPRMWaypoint> getWaypointList() {
		return (List<FAPRMWaypoint>) this.getEnvironment().getWaypointList();
	}

	/**
	 * Sets the list of waypoints previously sampled
	 * 
	 * @param waypointList the list of waypoints to set
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void setWaypointList(List<? extends Waypoint> waypointList) {
		this.getEnvironment().setWaypointList((List<Waypoint>) waypointList);
	}

	/**
	 * Gets the list of already created FAPRM edges
	 * 
	 * @return the list of edges
	 */
	@SuppressWarnings("unchecked")
	public List<FAPRMEdge> getEdgeList() {
		return (List<FAPRMEdge>) this.getEnvironment().getEdgeList();
	}

	/**
	 * Sets the list of edges previously sampled
	 * 
	 * @param edgetList the list of edges to set
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void setEdgeList(List<? extends Edge> edgeList) {
		this.getEnvironment().setEdgeList((List<Edge>) edgeList);
	}

	/**
	 * Gets the start FAPRM waypoint of this FAPRM planner.
	 * 
	 * @return the start FAPRM waypoint of this FAPRM planner
	 */
	protected FAPRMWaypoint getStart() {
		return this.start;
	}

	/**
	 * Sets the start FAPRM waypoint of this FAPRM planner.
	 * 
	 * @param start the start waypoint of this FAPRM planner
	 */
	protected void setStart(FAPRMWaypoint start) {
		this.start = start;
	}

	/**
	 * Gets the goal FAPRM waypoint of this FAPRM planner.
	 * 
	 * @return the goal FAPRM waypoint of this FAPRM planner
	 */
	protected FAPRMWaypoint getGoal() {
		return this.goal;
	}

	/**
	 * Sets the goal FAPRM waypoint of this FAPRM planner.
	 * 
	 * @param goal the goal FAPRM waypoint of this FAPRM planner
	 */
	protected void setGoal(FAPRMWaypoint goal) {
		this.goal = goal;
	}

	/**
	 * Adds a FAPRM waypoint to the expandable FAPRM waypoints.
	 * 
	 * @param waypoint the FAPRM waypoint
	 * 
	 * @return true if the FAPRM waypoint has been added to the expandable FAPRM
	 *         waypoints, false otherwise
	 */
	protected boolean addExpandable(FAPRMWaypoint waypoint) {
		return this.open.add(waypoint);
	}

	/**
	 * Removes a FAPRM waypoint from the expandable FAPRM waypoints.
	 * 
	 * @param waypoint the FAPRM waypoint
	 * 
	 * @return true if the FAPRM waypoint has been removed from the expandable FAPRM
	 *         waypoints, false otherwise
	 */
	protected boolean removeExpandable(FAPRMWaypoint waypoint) {
		return this.open.remove(waypoint);
	}

	/**
	 * Clears the expandable FAPRM waypoints.
	 */
	protected void clearExpandables() {
		this.open.clear();
	}

	/**
	 * Determines whether or not a FAPRM waypoint is expandable.
	 * 
	 * @param waypoint the FAPRM waypoint
	 * 
	 * @return true if the FAPRM waypoint is expandable, false otherwise
	 */
	protected boolean isExpandable(FAPRMWaypoint waypoint) {
		return this.open.contains(waypoint);
	}

	/**
	 * Determines whether or not FAPRM waypoints can be expanded.
	 * 
	 * @return true if FAPRM waypoints can be expanded, false otherwise
	 */
	protected boolean canExpand() {
		return !(this.open.isEmpty());
	}

	/**
	 * Polls a FAPRM waypoint from the expandable FAPRM waypoints.
	 * 
	 * @return the polled FAPRM waypoint from the expandable FAPRM waypoints if any,
	 *         null otherwise
	 */
	protected FAPRMWaypoint pollExpandable() {
		return this.open.poll();
	}

	/**
	 * Adds a FAPRM waypoint to the expanded FAPRM waypoints.
	 * 
	 * @param waypoint the FAPRM waypoint
	 * 
	 * @return true if the FAPRM waypoint has been added to the expanded FAPRM
	 *         waypoints, false otherwise
	 */
	protected boolean addExpanded(FAPRMWaypoint waypoint) {
		return this.closed.add(waypoint);
	}

	/**
	 * Clears the expanded FAPRM waypoints.
	 */
	protected void clearExpanded() {
		this.closed.clear();
	}

	/**
	 * Determines whether or not a FAPRM waypoint has been expanded.
	 * 
	 * @param waypoint the FAPRM waypoint
	 * 
	 * @return true if the FAPRM waypoint has been expanded, false otherwise
	 */
	protected boolean isExpanded(FAPRMWaypoint waypoint) {
		return this.closed.contains(waypoint);
	}

	/**
	 * Gets the first FAPRM waypoint of the current plan.
	 * 
	 * @return the first FAPRM waypoint of the current plan
	 */
	protected FAPRMWaypoint getFirstWaypoint() {
		return this.plan.getFirst();
	}

	/**
	 * Adds a first FAPRM waypoint to the current plan.
	 * 
	 * @param waypoint the first FAPRM waypoint to be added to the current plan
	 */
	protected void addFirstWaypoint(FAPRMWaypoint waypoint) {
		this.plan.addFirst(waypoint);
	}

	/**
	 * Clears the waypoints of the current plan.
	 */
	protected void clearWaypoints() {
		this.plan.clear();
	}

	/**
	 * Gets the minimum quality (initial beta) of this FAPRM planner.
	 * 
	 * @return the minimum quality (initial beta) of this FAPRM planner
	 * 
	 * @see AnytimePlanner#getMinimumQuality()
	 */
	@Override
	public double getMinimumQuality() {
		return this.initialBeta;
	}

	/**
	 * Sets the minimum quality (initial beta) of this FAPRM planner.
	 * 
	 * @param initialBeta the minimum quality (initial beta) of this FAPRM planner
	 * 
	 * @throws IllegalArgumentException if the initial beta is invalid
	 * 
	 * @see AnytimePlanner#setMinimumQuality(double)
	 */
	@Override
	public void setMinimumQuality(double initialBeta) {
		if ((0d <= initialBeta) && (initialBeta <= this.finalBeta)) {
			this.initialBeta = initialBeta;
		} else {
			throw new IllegalArgumentException("initial inflation is invalid");
		}
	}

	/**
	 * Gets the maximum quality (final beta) of this FAPRM planner.
	 * 
	 * @return the maximum quality (final beta) of this FAPRM planner
	 * 
	 * @see AnytimePlanner#getMaximumQuality()
	 */
	@Override
	public double getMaximumQuality() {
		return this.finalBeta;
	}

	/**
	 * Sets the maximum quality (final beta) of this FAPRM planner.
	 * 
	 * @param finalBeta the maximum quality (final beta) of this FAPRM planner
	 * 
	 * @throws IllegalArgumentException if the final beta is invalid
	 * 
	 * @see AnytimePlanner#setMaximumQuality(double)
	 */
	@Override
	public void setMaximumQuality(double finalBeta) {
		if ((1d >= finalBeta) && (this.initialBeta <= finalBeta)) {
			this.finalBeta = finalBeta;
		} else {
			throw new IllegalArgumentException("final deflation is invalid");
		}
	}

	/**
	 * Gets the quality improvement (stepBeta) of this FAPRM planner.
	 * 
	 * @return the quality improvement (stepBeta) of this FAPRM planner
	 * 
	 * @see AnytimePlanner#getQualityImprovement()
	 */
	@Override
	public double getQualityImprovement() {
		return this.stepBeta;
	}

	/**
	 * Sets the quality improvement (stepBeta) of this FAPRM planner.
	 * 
	 * @param stepBeta the quality improvement (stepBeta) of this FAPRM planner
	 * 
	 * @throws IllegalArgumentException if the step Beta is invalid
	 * 
	 * @see AnytimePlanner#setQualityImprovement(double)
	 */
	@Override
	public void setQualityImprovement(double stepBeta) {
		if (0d < stepBeta) {
			this.stepBeta = stepBeta;
		} else {
			throw new IllegalArgumentException("deflation amount is invalid");
		}
	}

	/**
	 * Increases the current beta by stepBeta, or otherwise to the final beta.
	 */
	protected void inflate() {
		if (this.finalBeta > (this.beta + this.stepBeta)) {
			this.beta += this.stepBeta;
		} else {
			this.beta = this.finalBeta;
		}
	}

	/**
	 * Determines whether or not the final beta has been reached.
	 * 
	 * @return true if the final beta has been reached, false otherwise
	 */
	protected boolean isInflated() {
		return (this.beta == this.finalBeta);
	}

	/**
	 * Gets the current beta of this FAPRM planner.
	 * 
	 * @return the current beta of this FAPRM planner
	 */
	protected double getInflation() {
		return this.beta;
	}

	/**
	 * Sets the current beta of this FAPRM planner.
	 * 
	 * @param beta the current beta of this FAPRM planner
	 */
	protected void setInflation(double beta) {
		this.beta = beta;
	}

	/**
	 * Gets the desirability zones built by the user that affect the environment of
	 * this FAPRM planner.
	 * 
	 * @return the desirabilityZones of this planner
	 */
	public ArrayList<DesirabilityZone> getDesirabilityZones() {
		return desirabilityZones;
	}

	/**
	 * Sets the desirability zones built by the user that affect the environment of
	 * this FAPRM planner.
	 * 
	 * @param desirabilityZones the desirabilityZones to set
	 */
	public void setDesirabilityZones(ArrayList<DesirabilityZone> desirabilityZones) {
		this.desirabilityZones = desirabilityZones;
	}

	/**
	 * Sets the desirability value of an edge by checking which desirability zones
	 * intersect the edge. The desirability value of the edge is the mean value of
	 * the desirability values of the zones it intersects.
	 * 
	 * @param edge the edge whose desirability value is computed
	 */
	public void setEdgeDesirability(FAPRMEdge edge) {
		double desirability = 0;
		int i = 0;
		for (DesirabilityZone zone : this.getDesirabilityZones()) {
			if (zone.intersects(edge.getLine())) {
				desirability = desirability + zone.getDesirability();
				i++;
			}
		}
		if (i == 0) {
			desirability = 0.5;
		} else {
			desirability = desirability / i;
		}
		edge.setDesirability(desirability);
	}

	/**
	 * Gets the parameter lambda of this FAPRM planner.
	 * 
	 * @return the lambda the parameter lambda of this FAPRM planner
	 */
	public double getLambda() {
		return lambda;
	}

	/**
	 * Sets the parameter lambda of this FAPRM planner.
	 * 
	 * @param lambda the parameter lambda to set
	 */
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	/**
	 * Connects a plan of specified FAPRM waypoints.
	 * 
	 * @param waypoint the last FAPRM waypoint of a computed plan
	 */
	protected void connectPlan(FAPRMWaypoint waypoint) {
		this.clearWaypoints();

		// setting goal dtg and ttg
		waypoint.setDtg(0d);
		waypoint.setTtg(Duration.ZERO);

		// in accordance with moving start capability
		while (!this.getStart().equals(waypoint)) {
			this.addFirstWaypoint(waypoint.clone());
			if (waypoint.getParent() == null) {
				System.out.println("NULLLLl");
			}
			waypoint = waypoint.getParent();
			if (null != waypoint) {
				waypoint.setTtg(Duration.between(waypoint.getEto(), this.getFirstWaypoint().getEto()));
				waypoint.setDtg(this.getEnvironment().getDistance(waypoint, this.getFirstWaypoint()));
			}
		}
		this.addFirstWaypoint(waypoint.clone());
	}

	/**
	 * Samples a new waypoint from the environment with a given bias to the goal
	 * 
	 * @param bias the percentage value of bias to sample the goal
	 * 
	 * @return waypoint the FAPRMWaypoint sampled
	 */
	protected Position sampleBiased(int bias, FAPRMWaypoint waypoint) {
		Position position;
		int rand = new Random().nextInt(100 - 1) + 1;

		position = (rand <= bias) ? this.sampleGoal(waypoint) : this.samplePosition(waypoint);

		while (this.getEnvironment().isInsideGlobe(this.getEnvironment().getGlobe(), position)
				|| this.getEnvironment().checkConflict(position, getAircraft())
				|| this.getEnvironment().checkConflict(waypoint, position, getAircraft())
				|| !this.getAircraft().getCapabilities().isFeasible(waypoint, position,
						this.getEnvironment().getGlobe())
				|| !this.getEnvironment().contains(position)) {
			rand = new Random().nextInt(100 - 1) + 1;
			position = (rand <= bias) ? this.sampleGoal(waypoint) : this.samplePosition(waypoint);
		}

		return position;
	}

	/**
	 * Samples a new position in the direction of the goal, at a specified maximum
	 * distance away from the source position.
	 * 
	 * @param position the source position from which the tree is grown
	 * 
	 * @return the new position resulting from the controlled growth of the tree
	 */
	protected Position sampleGoal(Position position) {
		Position positionNew;
		Vec4 point1 = super.getEnvironment().getGlobe().computePointFromPosition(position);
		Vec4 point2 = super.getEnvironment().getGlobe().computePointFromPosition(this.getGoal());

		double dist = point1.distanceTo3(point2);

		if (dist < this.maxDistance) {
			positionNew = this.getGoal();
		} else {
			double x, y, z, dx, dy, dz, dist2, epsilon2, ddz;
			dx = point2.x - point1.x;
			dy = point2.y - point1.y;
			dz = point2.z - point1.z;

			ddz = dz / dist * this.maxDistance;
			epsilon2 = Math.sqrt(this.maxDistance * this.maxDistance - ddz * ddz);
			dist2 = point1.distanceTo2(point2);

			x = point1.x + dx / dist2 * epsilon2;
			y = point1.y + dy / dist2 * epsilon2;
			z = point1.z + ddz;

			Vec4 pointNew = new Vec4(x, y, z);
			positionNew = super.getEnvironment().getGlobe().computePositionFromPoint(pointNew);
		}
		return positionNew;
	}

	/**
	 * Samples a position from a continuous space around a given position
	 * 
	 * @param position the position around which the new position is going to be
	 *            sampled
	 * 
	 * @return the sampled position in globe coordinates
	 */
	public Position samplePosition(Position position) {
		Vec4 point = this.getEnvironment().getGlobe().computePointFromPosition(position);
		List<Vec4> corners = new ArrayList<Vec4>();
		double halfDistance = this.maxDistance / Math.sqrt(3);

		corners.add(point.add3(-halfDistance, +halfDistance, -halfDistance));
		corners.add(point.add3(+halfDistance, +halfDistance, -halfDistance));
		corners.add(point.add3(+halfDistance, -halfDistance, -halfDistance));
		corners.add(point.add3(+halfDistance, -halfDistance, +halfDistance));
		corners.add(point.add3(-halfDistance, -halfDistance, +halfDistance));
		corners.add(point.add3(-halfDistance, +halfDistance, +halfDistance));

		SamplingEnvironment env = new SamplingEnvironment(
				new Box(gov.nasa.worldwind.geom.Box.computeBoundingBox(corners)));
		env.setGlobe(this.getEnvironment().getGlobe());
		Position pos = env.sampleRandomPosition();
		return pos;
	}

	/**
	 * Indicates whether or not two waypoints are connectable.
	 * 
	 * @param waypoint the waypoint in globe coordinates
	 * @param neighbor the neighbor in globe coordinates
	 * @param num the number of connected neighbors to the waypoint
	 * 
	 * @return true if the two waypoints are connectable, false otherwise
	 */
	protected boolean areConnectable(Waypoint waypoint, Waypoint neighbor, int num) {
		boolean connectable = false;

		Capabilities capabilities = this.getAircraft().getCapabilities();

		if (super.getEnvironment().getDistance(neighbor, waypoint) < maxDistance && num < maxNeighbors) {
			if ((capabilities.isFeasible(waypoint, neighbor, this.getEnvironment().getGlobe())
					|| capabilities.isFeasible(neighbor, waypoint, this.getEnvironment().getGlobe()))
					&& !this.getEnvironment().checkConflict(waypoint, neighbor, getAircraft())) {
				connectable = true;
			}
		}

		return connectable;
	}

	/**
	 * Expands a FAPRM waypoint towards its neighbors in the environment.
	 * 
	 * @param waypoint the FAPRM waypoint to be expanded
	 * 
	 * @return the new sampled waypoint
	 */
	protected FAPRMWaypoint expand(FAPRMWaypoint waypoint) {
		Position newPosition = this.sampleBiased(bias, waypoint);
		FAPRMWaypoint newWaypoint = this.createWaypoint(newPosition);

		newWaypoint.setDistanceToGoal(this.getEnvironment().getNormalizedDistance(newWaypoint, this.getGoal()));

		this.createEdge(waypoint, newWaypoint);
		waypoint.addNeighbor(newWaypoint);
		newWaypoint.addNeighbor(waypoint);

		int numConnectedNeighbor = 1;
		this.getEnvironment().sortNearest(newWaypoint);

		for (FAPRMWaypoint neighbor : this.getWaypointList()) {
			if (neighbor.equals(newWaypoint) || neighbor.equals(waypoint))
				continue;
			if (this.areConnectable(neighbor, newWaypoint, numConnectedNeighbor)) {
				numConnectedNeighbor++;
				this.createEdge(neighbor, newWaypoint);
				neighbor.addNeighbor(newWaypoint);
				newWaypoint.addNeighbor(neighbor);
			}
		}
		return newWaypoint;
	}

	/**
	 * Creates a waypoint at a specified position, initializes its beta value to the
	 * current inflation, density to 0 and the estimated costs to infinity.
	 * 
	 * @param position the position in globe coordinates
	 * 
	 * @return the new created waypoint
	 */
	@SuppressWarnings("unchecked")
	protected FAPRMWaypoint createWaypoint(Position position) {
		FAPRMWaypoint newWaypoint = new FAPRMWaypoint(position);
		newWaypoint.setEto(this.getEnvironment().getTime());
		newWaypoint.setBeta(this.getInflation());
		newWaypoint.setDensity(0);
		((List<FAPRMWaypoint>) this.getWaypointList()).add(newWaypoint);
		return newWaypoint;
	}

	/**
	 * Creates an edge between a source and a target waypoint, sets the cost
	 * interval tree, desirability and lambda values and adds it to the edge list.
	 * 
	 * @param source the source FAPRM waypoint
	 * @param target the target FAPRM waypoint
	 */
	protected void createEdge(FAPRMWaypoint source, FAPRMWaypoint target) {
		Vec4 sourcePoint = this.getEnvironment().getGlobe().computePointFromPosition(source);
		Vec4 targetPoint = this.getEnvironment().getGlobe().computePointFromPosition(target);

		FAPRMEdge edgeNew = new FAPRMEdge(source, target, new Line(sourcePoint, targetPoint));
		edgeNew.setCostIntervals(this.getEnvironment().embedIntervalTree(edgeNew.getLine()));
		this.setEdgeDesirability(edgeNew);
		edgeNew.setLambda(this.getLambda());
		this.getEdgeList().add(edgeNew);
	}

	/**
	 * Computes the estimated cost of a specified target FAPRM waypoint when reached
	 * via a specified source FAPRM waypoint.
	 * 
	 * @param source the source FAPRM waypoint in globe coordinates
	 * @param target the target FAPRM waypoint in globe coordinates
	 */
	protected void computeCost(FAPRMWaypoint source, FAPRMWaypoint target) {

		Path leg = new Path(source, target);
		Capabilities capabilities = this.getAircraft().getCapabilities();
		Globe globe = this.getEnvironment().getGlobe();
		ZonedDateTime end = capabilities.getEstimatedTime(leg, globe, source.getEto());

		double cost = this.getEnvironment().getDesirabilityStepCost(source, target, source.getEto(), end,
				this.getCostPolicy(), this.getRiskPolicy());

		if (1 / (1 + (source.getCost() + cost)) > target.getG()) {
			target.setCost(source.getCost() + cost);
			target.setEto(end);
			target.setParent(source);
		}
		return;

	}

	/**
	 * Creates a trajectory of the computed plan.
	 * 
	 * @return the trajectory of the computed plan
	 */
	@SuppressWarnings("unchecked")
	protected Trajectory createTrajectory() {
		return new Trajectory((List<FAPRMWaypoint>) this.plan.clone());
	}

	/**
	 * Improves a plan incrementally.
	 */
	protected Trajectory improve() {
		Trajectory trajectory = null;
		this.inflate();
		for (FAPRMWaypoint waypoint : this.getWaypointList()) {
			waypoint.setBeta(this.getInflation());
		}
		this.clearExpanded();
		this.clearExpandables();
		this.addExpandable(this.getStart());
		trajectory = this.compute();
		return trajectory;
	}

	/** indicates whether or not a multi-part plan is being improved */
	protected boolean improving = false;

	/** the index of the backed up expandable waypoints */
	protected int backupIndex = -1;

	/** the backed up expandable waypoints */
	protected final ArrayList<ArrayList<FAPRMWaypoint>> backups = new ArrayList<>();

	/**
	 * Initializes a number of open backup priority queues.
	 * 
	 * @param size the number of open backup priority queues
	 */
	protected void initBackups(int size) {
		this.backups.clear();

		for (int waypointIndex = 0; waypointIndex < size; waypointIndex++) {
			this.backups.add(waypointIndex, new ArrayList<FAPRMWaypoint>());
		}
		this.backupIndex = -1;
	}

	/**
	 * Determines whether or not a backup can be performed.
	 * 
	 * @return true if a backup can be performed, false otherwise
	 */
	protected boolean canBackup() {
		return (-1 < this.backupIndex) && (this.backupIndex < this.backups.size());
	}

	/**
	 * Determines whether or not a backup is available.
	 * 
	 * @return true if a backup is available, false otherwise
	 */
	protected boolean hasBackup() {
		return this.canBackup() && (!this.backups.get(this.backupIndex).isEmpty());
	}

	/**
	 * Backs up the expandable FAPRM waypoints for improvement.
	 * 
	 * @return true if a backup has been performed, false otherwise
	 */
	protected boolean backup() {
		boolean backedup = false;
		if (this.canBackup()) {
			this.backups.get(this.backupIndex).clear();
			// TODO: make clones
			for (FAPRMWaypoint waypoint : this.getWaypointList()) {
				FAPRMWaypoint newWaypoint = waypoint.clone();
				// newWaypoint.setPai(waypoint.getParent());
				this.backups.get(this.backupIndex).add(newWaypoint);
			}

			for (FAPRMWaypoint wpt : this.backups.get(this.backupIndex)) {
				FAPRMWaypoint wptMirror = this.getWaypointList().stream().filter(s -> s.equals(wpt)).findFirst().get();
				FAPRMWaypoint parentMirror = wptMirror.getParent();
				FAPRMWaypoint parent;
				if (parentMirror == null) {
					parent = null;
				} else {
					parent = this.backups.get(this.backupIndex).stream().filter(s -> s.equals(parentMirror)).findFirst()
							.get();
				}
				wpt.setParent(parent);
			}

			backedup = true;
			this.getWaypointList().clear();
		}
		return backedup;
	}

	/**
	 * Restores expandable FAPRM waypoints for improvement.
	 * 
	 * @return true if a restoral has been performed, false otherwise
	 */
	protected boolean restore() {
		boolean restored = false;
		if (this.hasBackup()) {
			for (FAPRMWaypoint waypoint : this.backups.get(this.backupIndex)) {
				waypoint.setBeta(this.getInflation());
			}
			this.setWaypointList(this.backups.get(this.backupIndex));
			restored = true;
		}
		return restored;
	}

	/**
	 * Corrects a trajectory, checking if any of its edges is in conflict with
	 * terrain obstacles.
	 * 
	 * @param trajectory the planned trajectory
	 * 
	 * @return true if this trajectory is feasible, false otherwise
	 */
	public boolean correctTrajectory(Trajectory trajectory) {
		if (trajectory == null)
			return false;

//		HashSet<Edge> conflictEdges = new HashSet<Edge>();
//		// waypoints conflict is checked when a new position is sampled
//
//		Waypoint wpt1 = Iterables.get(trajectory.getWaypoints(), 0);
//		Waypoint wpt2;
//		for (int i = 0; i < trajectory.getLength() - 1; i++) {
//			wpt2 = Iterables.get(trajectory.getWaypoints(), i + 1);
//			if (this.getEnvironment().checkConflict(wpt1, wpt2, getAircraft())) {
//				Edge edge = new Edge(wpt1, wpt2);
//				Edge edgeInList = this.getEdgeList().stream().filter(s -> s.equals(edge)).findFirst().get();
//				conflictEdges.add(edgeInList);
//			}
//			wpt1 = wpt2;
//		}
//		if (!conflictEdges.isEmpty()) {
//			System.out.println("edges to be corrected");
//			this.correctEdges(conflictEdges);
//			return false;
//		}
		return true;
	}

//	/**
//	 * Corrects the edge list by removing the edges that are in conflict with
//	 * terrain obstacles. Also, it corrects the affected waypoints and propagates
//	 * the updates.
//	 * 
//	 * @param conflictEdges the set of edges that are in conflict with terrain
//	 *            obstacles
//	 */
//	public void correctEdges(HashSet<Edge> conflictEdges) {
//		Set<FAPRMWaypoint> waypoints = new HashSet<FAPRMWaypoint>();
//		for (Edge edge : conflictEdges) {
//			FAPRMWaypoint waypoint1 = (FAPRMWaypoint) this.getWaypointList().stream()
//					.filter(s -> s.equals(edge.getPosition1())).findFirst()
//					.get();
//			FAPRMWaypoint waypoint2 = (FAPRMWaypoint) this.getWaypointList().stream()
//					.filter(s -> s.equals(edge.getPosition2())).findFirst()
//					.get();
//			if (waypoint1.equals(waypoint2.getParent())) {
//				waypoint2.setParent(null);
//				waypoint2.setCost(Double.POSITIVE_INFINITY);
//				waypoint2.getNeighbors().remove(waypoint1);
//				waypoint1.getNeighbors().remove(waypoint2);
//				waypoints.add(waypoint2);
//			}
//			if (waypoint2.equals(waypoint1.getParent())) {
//				waypoint1.setParent(null);
//				waypoint1.setCost(Double.POSITIVE_INFINITY);
//				waypoint2.getNeighbors().remove(waypoint1);
//				waypoint1.getNeighbors().remove(waypoint2);
//				waypoints.add(waypoint1);
//			}
//			this.getEdgeList().remove(edge);
//		}
//		for(FAPRMWaypoint waypoint: waypoints) {
//			for (FAPRMWaypoint wptaux : waypoint.getNeighbors()) {
//				if(wptaux.equals(this.getGoal()))
//					continue;
//				this.computeCost(wptaux, waypoint);
//			}
//		}
//		this.propagateCorrections(waypoints);
//	}
//
//	
	
	/**
	 * Propagates the corrections. If a predecessor of a certain waypoint had its
	 * parent changed to null, then that waypoint and its successors need to be
	 * corrected.
	 * 
	 * @param waypoints the set of waypoints to correct
	 */
	@SuppressWarnings("unchecked")
	protected void propagateCorrections(Set<FAPRMWaypoint> waypoints) {
		Set<FAPRMWaypoint> newWaypoints = new HashSet<FAPRMWaypoint>();

		for (FAPRMWaypoint waypoint : waypoints) {
			List<FAPRMWaypoint> list = (List<FAPRMWaypoint>) this.getWaypointList();
			for (FAPRMWaypoint aux : list) {
				if (aux.getParent() == null)
					continue;
				if (aux.getParent().equals(waypoint)) {
					newWaypoints.add(aux);
				}
			}
		}
		if (newWaypoints.isEmpty()) {
			return;
		} else {
			for (FAPRMWaypoint waypoint : newWaypoints) {
				waypoint.setParent(null);
				waypoint.setCost(Double.POSITIVE_INFINITY);
				for (FAPRMWaypoint wptaux : waypoint.getNeighbors()) {
					if (wptaux.equals(this.getGoal())|| wptaux.getCost()==Double.POSITIVE_INFINITY)
						continue;
					this.computeCost(wptaux, waypoint);
				}
			}
		}
		this.propagateCorrections(newWaypoints);
	}

	/**
	 * Updates all successors of a given waypoint.
	 * 
	 * @param source the predecessor of all waypoints to be updated
	 */
	public void updateNeighbors(FAPRMWaypoint source) {

		if (source.equals(this.getGoal())) {
			return;
		}
		//TODO: sort get neighbors by increasing cost (g-value)
		for (FAPRMWaypoint waypoint : source.getNeighbors()) {
			if (waypoint.equals(this.getStart()))
				continue;

			FAPRMWaypoint oldParent = waypoint.getParent();
			double g = waypoint.getG();
			for (FAPRMWaypoint wptaux : waypoint.getNeighbors()) {
				if(wptaux.equals(this.getGoal()))
					continue;
				this.computeCost(wptaux, waypoint);
			}
			if (!this.isExpanded(waypoint)) {
				this.removeExpandable(waypoint);
				this.addExpandable(waypoint);
			}
//			if (this.parentChanged(oldParent, waypoint) || g < waypoint.getG()) {
//				if (!this.isExpanded(waypoint)) {
//					this.updateSuccessors(waypoint);
//				}
//			}
		}
	}

	/**
	 * 
	 * @param source
	 */
	public void updateSuccessors(FAPRMWaypoint source) {
		if (source.equals(this.getGoal())) {
			return;
		}
		//TODO: sort get neighbors by increasing cost (g-value)
		for (FAPRMWaypoint waypoint : source.getNeighbors()) {
			if (waypoint.equals(this.getStart()))
				continue;

			FAPRMWaypoint oldParent = waypoint.getParent();
			double g = waypoint.getG();

			for (FAPRMWaypoint wptaux : waypoint.getNeighbors()) {
				this.computeCost(wptaux, waypoint);
			}
			if (this.parentChanged(oldParent, waypoint) || g < waypoint.getG()) {
				if (!this.isExpanded(waypoint)) {
					this.updateSuccessors(waypoint);
				}
			}
		}
	}
	/**
	 * Checks if the parent waypoint of a given waypoint has changed after
	 * recomputing the costs and updating the neighbors.
	 * 
	 * @param oldParent the old parent waypoint of the given waypoint
	 * @param waypoint the given waypoint
	 * 
	 * @return true if the parent waypoint has changed, false otherwise
	 */
	public boolean parentChanged(FAPRMWaypoint oldParent, FAPRMWaypoint waypoint) {
		if (oldParent == null) {
			if (waypoint.getParent() != null)
				return true;
			else {
				return false;
			}
		} else {
			if (oldParent.equals(waypoint.getParent())) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * Updates the density of all waypoints in the waypoint list, by checking the
	 * number of waypoints that lie within a certain distance.
	 * 
	 * @param waypoint the new sampled waypoint
	 */
	public void updateDensity(FAPRMWaypoint waypoint) {
		int density = 0;
		Set<FAPRMWaypoint> backupOpen = new HashSet<FAPRMWaypoint>();

		for (FAPRMWaypoint neighbor : this.getWaypointList()) {
			if (this.getEnvironment().getDistance(waypoint, neighbor) < maxDistance) {
				density++;
				neighbor.incrementDensity();
			}
			if (this.isExpandable(neighbor)) {
				backupOpen.add(neighbor);
				this.removeExpandable(neighbor);
			}
		}
		for (FAPRMWaypoint wpt : backupOpen) {
			this.addExpandable(wpt);
		}
		waypoint.setDensity(density);

		return;
	}

	/**
	 * Checks whether or not a given waypoint and the goal waypoint are connectable.
	 * 
	 * @param source the waypoint
	 * 
	 * @return true if the two waypoints are connectable, false otherwise
	 */
	public boolean connectToGoal(FAPRMWaypoint source) {
		if (this.areConnectable(source, this.getGoal(), 0)) {
			if (!this.getEdgeList().contains(new Edge(source, this.getGoal()))) {
				this.createEdge(source, this.getGoal());
			}
			return true;
		}
		return false;
	}

	/**
	 * Computes a path between a start and a goal waypoint.
	 */
	protected void computeOrImprovePath() {
		while (this.canExpand()) {
			FAPRMWaypoint source = this.pollExpandable();
			if (source.equals(this.getGoal())) {
				this.connectPlan(source);
				return;
			}
			FAPRMWaypoint newSuccessor;
			if (this.connectToGoal(source)) {
				newSuccessor = this.getGoal();
				this.getGoal().addNeighbor(source);
				source.addNeighbor(this.getGoal());
			} else {
				newSuccessor = this.expand(source);
			}
			this.updateDensity(newSuccessor);
			this.updateNeighbors(source);
			if (this.isExpandable(source)) {
				this.removeExpandable(source);
			}
			this.addExpanded(source);
		}
		return;
	}

	/**
	 * Computes a plan.
	 * 
	 * @param origin the origin in globe coordinates
	 * @param destination the destination in globe coordinates
	 * @param etd the estimated time of departure
	 * 
	 * @return the planned trajectory from the origin to the destination with the
	 *         estimated time of departure
	 */
	protected Trajectory compute() {
		Trajectory trajectory = null;

		while (!this.correctTrajectory(trajectory)) {
			this.clearExpandables();
			this.clearExpanded();
			this.addExpandable(this.getStart());
			this.computeOrImprovePath();
			trajectory = this.createTrajectory();
		}
		return trajectory;
	}

	/**
	 * Initializes the planner to plan from an origin to a destination at a
	 * specified estimated time of departure.
	 * 
	 * @param origin the origin in globe coordinates
	 * @param destination the destination in globe coordinates
	 * @param etd the estimated time of departure
	 */
	protected void initialize(Position origin, Position destination, ZonedDateTime etd) {
		this.backupIndex++;
		if (this.improving) {
			this.restore();
		} else {
			this.setInflation(this.getMinimumQuality());
		}
		this.clearExpandables();
		this.clearExpanded();
		this.clearWaypoints();

		this.setStart(this.createWaypoint(origin));

		this.setGoal(this.createWaypoint(destination));

		this.getStart().setCost(0d);
		this.getStart().setDistanceToGoal(this.getEnvironment().getNormalizedDistance(this.getStart(), this.getGoal()));
		this.getStart().setEto(etd);

		this.getGoal().setDistanceToGoal(0d);

		this.addExpandable(this.getStart());
	}

	/**
	 * Plans a trajectory from an origin to a destination at a specified estimated
	 * time of departure.
	 * 
	 * @param origin the origin in globe coordinates
	 * @param destination the destination in globe coordinates
	 * @param etd the estimated time of departure
	 * 
	 * @return the planned trajectory from the origin to the destination with the
	 *         estimated time of departure
	 * 
	 * @see Planner#plan(Position, Position, ZonedDateTime)
	 */
	@Override
	public Trajectory plan(Position origin, Position destination, ZonedDateTime etd) {
		// if planner is invoked again with the same environment, lists are cleared
		this.getWaypointList().clear();
		this.getEdgeList().clear();

		this.initialize(origin, destination, etd);
		Trajectory trajectory = this.compute();
		this.revisePlan(trajectory);
		do {
			// Online
			if (isOnline()) {
				System.out.println("Online planning!!!");
				this.updateAircraftTimedPosition();
				System.out.println("Position updated!!");

				int index = getDatalink().getNextWaypointIndex();
				System.out.println("Next Waypoint index = " + index);
				if (index >= 0 && index + 3 < this.plan.size()) {
					System.out.println("Current plan -----------------------");
					for (Waypoint waypoint : this.plan)
						System.out.println(waypoint);
					System.out.println("------------------------------------");
					System.out.println("In plan this is ... " + this.plan.get(index));
					this.updateStart(plan.get(index+3));
				} else
					System.out.println("Negative indexxxx (or maximum) " + index);
			}
			// Anytime
			if (!this.isInflated()) {
				System.out.println("Anytime");
				System.out.println("Improving");
				trajectory = this.improve();
				this.revisePlan(trajectory);
				if (getDatalink().isConnected()) {
					this.getDatalink().uploadFlightPath(trajectory);
					System.out.println("UPLOADDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
				}
			}

		} while ((!isOnline() && !isInflated()) || (isOnline() && !isInsideGoalRegion()));
		System.out.println("Improved");

		return trajectory;
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
	 * @return the planned trajectory from the origin to the destination along the
	 *         waypoints with the estimated time of departure
	 * 
	 * @see Planner#plan(Position, Position, List, ZonedDateTime)
	 */
	@Override
	public Trajectory plan(Position origin, Position destination, List<Position> waypoints, ZonedDateTime etd) {
		// if planner is invoked again with the same environment, lists are cleared
		this.getWaypointList().clear();
		this.getEdgeList().clear();

		this.initBackups(waypoints.size() + 1);
		Trajectory trajectory = this.oneTimePlan(origin, destination, waypoints, etd);
		this.improving = true;
		while (!this.isInflated()) {
			this.inflate();
			this.backupIndex = -1;
			trajectory = this.oneTimePlan(origin, destination, waypoints, etd);
		}
		this.improving = false;

		return trajectory;
	}

	/**
	 * Plans a one time trajectory from an origin to a destination along waypoints
	 * at a specified estimated time of departure, considering the current beta
	 * value.
	 * 
	 * @param origin the origin in globe coordinates
	 * @param destination the destination in globe coordinates
	 * @param waypoints the waypoints in globe coordinates
	 * @param etd the estimated time of departure
	 * 
	 * @return the planned trajectory from the origin to the destination along the
	 *         waypoints with the estimated time of departure
	 */
	public Trajectory oneTimePlan(Position origin, Position destination, List<Position> waypoints, ZonedDateTime etd) {
		LinkedList<Waypoint> plan = new LinkedList<>();
		Waypoint currentOrigin = new Waypoint(origin);
		ZonedDateTime currentEtd = etd;

		// collect intermediate destinations
		ArrayList<Waypoint> destinations = waypoints.stream().map(Waypoint::new)
				.collect(Collectors.toCollection(ArrayList::new));
		destinations.add(new Waypoint(destination));

		// plan and concatenate partial trajectories
		for (Waypoint currentDestination : destinations) {
			if (!(currentOrigin.equals(currentDestination))) {
				// plan partial trajectory
				this.initialize(currentOrigin, currentDestination, currentEtd);
				Trajectory part = this.compute();
				this.backup();
				// append partial trajectory to plan
				if ((!plan.isEmpty()) && (!part.isEmpty())) {
					plan.pollLast();
				}

				for (Waypoint waypoint : part.getWaypoints()) {
					plan.add(waypoint);
				}
				if (plan.peekLast().equals(currentOrigin)) {
					// if no plan could be found, return an empty trajectory
					Trajectory trajectory = new Trajectory();
					this.revisePlan(trajectory);
					return trajectory;
				} else {
					currentOrigin = plan.peekLast();
					currentEtd = currentOrigin.getEto();
				}
			}
		}
		Trajectory trajectory = new Trajectory(plan);
		this.revisePlan(trajectory);
		return trajectory;
	}

	/**
	 * Indicates whether or not this FAPRM planner supports a specified environment.
	 * 
	 * @param environment the environment
	 * 
	 * @return true if the environment is a sampling environment, false otherwise
	 * 
	 * @see SamplingEnvironment
	 */
	@Override
	public boolean supports(Environment environment) {
		boolean supports = super.supports(environment);

		if (supports) {
			supports = (environment instanceof SamplingEnvironment);
		}

		return supports;
	}

	/**
	 * Indicates whether or not this PRM planner supports specified
	 * waypoints.
	 * 
	 * @param waypoints the waypoints
	 * 
	 * @return true if waypoints are contained in the planner's environment,
	 *         false otherwise
	 */
	@Override
	public boolean supports(List<Position> waypoints) {
		boolean supports = false;
		
		supports = super.supports(waypoints);
		
		for(Position waypoint : waypoints) {
			if(this.getEnvironment().checkConflict(waypoint, getAircraft()))
				supports = false;
		}
		
		return supports;
	}
	
	/** the state of the online capabilities of the planner mode (active or not) */
	private boolean onlineStatus;

	/** the distance threshold to consider a position displacement as worthy of a new plan */
	private double positionThreshold;
	
	/** the datalink connection of this planner */ 
	private Datalink datalink;

	/** the radius of the sphere defining the goal region */
	private static final double GOAL_THRESHOLD = 1d;
	
	/** the current position of the aircraft */
	private Waypoint aircraftTimedPosition;

	/**
	 * Checks if the online capabilities of the planner mode are active or not.
	 * 
	 * @return true if the planner mode is set to online, false otherwise
	 */
	public boolean isOnline() {
		return onlineStatus;
	}
	
	/**
	 * Sets the online status of the planner.
	 * 
	 * @param onlineStatus the online status of this planner
	 */
	public void setOnlineStatus(boolean onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	/**
	 * Gets the datalink connection of this planner.
	 * 
	 * @return the datalink
	 */
	public Datalink getDatalink() {
		return datalink;
	}

	/**
	 * Sets the datalink connection of this planner.
	 * 
	 * @param datalink the datalink to set
	 */
	public void setDatalink(Datalink datalink) {
		this.datalink = datalink;
	}
	
	/**
	 * Sets the distance threshold to consider a position displacement as worthy of
	 * a new plan.
	 * 
	 * @param positionThreshold the distance threshold for each position
	 */
	public void setPositionThreshold(double positionThreshold) {
		this.positionThreshold = positionThreshold;
	}

	/**
	 * Gets the distance threshold to consider a position displacement as worthy of
	 * a new plan.
	 * 
	 * @return the distance threshold for each position
	 */
	public double getPositionThreshold() {
		return positionThreshold;
	}

	/**
	 * Gets the current position of the aircraft.
	 * 
	 * @return the current position of the aircraft
	 */
	public Waypoint getAircraftTimedPosition() {
		return aircraftTimedPosition;
	}

	/**
	 * Sets the current position of the aircraft.
	 * 
	 * @param aircraftPosition the current position of the aircraft
	 */
	public void setAircraftTimedPosition(Waypoint aircraftTimedPosition) {
		this.aircraftTimedPosition = aircraftTimedPosition;
	}

	/**
	 * Check if the distance between the two positions is significant to consider
	 * the current position as a different one.
	 * 
	 * @param previous the previous position to consider
	 * @param current the current position to consider
	 * 
	 * @return true if the movement between the two positions is significant, false
	 *         otherwise
	 */
	public boolean isSignificantMovement(Position previous, Position current) {
		return this.getEnvironment().getDistance(previous, current) > this.getPositionThreshold();
	}

	/**
	 * Checks if the current position of the aircraft is inside the goal region.
	 * 
	 * @return true if aircraft is inside the goal region, false otherwise
	 */
	public boolean isInsideGoalRegion() {
		return this.getEnvironment().getDistance(this.getAircraftTimedPosition(), this.getGoal()) < GOAL_THRESHOLD;
	}

	/**
	 * Updates the current position of the aircraft in the planner by reading its
	 * actual position from an external source.
	 */
	public void updateAircraftTimedPosition() {
		this.setAircraftTimedPosition(getDatalink().getAircraftTimedPosition());
	}
	
	/**
	 * Updates the start position.
	 * 
	 * @param newStart the new start waypoint to set
	 */
	public void updateStart(FAPRMWaypoint newStart) {
		this.clearExpandables();
		FAPRMWaypoint oldStart = this.getStart();

		for (FAPRMWaypoint waypoint : this.getWaypointList()) {
			waypoint.setCost(Double.POSITIVE_INFINITY);
			waypoint.setParent(null);
		}
		System.out.println("oldstart " +oldStart + " newstart " + newStart);
		this.setStart(this.createWaypoint(newStart));

		this.getStart().setCost(0d);
		this.getStart().setDistanceToGoal(this.getEnvironment().getNormalizedDistance(this.getStart(), this.getGoal()));
		this.getStart().setEto(this.getEnvironment().getTime());

		int numConnectedNeighbor = 0;
		this.getEnvironment().sortNearest(this.getStart());

		for (FAPRMWaypoint neighbor : this.getWaypointList()) {
			if (neighbor.equals(this.getStart()))
				continue;
			if (this.areConnectable(neighbor, this.getStart(), numConnectedNeighbor)) {
				numConnectedNeighbor++;
				this.createEdge(neighbor, this.getStart());
				neighbor.addNeighbor(this.getStart());
				this.getStart().addNeighbor(neighbor);
			}
		}

		this.getGoal().setCost(Double.POSITIVE_INFINITY);
		this.getGoal().setParent(null);

		return;

	}
}
