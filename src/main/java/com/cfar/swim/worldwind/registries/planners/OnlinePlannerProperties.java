/**
 * Copyright (c) 2018, Manuel Rosa (UVic Center for Aerospace Research)
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

/**
 * Describes the properties bean of an online planner.
 * 
 * @author Manuel Rosa
 *
 */
public interface OnlinePlannerProperties extends PlannerProperties {

	/**
	 * Checks if the online capabilities of the planner mode are active or not.
	 * 
	 * @return true if the planner mode is set to online, false otherwise
	 */
	public boolean isOnline();
	
	/**
	 * Sets the online capabilities of the planner as are active or not.
	 * 
	 * @param online the state of the online capabilities
	 */
	public void setOnline(boolean online);
	
	/**
	 * Gets the  time step to update the current position of the aircraft.
	 * 
	 * @return the time step to update the current position
	 */
	public double getUpdateStep();

	/**
	 * Sets the time step to update the current position of the aircraft.
	 * 
	 * @param updateStep the time step to update the current position
	 */
	public void setUpdateStep(double updateStep);

	/**
	 * Gets the distance threshold to consider a position displacement as worthy of a new plan.
	 * 
	 * @return the distance threshold for each position
	 */
	public double getPositionThreshold();

	/**
	 * Sets the distance threshold to consider a position displacement as worthy of a new plan.
	 * 
	 * @param positionThreshold the distance threshold for each position
	 */
	public void setPositionThreshold(double positionThreshold);

}