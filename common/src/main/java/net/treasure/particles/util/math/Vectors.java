package net.treasure.particles.util.math;

import org.bukkit.util.Vector;

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
}