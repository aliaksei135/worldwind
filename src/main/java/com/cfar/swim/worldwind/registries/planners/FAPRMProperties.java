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
package com.cfar.swim.worldwind.registries.planners;

import com.cfar.swim.worldwind.ai.prm.rigidprm.QueryMode;

/**
 * Realizes the properties bean of a FA PRM planner.
 * 
 * @author Henrique Ferreira
 *
 */
public class FAPRMProperties extends AbstractPlannerProperties implements AnytimePlannerProperties {

	/** the description of this planner properties bean */
	private final static String DESCRIPTION_FAPRM = "Flexible Anytime PRM: the sampling strategy consists of a trade-off between coverage of the configuration space and a heuristic optimization of the path cost and desirability. It has anytime capabilities.";

	/** the maximum number of neighbors a waypoint can be connected to */
	private int maxNeighbors = 5;

	/** the maximum distance between two neighboring waypoints */
	private double maxDistance = 60d;

	/** the bias of the sampling algorithm towards goal */
	private int bias = 5;

	/** the initial inflation factor (initial beta value) */
	private double minimumQuality = 0d;

	/** the final inflation factor (final beta value) */
	private double maximumQuality = 1d;

	/** the inflation amount to be applied to the current inflation */
	private double qualityImprovement = 0.3;

	/** the parameter lambda that weights the desirability influence on the cost */
	private double lambda = 0.5;

	/** the improvement factor for the cost of each new generated solution */
	private double solutionImprovement = 0.05;
	
	/** the query mode of this FA PRM properties */
	protected QueryMode mode = QueryMode.SINGLE;

	/**
	 * Constructs a new FA PRM planner properties bean.
	 */
	public FAPRMProperties() {
		super();
		this.setMinimumQuality(0d);
		this.setMaximumQuality(1d);
		this.setQualityImprovement(0.3);
		this.setDescription(DESCRIPTION_FAPRM);
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
	 * Gets the minimum quality (initial inflation) of this FA PRM properties bean.
	 * 
	 * @return the minimum quality (initial inflation) of this FA PRM properties
	 *         bean
	 * 
	 * @see AnytimePlannerProperties#getMinimumQuality()
	 */
	@Override
	public double getMinimumQuality() {
		return this.minimumQuality;
	}

	/**
	 * Sets the minimum quality (initial inflation) of this FA PRM properties bean.
	 * 
	 * @param initialInflation the minimum quality (initial inflation) of this FAD
	 *            PRM properties bean
	 * 
	 * @throws IllegalArgumentException if the initial inflation is invalid
	 * 
	 * @see AnytimePlannerProperties#setMinimumQuality(double)
	 */
	@Override
	public void setMinimumQuality(double initialInflation) {
		if ((0d <= initialInflation) && (initialInflation <= this.maximumQuality)) {
			this.minimumQuality = initialInflation;
		} else {
			throw new IllegalArgumentException("initial inflation is invalid");
		}
	}

	/**
	 * Gets the maximum quality (final inflation) of this FA PRM properties bean.
	 * 
	 * @return the maximum quality (final inflation) of this FA PRM properties bean
	 * 
	 * @see AnytimePlannerProperties#getMaximumQuality()
	 */
	@Override
	public double getMaximumQuality() {
		return this.maximumQuality;
	}

	/**
	 * Sets the maximum quality (final inflation) of this FA PRM properties bean.
	 * 
	 * @param finalInflation the maximum quality (final inflation) of this FA PRM
	 *            properties bean
	 * 
	 * @throws IllegalArgumentException if the final inflation is invalid
	 * 
	 * @see AnytimePlannerProperties#setMaximumQuality(double)
	 */
	@Override
	public void setMaximumQuality(double finalInflation) {
		if ((1d >= finalInflation) && (this.minimumQuality <= finalInflation)) {
			this.maximumQuality = finalInflation;
		} else {
			throw new IllegalArgumentException("final deflation is invalid");
		}
	}

	/**
	 * Gets the quality improvement (inflation amount) of this FA PRM properties
	 * bean.
	 * 
	 * @return the quality improvement (inflation amount) of this FA PRM properties
	 *         bean
	 * 
	 * @see AnytimePlannerProperties#getQualityImprovement()
	 */
	@Override
	public double getQualityImprovement() {
		return this.qualityImprovement;
	}

	/**
	 * Sets the quality improvement (inflation amount) of this FA PRM properties
	 * bean.
	 * 
	 * @param inflationAmount the quality improvement (inflation amount) of this FAD
	 *            PRM properties bean
	 * 
	 * @throws IllegalArgumentException if the inflation amount is invalid
	 * 
	 * @see AnytimePlannerProperties#setQualityImprovement(double)
	 */
	@Override
	public void setQualityImprovement(double inflationAmount) {
		if (0d < inflationAmount) {
			this.qualityImprovement = inflationAmount;
		} else {
			throw new IllegalArgumentException("inflation amount is invalid");
		}
	}

	/**
	 * Gets the bias of sampling towards the goal of this FA PRM properties bean.
	 * 
	 * @return the bias of sampling towards the goal
	 */
	public int getBias() {
		return bias;
	}

	/**
	 * Sets the bias of sampling towards the goal of this FA PRM properties bean.
	 * 
	 * @param the bias of sampling towards the goal
	 */
	public void setBias(int bias) {
		this.bias = bias;
	}

	/**
	 * Gets the parameter lambda of this FA PRM properties bean.
	 * 
	 * @return the lambda the parameter lambda of this FA PRM properties
	 */
	public double getLambda() {
		return lambda;
	}

	/**
	 * Sets the parameter lambda of this FA PRM properties bean.
	 * 
	 * @param lambda the parameter lambda to set
	 */
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	/**
	 * Gets the query mode of this planner.
	 * 
	 * @return the mode the query mode
	 */
	public QueryMode getMode() {
		return mode;
	}

	/**
	 * Sets the query mode of this planner.
	 * 
	 * @param mode the mode to set
	 */
	public void setMode(QueryMode mode) {
		this.mode = mode;
	}

	/**
	 * Gets the improvement factor for the cost of each new generated solution
	 * 
	 * @return the solutionImprovement the improvement factor for the cost of each
	 *         new generated solution
	 */
	public double getSolutionImprovement() {
		return solutionImprovement;
	}

	/**
	 * Sets the improvement factor for the cost of each new generated solution
	 * 
	 * @param solutionImprovement the improvement factor for the cost of each new
	 *            generated solution
	 */
	public void setSolutionImprovement(double solutionImprovement) {
		this.solutionImprovement = solutionImprovement;
	}

}