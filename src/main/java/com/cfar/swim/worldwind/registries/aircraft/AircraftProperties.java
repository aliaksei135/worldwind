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
package com.cfar.swim.worldwind.registries.aircraft;

import com.cfar.swim.worldwind.aircraft.Aircraft;
import com.cfar.swim.worldwind.aircraft.CombatIdentification;
import com.cfar.swim.worldwind.registries.Properties;

/**
 * Abstracts aircraft properties applicable to all aircraft.
 * 
 * @author Stephan Heinemann
 *
 */
public abstract class AircraftProperties implements Properties<Aircraft> {

	/** the combat identification of this aircraft properties bean */
	private CombatIdentification combatIdentification;
	
	/** the separation radius of this aircraft properties bean */
	private double separationRadius;
	
	/** the maximum angle of climb speed of this aircraft properties bean */
	private double maximumAngleOfClimbSpeed;
	
	/** the maximum rate of climb speed of this aircraft properties bean */
	private double maximumRateOfClimbSpeed;
	
	/** the cruise climb speed of this aircraft properties bean */
	private double cruiseClimbSpeed;
	
	/** the cruise speed of this aircraft properties bean */
	private double cruiseSpeed;
	
	/** the cruise descent speed of this aircraft properties bean */
	private double cruiseDescentSpeed;
	
	/** the approach speed of this aircraft properties bean */
	private double approachSpeed;
	
	/** the maximum glide speed of this aircraft properties bean */
	private double maximumGlideSpeed;
	
	/** the maximum rate of descent speed of this aircraft properties bean */
	private double maximumRateOfDescentSpeed;
	
	/** the maximum speed of this aircraft properties bean */
	private double maximumSpeed;
	
	/** the maximum rate of climb of this aircraft properties bean */
	private double maximumRateOfClimb;
	
	/** the maximum cruise rate of climb of this aircraft properties bean */
	private double cruiseRateOfClimb;
	
	/** the cruise rate of descent of this aircraft properties bean */
	private double cruiseRateOfDescent;
	
	/** the approach rate of descent of this aircraft properties bean */
	private double approachRateOfDescent;
	
	/** the maximum rate of descent of this aircraft properties bean */
	private double maximumRateOfDescent;
	
	/** the maximum angle of climb of this aircraft properties bean */
	private double maximumAngleOfClimb;
	
	/**
	 * Constructs a new aircraft properties bean.
	 */
	public AircraftProperties() {
		this.combatIdentification = CombatIdentification.FRIEND;
		
		this.maximumAngleOfClimbSpeed = 0d;
		this.maximumRateOfClimbSpeed = 0d;
		this.cruiseClimbSpeed = 0d;
		this.cruiseSpeed = 0d;
		this.cruiseDescentSpeed = 0d;
		this.approachSpeed = 0d;
		this.maximumGlideSpeed = 0d;
		this.maximumRateOfDescentSpeed = 0d;
		this.maximumSpeed = 0d;
		
		this.maximumRateOfClimb = 0d;
		this.cruiseRateOfClimb = 0d;
		this.cruiseRateOfDescent = 0d;
		this.approachRateOfDescent = 0d;
		this.maximumRateOfDescent = 0d;
		
		this.maximumAngleOfClimb = 0d; 
	}
	
	/**
	 * Gets the combat identification of this aircraft property bean.
	 * 
	 * @return the combat identification of this aircraft property bean
	 */
	public CombatIdentification getCombatIdentification() {
		return this.combatIdentification;
	}

	/**
	 * Sets the combat identification of this aircraft property bean.
	 * 
	 * @param combatIdentification the combat identification to be set
	 */
	public void setCombatIdentification(CombatIdentification combatIdentification) {
		this.combatIdentification = combatIdentification;
	}

	/**
	 * Gets the separation radius of this aircraft property bean.
	 * 
	 * @return the separation radius of this aircraft property bean
	 */
	public double getSeparationRadius() {
		return this.separationRadius;
	}
	
	/**
	 * Sets the separation radius of this aircraft property bean.
	 * 
	 * @param separationRadius the separation radius to be set
	 */
	public void setSeparationRadius(double separationRadius) {
		this.separationRadius = separationRadius;
	}

	/**
	 * Gets the maximum angle of climb speed of this aircraft property bean.
	 * 
	 * @return the maximum angle of climb speed of this aircraft property bean
	 */
	public double getMaximumAngleOfClimbSpeed() {
		return maximumAngleOfClimbSpeed;
	}
	
