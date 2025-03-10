package fr.loudo.cameratester.utils;

public class MathUtils {

    public static double[] inOrOutBodyCamera(double[] start, double[] end, double t)
    {
        double easeOutT = easeOutCubic(t);

        double y = lerp(start[0], end[0], easeOutT);
        double headYaw = lerp(start[1], end[1], easeOutT);

        return new double[]{y, headYaw};
    }

    public static double[] moveBetweenPoints(double[] start, double[] end, double t)
    {
        double easeOutT = easeOutCubic(t);

        double x = lerp(start[0], end[0], easeOutT);
        double y = lerp(start[1], end[1], easeOutT);
        double z = lerp(start[2], end[2], easeOutT);

        return new double[]{x, y, z};
    }

    public static double easeOutCubic(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    public static double lerp(double a, double b, double t)
    {
        return a + (b - a) * t;
    }
}
