package sw;
import robocode.*;
import java.awt.*;
import robocode.TeamRobot;
/*
SophiaWang - a robot by Sophia Wang
01/10/2024 TEAM NU Robocode Tournament
*/
public class SophiaWang extends TeamRobot {
private int curGunDir = 1;
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
	if (isTeammate(e.getName())) { return; } // TEAMMATE; move on
	else { 
	// ENEMY; aim and shoot
	
	/*
	FIREPOWER STRATEGY:
	we want to set bullet speed first to have the most accurate enemy
	direction later
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
	
	// "OFF WITH HIS HEAD!!"
	if (firepower < 0.5) { firepower = 0.5; } // too low is pretty much ineffective
		setFire(firepower);
		// approach the enemy and adjust gun
		setAhead(100);
		curGunDir *= -1;
		setTurnGunRight(360 * curGunDir); // turn the gun in the opposite
		direction
		execute(); // executes the turnRight, turnGunRight, setFire, and 
		setAhead
	}
}
	
// Hit wall
public void onHitWall(HitWallEvent e) {
	back(20);
	turnGunRight(360);
}