	/**
	 * Sets the maximum angle of climb speed of this aircraft property bean.
	 * 
	 * @param maximumAngleOfClimbSpeed the maximum angle of climb speed to be set
	 */
	public void setMaximumAngleOfClimbSpeed(double maximumAngleOfClimbSpeed) {
		this.maximumAngleOfClimbSpeed = maximumAngleOfClimbSpeed;
	}
	
	/**
	 * Gets the maximum rate of climb speed of this aircraft property bean.
	 * 
	 * @return the maximum rate of climb speed of this aircraft property bean
	 */
	public double getMaximumRateOfClimbSpeed() {
		return maximumRateOfClimbSpeed;
	}
	
	/**
	 * Sets the maximum rate of climb speed of this aircraft property bean.
	 * 
	 * @param maximumRateOfClimbSpeed the maximum rate of climb speed to be set
	 */
	public void setMaximumRateOfClimbSpeed(double maximumRateOfClimbSpeed) {
		this.maximumRateOfClimbSpeed = maximumRateOfClimbSpeed;
	}
	
	/**
	 * Gets the cruise climb speed of this aircraft property bean.
	 * 
	 * @return the cruise climb speed of this aircraft property bean
	 */
	public double getCruiseClimbSpeed() {
		return cruiseClimbSpeed;
	}
	
	/**
	 * Sets the cruise climb speed of this aircraft property bean.
	 * 
	 * @param cruiseClimbSpeed the cruise climb speed to be set
	 */
	public void setCruiseClimbSpeed(double cruiseClimbSpeed) {
		this.cruiseClimbSpeed = cruiseClimbSpeed;
	}
	
	/**
	 * Gets the cruise speed of this aircraft property bean.
	 * 
	 * @return the cruise speed of this aircraft property bean
	 */
	public double getCruiseSpeed() {
		return this.cruiseSpeed;
	}
	
	/**
	 * Sets the cruise speed of this aircraft property bean.
	 * 
	 * @param cruise speed the cruise speed to be set
	 */
	public void setCruiseSpeed(double cruiseSpeed) {
		this.cruiseSpeed = cruiseSpeed;
	}
	
	/**
	 * Gets the cruise descent speed of this aircraft property bean.
	 * 
	 * @return the cruise descent speed of this aircraft property bean
	 */
	public double getCruiseDescentSpeed() {
		return cruiseDescentSpeed;
	}
	
	/**
	 * Sets the cruise descent speed of this aircraft property bean.
	 * 
	 * @param cruiseDescentSpeed the cruies descent speed to be set
	 */
	public void setCruiseDescentSpeed(double cruiseDescentSpeed) {
		this.cruiseDescentSpeed = cruiseDescentSpeed;
	}
	
	/**
	 * Gets the approach speed of this aircraft property bean.
	 * 
	 * @return the approach speed of this aircraft property bean
	 */
	public double getApproachSpeed() {
		return approachSpeed;
	}
	
	/**
	 * Sets the approach speed of this aircraft property bean.
	 * 
	 * @param approachSpeed the approach speed to be set
	 */
	public void setApproachSpeed(double approachSpeed) {
		this.approachSpeed = approachSpeed;
	}
	
	/**
	 * Gets the maximum glide speed of this aircraft property bean.
	 * 
	 * @return the maximum glide speed of this aircraft property bean
	 */
	public double getMaximumGlideSpeed() {
		return maximumGlideSpeed;
	}
	
	/**
	 * Sets the maximum glide speed of this aircraft property bean.
	 * 
	 * @param maximumGlideSpeed the maximum glide speed to be set
	 */
	public void setMaximumGlideSpeed(double maximumGlideSpeed) {
		this.maximumGlideSpeed = maximumGlideSpeed;
	}
	
	/**
	 * Gets the maximum rate of descent speed of this aircraft property bean.
	 * 
	 * @return the maximum rate of descent speed of this aircraft property bean
	 */
	public double getMaximumRateOfDescentSpeed() {
		return maximumRateOfDescentSpeed;
	}
	
	/**
	 * Sets the maximum rate of descent speed of this aircraft property bean.
	 * 
	 * @param maximumRateOfDescentSpeed the the maximum rate of descent speed to be set
	 */
	public void setMaximumRateOfDescentSpeed(double maximumRateOfDescentSpeed) {
		this.maximumRateOfDescentSpeed = maximumRateOfDescentSpeed;
	}
	
