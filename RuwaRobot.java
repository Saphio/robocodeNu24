package rt;
 
import robocode.*;
import java.awt.Color;

/**
 * RuwaRobot - a robot by (Ruwa Taha)
 */
public class RuwaRobot extends AdvancedRobot {
    private boolean movingForward = true;

    public void run() {
//Sets the team main colors of the RuwaRobot (3 shades of green).
        setColors(new Color(98, 169, 124), new Color(126, 224, 129), new Color(195, 243, 192));

        while (true) {
//Moves the robot forward and backward in a motion of the same distance.
            if (movingForward) {
                setAhead(100);
            } else {
                setBack(100);
            }
           
            // Once the robot hits one of the walls, this code switches the direction of the robot in order to get it back into play.
            movingForward = !movingForward;  

           // Turns the gun to place the robot back into position.
            turnGunRight(360);  
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
   
 
        double absoluteBearing = getHeading() + e.getBearing();
        double gunTurn = robocode.util.Utils.normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
        turnGunRight(gunTurn);

     
     
   
         // Fires at the other robots.
fire(5);
    }
   
//Sets the robot back 30 pixels when running into a wall or being it by a bullet in order to continue moving and gain a better position against the enemy.
    public void onHitByBullet(HitByBulletEvent e) {
        setBack(30);
    }

    public void onHitWall(HitWallEvent e) {
        setBack(30);
    }
}
