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

import java.time.ZonedDateTime;
import java.util.Set;

import com.cfar.swim.worldwind.render.Obstacle;
import com.cfar.swim.worldwind.render.ThresholdRenderable;
import com.cfar.swim.worldwind.render.TimedRenderable;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;

/**
 * Describes an environment as a renderable on a globe (spatial aspect) with
 * associated cost intervals (temporal aspect).
 * TODO: Update description
 * 
 * @author Stephan Heinemann
 *
 */
public interface Environment extends TimedRenderable, ThresholdRenderable {
	
	/**
	 * Gets the globe of this environment.
	 * 
	 * @return the globe of this environment
	 */
	public Globe getGlobe();
	
	/**
	 * Sets the globe of this environment.
	 * 
	 * @param globe the globe of this environment
	 */
	public void setGlobe(Globe globe);
	
	// TODO: aggregate property time intervals for air density (temperature, pressure, humidity)
	// TODO: create a new AirDataInterval class which aggregates AirData class
	// TODO: AirData, Surface/GroundData extends EnvironmentData
	// TODO: environment would have to store air/ground property intervals
	// TODO: data could be made available via WXXM and IWXXM
	
	/**
	 * Indicates whether or not this environment contains a position.
	 * 
	 * @param position the position in globe coordinates
	 * 
	 * @return true if this environment contains the position, false otherwise
	 */
	public boolean contains(Position position);
	
	
	/**
	 * Gets the center position of this environment in globe coordinates.
	 * 
	 * @return the center position of this environment in globe coordinates
	 */
	public Position getCenterPosition();
	
	/**
	 * Gets the neighbors of this environment.
	 * 
	 * @return the neighbors of this environment
	 */
	public Set<? extends Environment> getNeighbors();
	
	/**
	 * Indicates whether or not this environment is a neighbor of another
	 * environment.
	 * 
	 * @param neighbor the potential neighbor
	 * 
	 * @return true if this environment is a neighbor of the other environment,
	 *         false otherwise
	 */
	public boolean areNeighbors(Environment neighbor);
	
	/**
	 * Embeds an obstacle into this environment.
	 * 
	 * @param obstacle the obstacle to be embedded
	 * 
	 * @return true if the obstacle has been embedded, false otherwise
	 */
	public boolean embed(Obstacle obstacle);
	
	/**
	 * Umembeds an obstacle from this environment.
	 * 
	 * @param obstacle the obstacle to be unembedded
	 * 
	 * @return true if the obstacle has been unembedded, false otherwise
	 */
	public boolean unembed(Obstacle obstacle);
	
	/**
	 * Unembeds all obstacles from this environment.
	 */
	public void unembedAll();
	
	/**
	 * Indicates whether or not an obstacle is embedded in this environment.
	 * 
	 * @param obstacle the obstacle
	 * 
	 * @return true if the obstacle is embedded in this environment,
	 *         false otherwise
	 */
	public boolean isEmbedded(Obstacle obstacle);
	
	/**
	 * Updates this environment for an embedded obstacle.
	 * 
	 * @param obstacle the embedded obstacle
	 */
	public void refresh(Obstacle obstacle);
	
	/**
	 * Gets the distance between two positions in this environment.
	 * 
	 * @param position1 the first position
	 * @param position2 the second position
	 * 
	 * @return the distance between the two positions in this environment
	 */
	public double getDistance(Position position1, Position position2);
	
	/**
	 * Gets the normalized distance between two positions in this environment.
	 * 
	 * @param position1 the first position
	 * @param position2 the second position
	 * 
	 * @return the normalized distance between the two positions in this
	 *         environment
	 */
	public double getNormalizedDistance(Position position1, Position position2);

	
	/**
	 * Gets the step cost from an origin to a destination position within this
	 * environment between a start and an end time given a cost policy and
	 * risk policy.
	 * 
	 * @param origin the origin position in globe coordinates
	 * @param destination the destination position in globe coordinates
	 * @param start the start time
	 * @param end the end time
	 * @param costPolicy the cost policy
	 * @param riskPolicy the risk policy
	 * 
	 * @return the step cost from the origin to the destination position
	 */
	public double getStepCost(
			Position origin, Position destination,
			ZonedDateTime start, ZonedDateTime end,
			CostPolicy costPolicy, RiskPolicy riskPolicy);
	
	/**
	 * Gets the leg cost from an origin to a destination position within this
	 * environment between a start and an end time given a cost policy and
	 * risk policy.
	 * 
	 * @param origin the origin position in globe coordinates
	 * @param destination the destination position in globe coordinates
	 * @param start the start time
	 * @param end the end time
	 * @param costPolicy the cost policy
	 * @param riskPolicy the risk policy
	 * 
	 * @return the leg cost from the origin to the destination position
	 */
	public double getLegCost(
			Position origin, Position destination,
			ZonedDateTime start, ZonedDateTime end,
			CostPolicy costPolicy, RiskPolicy riskPolicy);
	
	/**
	 * Gets the leg cost from the center of this environment to the center of
	 * another environment between a start and an end time given a cost
	 * policy and risk policy.
	 * 
	 * @param destination the destination environment
	 * @param start the start time
	 * @param end the end time
	 * @param costPolicy the cost policy
	 * @param riskPolicy the risk policy
	 * 
	 * @return the leg cost from the center of this environment to the center
	 *         of the destination environment
	 */
	public double getLegCost(
			Environment destination,
			ZonedDateTime start, ZonedDateTime end,
			CostPolicy costPolicy, RiskPolicy riskPolicy);
	
}