	/**
	 * Gets the maximum speed of this aircraft property bean.
	 * 
	 * @return the maximum speed of this aircraft property bean
	 */
	public double getMaximumSpeed() {
		return maximumSpeed;
	}
	
	/**
	 * Sets the maximum speed of this aircraft property bean.
	 * 
	 * @param maximumSpeed the maximum speed to be set
	 */
	public void setMaximumSpeed(double maximumSpeed) {
		this.maximumSpeed = maximumSpeed;
	}

	/**
	 * Gets the maximum rate of climb of this aircraft property bean.
	 * 
	 * @return the maximum rate of climb of this aircraft property bean
	 */
	public double getMaximumRateOfClimb() {
		return maximumRateOfClimb;
	}
	
	/**
	 * Sets the maximum rate of climb of this aircraft property bean.
	 * 
	 * @param maximumRateOfClimb the maximumRateOfClimb to be set
	 */
	public void setMaximumRateOfClimb(double maximumRateOfClimb) {
		this.maximumRateOfClimb = maximumRateOfClimb;
	}
	
	/**
	 * Gets the cruise rate of climb of this aircraft property bean.
	 * 
	 * @return the cruise rate of climb of this aircraft property bean
	 */
	public double getCruiseRateOfClimb() {
		return cruiseRateOfClimb;
	}
	
	/**
	 * Sets the cruise rate of climb of this aircraft property bean.
	 * 
	 * @param cruiseRateOfClimb the cruise rate of climb to be set
	 */
	public void setCruiseRateOfClimb(double cruiseRateOfClimb) {
		this.cruiseRateOfClimb = cruiseRateOfClimb;
	}
	
	/**
	 * Gets the cruise rate of descent of this aircraft property bean.
	 * 
	 * @return the cruise rate of descent of this aircraft property bean
	 */
	public double getCruiseRateOfDescent() {
		return cruiseRateOfDescent;
	}
	
	/**
	 * Sets the cruise rate of descent of this aircraft property bean.
	 * 
	 * @param cruiseRateOfDescent the cruise rate of descent to be set
	 */
	public void setCruiseRateOfDescent(double cruiseRateOfDescent) {
		this.cruiseRateOfDescent = cruiseRateOfDescent;
	}
	
	/**
	 * Gets the approach rate of descent of this aircraft property bean.
	 * 
	 * @return the approach rate of descent of this aircraft property bean
	 */
	public double getApproachRateOfDescent() {
		return approachRateOfDescent;
	}
	
	/**
	 * Sets the approach rate of descent of this aircraft property bean.
	 * 
	 * @param approachRateOfDescent the approach rate of descent to be set
	 */
	public void setApproachRateOfDescent(double approachRateOfDescent) {
		this.approachRateOfDescent = approachRateOfDescent;
	}
	
	/**
	 * Gets the maximum rate of descent of this aircraft property bean.
	 * 
	 * @return the maximum rate of descent of this aircraft property bean
	 */
	public double getMaximumRateOfDescent() {
		return maximumRateOfDescent;
	}
	
	/**
	 * Sets the maximum rate of descent of this aircraft property bean.
	 * 
	 * @param maximumRateOfDescent the maximum rate of descent to be set
	 */
	public void setMaximumRateOfDescent(double maximumRateOfDescent) {
		this.maximumRateOfDescent = maximumRateOfDescent;
	}
	
	/**
	 * Gets the maximum angle of climb of this aircraft property bean.
	 * 
	 * @return the maximum angle of climb of this aircraft property bean
	 */
	public double getMaximumAngleOfClimb() {
		return maximumAngleOfClimb;
	}
	
	/**
	 * Sets the maximum angle of climb of this aircraft property bean.
	 * 
	 * @param maximumAngleOfClimb the maximum angle of climb to be set
	 */
	public void setMaximumAngleOfClimb(double maximumAngleOfClimb) {
		this.maximumAngleOfClimb = maximumAngleOfClimb;
	}

	/**
	 * Clones this aircraft properties bean.
	 * 
	 * @return a clone of this aircraft properties bean
	 * 
	 * @see Properties#clone()
	 */
	@Override
	public AircraftProperties clone() {
		AircraftProperties clone = null;
		try {
			clone = (AircraftProperties) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}
}
