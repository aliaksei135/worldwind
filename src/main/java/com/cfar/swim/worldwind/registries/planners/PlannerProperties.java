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

import com.cfar.swim.worldwind.ai.Planner;
import com.cfar.swim.worldwind.planning.CostPolicy;
import com.cfar.swim.worldwind.planning.RiskPolicy;
import com.cfar.swim.worldwind.registries.Properties;

/**
 * Abstracts planner properties applicable to all planners.
 * 
 * @author Stephan Heinemann
 *
 */
public abstract class PlannerProperties implements Properties<Planner> {

	/** the cost policy of this planner properties bean */
	CostPolicy costPolicy;
	
	/** the risk policy of this planner properties bean */
	RiskPolicy riskPolicy;
	
	/**
	 * Constructs a new planner properties bean using default cost and risk
	 * policy property values.
	 */
	public PlannerProperties() {
		this.costPolicy = CostPolicy.AVERAGE;
		this.riskPolicy = RiskPolicy.SAFETY;
	}
	
	/**
	 * Constructs a new planner properties bean with specified cost and risk
	 * policy property values.
	 * 
	 * @param costPolicy the cost policy of this planner properties bean
	 * @param riskPolicy the risk policy of this planner properties bean
	 */
	public PlannerProperties(CostPolicy costPolicy, RiskPolicy riskPolicy) {
		this.costPolicy = costPolicy;
		this.riskPolicy = riskPolicy;
	}
	
	/**
	 * Gets the cost policy of this planner properties bean.
	 * 
	 * @return the cost policy of this planner properties bean
	 */
	public CostPolicy getCostPolicy() {
		return this.costPolicy;
	}
	
	/**
	 * Sets the cost policy of this planner properties bean.
	 * 
	 * @param costPolicy the cost policy to be set
	 */
	public void setCostPolicy(CostPolicy costPolicy) {
		this.costPolicy = costPolicy;
	}
	
	/**
	 * Gets the risk policy of this planner properties bean.
	 * 
	 * @return the risk policy of this planner properties bean
	 */
	public RiskPolicy getRiskPolicy() {
		return this.riskPolicy;
	}
	
	/**
	 * Sets the risk policy of this planner properties bean.
	 * 
	 * @param riskPolicy the risk policy to be set
	 */
	public void setRiskPolicy(RiskPolicy riskPolicy) {
		this.riskPolicy = riskPolicy;
	}
	
	/**
	 * Clones this planner properties bean.
	 * 
	 * @return a clone of this planner properties bean
	 * 
	 * @see Properties#clone()
	 */
	@Override
	public PlannerProperties clone() {
		PlannerProperties clone = null;
		try {
			clone = (PlannerProperties) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}
	
}
