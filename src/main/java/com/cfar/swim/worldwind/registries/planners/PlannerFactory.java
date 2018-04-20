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
package com.cfar.swim.worldwind.registries.planners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.cfar.swim.worldwind.ai.Planner;
import com.cfar.swim.worldwind.ai.astar.arastar.ARAStarPlanner;
import com.cfar.swim.worldwind.ai.astar.astar.ForwardAStarPlanner;
import com.cfar.swim.worldwind.ai.astar.thetastar.ThetaStarPlanner;
import com.cfar.swim.worldwind.ai.prm.basicprm.BasicPRM;
import com.cfar.swim.worldwind.ai.prm.lazyprm.LazyPRM;
import com.cfar.swim.worldwind.ai.rrt.basicrrt.RRTreePlanner;
import com.cfar.swim.worldwind.ai.rrt.hrrt.HRRTreePlanner;
import com.cfar.swim.worldwind.registries.Factory;
import com.cfar.swim.worldwind.registries.Specification;
import com.cfar.swim.worldwind.session.Scenario;

/**
 * Realizes a planner factory to create planners according to customized planner
 * specifications.
 * 
 * @author Stephan Heinemann
 * 
 * @see Factory
 * @see Specification
 */
public class PlannerFactory implements Factory<Planner> {

	// TODO: Both, environment and planner factory need the active scenario
	// maybe pull up scenario functionality into abstract factory

	/** the scenario of this planner factory */
	private Scenario scenario;

	/** the active scenario change listener of this planner factory */
	private ActiveScenarioChangeListener ascl = new ActiveScenarioChangeListener();

	/**
	 * Constructs a new planner factory with a specified scenario. The scenario
	 * aggregates an aircraft and environment which shall not be part of the planner
	 * specification.
	 * 
	 * @param scenario the scenario of this planner factory
	 */
	public PlannerFactory(Scenario scenario) {
		this.scenario = scenario;
	}

	/**
	 * Gets the scenario of this planner factory.
	 * 
	 * @return the scenario of this planner factory
	 */
	public Scenario getScenario() {
		return this.scenario;
	}

	/**
	 * Sets the scenario of this planner factory.
	 * 
	 * @param scenario the scenario to be set
	 */
	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	/**
	 * Gets the active scenario change listener of this planner factory.
	 * 
	 * @return the active scenario change listener of this planner factory
	 */
	public PropertyChangeListener getActiveScenarioChangeListener() {
		return this.ascl;
	}

	/**
	 * Creates a new planner according to a customized planner specification.
	 * 
	 * @param specification the customized planner specification
	 * 
	 * @return the created planner
	 * 
	 * @see Factory#createInstance(Specification)
	 */
	@Override
	public Planner createInstance(Specification<Planner> specification) {
		Planner planner = null;

		// TODO: validate scenario for planner creation? (supports)

		if (specification.getId().equals(Specification.PLANNER_FAS_ID)) {
			ForwardAStarProperties properties = (ForwardAStarProperties) specification.getProperties();
			planner = new ForwardAStarPlanner(scenario.getAircraft(), scenario.getEnvironment());
			planner.setCostPolicy(properties.getCostPolicy());
			planner.setRiskPolicy(properties.getRiskPolicy());
		} else if (specification.getId().equals(Specification.PLANNER_TS_ID)) {
			ThetaStarProperties properties = (ThetaStarProperties) specification.getProperties();
			planner = new ThetaStarPlanner(scenario.getAircraft(), scenario.getEnvironment());
			planner.setCostPolicy(properties.getCostPolicy());
			planner.setRiskPolicy(properties.getRiskPolicy());
		} else if (specification.getId().equals(Specification.PLANNER_ARAS_ID)) {
			ARAStarProperties properties = (ARAStarProperties) specification.getProperties();
			planner = new ARAStarPlanner(scenario.getAircraft(), scenario.getEnvironment());
			planner.setCostPolicy(properties.getCostPolicy());
			planner.setRiskPolicy(properties.getRiskPolicy());
			((ARAStarPlanner) planner).setMinimumQuality(properties.getMinimumQuality());
			((ARAStarPlanner) planner).setMaximumQuality(properties.getMaximumQuality());
			((ARAStarPlanner) planner).setQualityImprovement(properties.getQualityImprovement());
		} else if (specification.getId().equals(Specification.PLANNER_PRM_ID)) {
			BasicPRMProperties properties = (BasicPRMProperties) specification.getProperties();
			planner = new BasicPRM(scenario.getAircraft(), scenario.getEnvironment(), properties.getMaxIter(),
					properties.getMaxNeighbors(), properties.getMaxDistance());
			planner.setCostPolicy(properties.getCostPolicy());
			planner.setRiskPolicy(properties.getRiskPolicy());
		} else if (specification.getId().equals(Specification.PLANNER_LAZYPRM_ID)) {
			LazyPRMProperties properties = (LazyPRMProperties) specification.getProperties();
			planner = new LazyPRM(scenario.getAircraft(), scenario.getEnvironment(), properties.getMaxIter(),
					properties.getMaxNeighbors(), properties.getMaxDistance());
			planner.setCostPolicy(properties.getCostPolicy());
			planner.setRiskPolicy(properties.getRiskPolicy());
		} else if (specification.getId().equals(Specification.PLANNER_RRT_ID)) {
			RRTreeProperties properties = (RRTreeProperties) specification.getProperties();
			planner = new RRTreePlanner(scenario.getAircraft(), scenario.getEnvironment(), properties.getEpsilon(),
					properties.getBias(), properties.getMaxIter());
			planner.setCostPolicy(properties.getCostPolicy());
			planner.setRiskPolicy(properties.getRiskPolicy());
			((RRTreePlanner) planner).setStrategy(properties.getStrategy());
		} else if (specification.getId().equals(Specification.PLANNER_HRRT_ID)) {
			HRRTreeProperties properties = (HRRTreeProperties) specification.getProperties();
			planner = new HRRTreePlanner(scenario.getAircraft(), scenario.getEnvironment(),
					properties.getEpsilon(), properties.getBias(), properties.getMaxIter(),
					properties.getProbFloor(), properties.getNeighbors());
			planner.setCostPolicy(properties.getCostPolicy());
			planner.setRiskPolicy(properties.getRiskPolicy());
			((HRRTreePlanner) planner).setStrategy(properties.getStrategy());
			((HRRTreePlanner) planner).setHeuristic(properties.getHeuristic());
		}
		// TODO: implement more planners

		return planner;
	}

	/**
	 * Realizes an active scenario change listener for this planner factory.
	 * 
	 * @author Stephan Heinemann
	 * 
	 */
	private class ActiveScenarioChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() instanceof Scenario) {
				scenario = (Scenario) evt.getNewValue();
			}
		}
	}

}
