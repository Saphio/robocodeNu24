package sw;
import robocode.*;
import java.awt.*;

import robocode.TeamRobot;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * SophiaWang - a robot by Sophia Wang
 */
public class SophiaWang extends TeamRobot {

	// Default behavior
	public void run() {
		// COLORS
		setColors(new Color(98, 169, 124), new Color(126, 224, 129), new Color(195, 243, 192));
		setBulletColor(Color.green);
		setScanColor(Color.green);
		
		// MAIN - TODO
		while(true) {
			// keep turning the gun
			turnGunRight(360);
		}
	}

	// See another robot
	public void onScannedRobot(ScannedRobotEvent e) {
		out.println ("scanned robot");
		if (isTeammate(e.getName())) { return; } // TEAMMATE; move on
		else { 			
			// ENEMY; aim and shoot
			/*
			FIREPOWER STRATEGY:
			we want to set bullet speed first to have the most accurate enemy direction later
			optimize firepower to enemy energy, enemy distance, my energy
			*/

			// OPTIMIZE TO DISTANCE
			// bullet speed = 20 - 3 * power so bullet power should decrease as distance increases 
			double dist = e.getDistance(); // enemy distance
			double firepower = Math.min(3, 450/dist); // if 450/dist > 3, then the enemy is super close and the max is 3
			
			// OPTIMIZE TO MY ENERGY
			// if myEnergy < firepower, shooting will kill me
			double myEnergy = getEnergy();
			firepower = Math.min(firepower, myEnergy); 
			
			// OPTIMIZE TO ENEMY ENERGY
			// if eEnergy is low, don't need to waste extra energy
			// damage = (bulletPower * 4) + (max(0, bulletPower - 1) * 2)
			// working backward, if eEnergy <=16, we could kill the robot in a single shot
			double eEnergy = e.getEnergy();
			if (eEnergy < 16 && firepower > 1) {
				firepower = Math.min(firepower, (eEnergy + 2)/6);
			}
			// we still want to avoid killing ourselves/wasting energy b/c of distance
			// so we don't override, we just take the minimum

			// AIMING
			// turn toward the enemy
			setTurnRight(e.getBearing());
			// subtracing the gun heading accounts for a difference in the gun heading and robot heading
			setTurnGunRight(e.getBearing() + getHeading() - getGunHeading());
			// approach the enemy
			setAhead(100);
			
			// "OFF WITH HIS HEAD!!"
			setFire(firepower);
			execute(); // executes the turnRight, turnGunRight, setAhead and setFire
			out.println("fired");
		}
	}
	
	// Hit another robot (Probably coincidence?)
	public void onHitRobot(HitRobotEvent e) {
		out.println("hit robot");
		if (isTeammate(e.getName())) { return; } // TEAMMATE; move on
		else { // ENEMY; ram it!
			turnGunRight(e.getBearing() + getHeading() - getGunHeading());
			/*
			ON HIT ROBOT STRATEGY:
			if we hit another robot, we can ram it for bonus points 
			damage = (bulletPower * 4) + (max(0, bulletPower - 1) * 2)
			working backwards, we get:
			eEnergy > 16 -> can fire w/ power of 3
			eEnergy > 10 -> can fire w/ power of 2
			eEnergy > 4 -> can fire w/ power of 1
			eEnergy > 2 -> can fire w/ power of 0.5
			*/
			
			if (e.getEnergy() > 16) { fire(3); } 
			else if (e.getEnergy() > 10) { fire(2); }
			else if (e.getEnergy() > 4) { fire(1); }
			else if (e.getEnergy() > 2) { fire(0.5); }
			
			ahead (5);
		}
	}

	// Hit by bullet
	public void onHitByBullet(HitByBulletEvent e) {
		out.println("hit");
		/*
		HIT BY BULLET STRATEGY:
		when we're hit by a bullet, the bullet direction tells us the location of the enemy that shot it
		thus we can work backwards, saving time that would be wasted on scanning, and directly shoot
		*/
		
		// SOURCE
		// to find the source of the bullet, we need to turn the gun in the direction the bullet came from
		setTurnRight(e.getBearing());
		setTurnGunRight(e.getBearing() + getHeading() - getGunHeading());		
		execute();
		out.println("aimed");
		turnGunRight(360);
		// this should scan the robot, and call onScannedRobot()
	}
	
	// Hit wall
	public void onHitWall(HitWallEvent e) {
		back(20);
	}	
}
