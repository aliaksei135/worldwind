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
package com.cfar.swim.worldwind.connections;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Path;

/**
 * Abstracts a datalink connection to connect to and communicate with aircraft.
 * 
 * @author Stephan Heinemann
 *
 */
public abstract class Datalink implements Connection {
	
	/** the property change support of this datalink */
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	// TODO: Track as own class similar to Trajectory of Waypoints storing ATO
	
	/** the track of the source of this datalink */
	private Queue<Position> track = new ConcurrentLinkedQueue<>();
	
	/** the monitor of this datalink */
	ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
	
	/**
	 * Gets the track of the aircraft connected via this datalink.
	 * 
	 * @return the track of the aircraft connected via this datalink
	 */
	public Iterable<Position> getTrack() {
		// TODO: return Path, Trajectory or Track with set ATOs
		return this.track;
	}
	
	/**
	 * Connects this datalink.
	 * 
	 * @see Connection#connect()
	 */
	@Override
	public abstract void connect();
	
	/**
	 * Disconnect this datalink.
	 * 
	 * @see Connection#connect()
	 */
	@Override
	public abstract void disconnect();
	
	/**
	 * Indicates whether or not this datalink is connected.
	 * 
	 * @return true if this datalink is connected, false otherwise
	 * 
	 * @see Connection#isConnected()
	 */
	@Override
	public abstract boolean isConnected();
	
	/**
	 * Gets the aircraft position via this datalink.
	 * 
	 * @return the aircraft position obtained via this datalink
	 */
	public abstract Position getAircraftPosition();
	
	/**
	 * Gets the aircraft mode via this datalink.
	 * 
	 * @return the aircraft mode obtained via this datalink
	 */
	public abstract String getAircraftMode();
	
	/**
	 * Sets the aircraft mode via this datalink.
	 * 
	 * @param aircraftMode the aircraft mode to be set
	 */
	public abstract void setAircraftMode(String aircraftMode);
	
	/**
	 * Enables the aircraft safety via this datalink.
	 */
	public abstract void enableAircraftSafety();
	
	/**
	 * Disables the aircraft safety via this datalink.
	 */
	public abstract void disableAircraftSafety();
	
	/**
	 * Indicates whether or not the aircraft safety is enabled for the aircraft
	 * connected via this datalink.
	 * 
	 * @return true if the aircraft safety is enabled, false otherwise
	 */
	public abstract boolean isAircraftSafetyEnabled();
	
	/**
	 * Arms the aircraft via this datalink.
	 */
	public abstract void armAircraft();
	
	/**
	 * Disarms the aircraft via this datalink.
	 */
	public abstract void disarmAircraft();
	
	/**
	 * Indicates whether or not the aircraft connected via this datalink
	 * is armed.
	 * 
	 * @return true if the aircraft is armed, false otherwise
	 */
	public abstract boolean isAircraftArmed();
	
	/**
	 * Uploads a flight path to the aircraft connected via this datalink.
	 * 
	 * @param path the flight path to be uploaded
	 */
	public abstract void uploadFlightPath(Path path);
	
	// TODO: take-off specification / setup
	// flight envelope (initial altitude, vertical speed, horizontal speed)
	
	/**
	 * Initiates a take-off for the aircraft connected via this datalink.
	 */
	public abstract void takeOff();
	
	/**
	 * Initiates a landing for the aircraft connected via this datalink.
	 */
	public abstract void land();
	
	/**
	 * Initiates a return to and landing at the launch position for the
	 * aircraft connected via this datalink.
	 */
	public abstract void returnToLaunch();
	
	/**
	 * Starts the datalink monitor with a specified monitoring period.
	 * 
	 * @param period the monitoring period in milliseconds
	 */
	public void startMonitoring(long period) {
		this.track.clear();
		this.monitor.scheduleAtFixedRate(new DatalinkMonitor(), 0, period, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Stops the datalink monitor.
	 */
	public void stopMonitoring() {
		this.monitor.shutdown();
	}
	
	/**
	 * Realizes a datalink monitor.
	 * 
	 * @author Stephan Heinemann
	 *
	 */
	private class DatalinkMonitor implements Runnable {
		
		/**
		 * Monitors the datalink and fires registered property change listeners.
		 * 
		 * @see Runnable#run()
		 */
		@Override
		public void run() {
			track.add(getAircraftPosition());
			pcs.firePropertyChange("track", null, track);
			// TODO: extend monitored properties
		}
	}
	
	/**
	 * Adds a property change listener to this datalink.
	 * 
	 * @param listener the property change listener to be added
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}
	
	/**
	 * Removes a property change listener from this datalink.
	 * 
	 * @param listener the property change listener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}
	
	/**
	 * Adds a track change listener to this datalink.
	 * 
	 * @param listener the position change listener to be added
	 */
	public void addTrackChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener("track", listener);
	}
	
}
