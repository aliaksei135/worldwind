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
package com.cfar.swim.worldwind.tests;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.cfar.swim.worldwind.ai.rrt.basicrrt.RRTreePlanner;
import com.cfar.swim.worldwind.aircraft.CombatIdentification;
import com.cfar.swim.worldwind.aircraft.Iris;
import com.cfar.swim.worldwind.geom.Box;
import com.cfar.swim.worldwind.planning.SamplingEnvironment;
import com.google.common.collect.Iterables;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.Path;

/**
 * @author Manuel Rosa
 *
 */
public class RRTreePlannerTest {
	
	@Test
	public void RRTreeTester() {
		Globe globe = new Earth();
		Sector cadboroBay = new Sector(
            	Angle.fromDegrees(48.44),
            	Angle.fromDegrees(48.46),
            	Angle.fromDegrees(-123.29),
        		Angle.fromDegrees(-123.27)
            	);
        gov.nasa.worldwind.geom.Box boxNASA = Sector.computeBoundingBox(globe, 1.0, cadboroBay, 0.0, 150.0);
        
		// Create environment from box
		SamplingEnvironment samplingEnv = new SamplingEnvironment(new Box(boxNASA));
		samplingEnv.setGlobe(globe);
		
		// Set planner inputs
		Position origin = Position.fromDegrees(48.445, -123.285, 10);
		Position destination = Position.fromDegrees(48.455, -123.275, 100);
        ZonedDateTime etd = ZonedDateTime.of(LocalDate.of(2018, 5, 1), LocalTime.of(15, 0), ZoneId.of("America/Los_Angeles"));
        Iris iris = new Iris(origin, 5000, CombatIdentification.FRIEND);
        
        // CreatePlanner
        RRTreePlanner plannerRRT;
        // Bias 5%
        plannerRRT = new RRTreePlanner(iris, samplingEnv, 250, 5, 1500);
        
        // Compute plans
        Path path0 = plannerRRT.plan(origin, destination, etd);
        int size = Iterables.size(path0.getPositions());
        int waypoints = plannerRRT.getWaypointList().size();
        System.out.println(String.format("Waypoints created: %d Path size: %d", waypoints, size));
        assertTrue("RRTree should find a path", size>0);
        
        Path path1 = plannerRRT.plan(origin, destination, etd);
        size = Iterables.size(path1.getPositions());
        waypoints = plannerRRT.getWaypointList().size();
        System.out.println(String.format("Waypoints created: %d Path size: %d", waypoints, size));
        assertTrue("RRTree should find a path", size>0);
        
        // Bias 1%
        plannerRRT = new RRTreePlanner(iris, samplingEnv, 250, 1, 1500);
        
        Path path2 = plannerRRT.plan(origin, destination, etd);
        size = Iterables.size(path2.getPositions());
        waypoints = plannerRRT.getWaypointList().size();
        System.out.println(String.format("Waypoints created: %d Path size: %d", waypoints, size));
        assertTrue("RRTree should find a path", size>0);
        
        Path path3 = plannerRRT.plan(origin, destination, etd);
        size = Iterables.size(path3.getPositions());
        waypoints = plannerRRT.getWaypointList().size();
        System.out.println(String.format("Waypoints created: %d Path size: %d", waypoints, size));
        assertTrue("RRTree should find a path", size>0);
        
        System.out.println();
	}

}