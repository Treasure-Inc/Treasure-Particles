package net.treasure.util.math;

import org.bukkit.util.Vector;

public class Vectors {

    public static Vector rotateAroundAxisX(Vector v, double angle) {
        angle = Math.toRadians(angle);
        var cos = MathUtils.cos(angle);
        var sin = MathUtils.sin(angle);
        var y = v.getY() * cos - v.getZ() * sin;
        var z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double angle) {
        angle = -angle;
        angle = Math.toRadians(angle);
        var cos = MathUtils.cos(angle);
        var sin = MathUtils.sin(angle);
        var x = v.getX() * cos + v.getZ() * sin;
        var z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }
}