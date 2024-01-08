package GP;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Enumeration;

// TODO: 
// - predictive fire.
// - remove bots from dictionary when killed.
public class GavinParker extends TeamRobot
{
	Dictionary<String, Target> targets = new Hashtable<>();
	ArrayList<ScannedRobotEvent> scans = new ArrayList();
	double[] radarRotations;
	
	Target currentTarget;

	boolean calibrated = false;
	double sweepSpeed = 75;
	double sweepCalibrationSpeed = 5;
	double trackingPrecision = 2;
	
	double gunPower = 5;
	double shootDistance = 50;

	double targetBearing = 0; // angle to enemy
	double lastTargetBearing;
	double targetHeading = 0; // rotation of enemy
	double lastTargetHearing;
	double targetVelocity = 0; // how fast enemy is moving
	double lastTargetVelocity;
	double targetDistance = 0; // distance to enemy
	double lastTargetDistance;
	
	double gunBearing;
	
	// main loop and initialization
	public void run() {
	
		out.println("initializing robot");
		initializeRobot();

		// Robot main loop
		while(true) {
			out.println("started sweep");
			radarSweep(); // Find enemies
			out.println("finished sweep");
			
			if (targets.size() < getOthers() && currentTarget != null){
				targets.remove(currentTarget.scanData.getName());
			}
			
			handleMovement();
		}
	}
	
	// makes the radar move alot
	public void radarSweep(){
		out.println("starting radar turn");
		
		turnRadarRight(sweepSpeed);
		/*
		for (int i = 0; i < getOthers(); i++){
			turnRadarRight(radarRotations[i]);
		}*/
		
		//generateRadarRotations();
		
		// loop through each robot in the dictionary
		for (Enumeration t = targets.keys(); t.hasMoreElements();){
			Target target = targets.get(t.nextElement());
			
		}
	}
	
	public void handleMovement(){
		if (currentTarget == null) return;	
		
	}

	// shoots stuff
	public void handleTarget(Target target){
		if (target == null) return;	

		double targetDirection = target.scanData.getBearing() + getHeading();
		double difference = targetDirection - getGunHeading();
		double turnAngle = normalRelativeAngleDegrees(difference);
	
		turnGunRight(turnAngle);
		
		if (Math.abs(difference) < 0 + trackingPrecision){
			fire(gunPower);
		}
	}
	
	public void calibrateRobot(){
		radarRotations = new double[getOthers()];			

		out.println("getting robots");
		//get a reference to each robot.
		while (targets.size() < getOthers()){
			turnRadarRight(sweepCalibrationSpeed);
		}
		
		generateRadarRotations();
	}
	
	public void generateRadarRotations(){
		// calibrates the rotations for the radar
		Target[] _targets = getTargetArray();
		
		out.println("starting radar calibration");
		for (int i = 0; i < getOthers() ; i++){
			out.println("iteration " + i);
			double directionA = _targets[i].scanData.getBearing() + getHeading();
			double directionB = _targets[i + 1].scanData.getBearing() + getHeading();
			double difference = normalRelativeAngleDegrees(directionB - directionA);
				
			radarRotations[i] = difference;
		}
		out.println("radar calibrated");
	}
	
	// name is self explanitory
	public void initializeRobot(){
		Color bodyColor = new Color(98, 169, 124);
		Color gunColor = new Color(126, 224, 129);
		Color radarColor = new Color(195, 243, 192);
		setColors(bodyColor, gunColor, radarColor); // set robot colors
		
		//calibrateRobot();
	}
	
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		out.println("scanned " + e.getName());
		
		if (scans.contains(e) == true){
			scans.add(e);
		}

		Target target = new Target(e);
		targets.put(e.getName(), target);
		
		currentTarget = getTarget();
		
		if (calibrated == false){
			handleTarget(currentTarget);
		}
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {

	}
	
	// gets the closest robot 
	public Target getTarget(){
		// if no targets are found in the dictionary(none have been scanned)
		if (targets.isEmpty() == true) return null;
		
		// the robot we want to shoot at 
		Target priorityTarget = new Target(new ScannedRobotEvent());
		double distance = Double.MAX_VALUE;
			
		// loop through each robot in the dictionary and find the closest one
		for (Enumeration i = targets.keys(); i.hasMoreElements();){
			Target target = targets.get(i.nextElement());	

			// closer targets set to the priority
			if (distance > target.scanData.getDistance()){
				priorityTarget = target;
				distance = target.scanData.getDistance();
			}
		}
		
		return priorityTarget;
	}	
	
	// converts a dictionary of robots to an array of robots
	public Target[] getTargetArray(){
		Target[] targetsArray = new Target[targets.size()];

		int k = 0;
		// loop through each robot in the dictionary and find the closest one
		for (Enumeration i = targets.keys(); i.hasMoreElements();){
			Target target = targets.get(i.nextElement());
			
			targetsArray[k] = target;
		}
		
		return targetsArray;
	}
	
	// Prints the array of targets to the console for debugging purposes
	public void printTargets(){
		Target[] targetArray = getTargetArray();
		String output = "";
		
		for (int i = 0; i < targetArray.length; i++){
			output += targetArray[i].scanData.getName() + ", ";
		}
		
		out.println(output);
	}
}
