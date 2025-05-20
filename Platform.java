/* Platform class
 * Stub constructors provided
 * Can be used with the following tutorials:
 * https://longbaonguyen.github.io/courses/platformer/platformer.html
 * Authors: __________________
 * Last Edit: 5/20/25
 * Updated to Java version
 */


import processing.core.PApplet;
import processing.core.PImage;

public class Platform extends Sprite {

    PApplet p;

	//Platform defined by it's center-x and top-Y positions
	public Platform(PApplet p, PImage img, float posXCenter, float posYTop, float platWidth, float platHeight, int color) {
		
		//pass along the center-x and center-y to Sprite super
		super(p, img, 1.0f, posXCenter, posYTop + (platHeight/2));
		//System.out.println("PlatTopY: " + posYTop + "\tPlatCenterY: " + (posYTop + (platHeight/2)));
		// setWidth(platWidth);
		// setHeight(platHeight);
		//setColor(Color.black);
	}

	public Platform(PApplet p, PImage img, float posXCenter, float posYTop, float platWidth, float platHeight) {
		//pass along the center-x and center-y to Sprite super
		this(p, img, posXCenter, posYTop, platWidth, platHeight, 0x000000); // #000000
	}

}
