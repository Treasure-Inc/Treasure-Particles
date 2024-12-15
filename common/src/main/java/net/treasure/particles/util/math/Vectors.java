package net.treasure.particles.util.math;

import org.bukkit.util.Vector;

import java.util.Random;

public class Vectors {

    public static Vector rotateAroundAxisX(Vector v, double cos, double sin) {
        var y = v.getY() * cos - v.getZ() * sin;
        var z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double cos, double sin) {
        var x = v.getX() * cos + v.getZ() * sin;
        var z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
        var x = v.getX() * cos - v.getY() * sin;
        var y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    public static Vector getRandomVector() {
        var random = new Random();

        var u = random.nextDouble();
        var v = random.nextDouble();

        var theta = u * MathUtils.PI2;
        var phi = Math.acos(2 * v - 1);

        var sinTheta = MathUtils.sin(theta);
        var cosTheta = MathUtils.cos(theta);
        var sinPhi = MathUtils.sin(phi);
        var cosPhi = MathUtils.cos(phi);

        var x = sinPhi * cosTheta;
        var y = sinPhi * sinTheta;
        var z = cosPhi;

        // Going to take it on faith from the math gods that
        // this is always a normal vector
        return new Vector(x, y, z);
    }
}