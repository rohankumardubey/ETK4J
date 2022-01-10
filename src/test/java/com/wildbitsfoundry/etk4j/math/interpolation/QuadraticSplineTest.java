package com.wildbitsfoundry.etk4j.math.interpolation;

import com.wildbitsfoundry.etk4j.util.NumArrays;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class QuadraticSplineTest {
    static double[] x;
    static double[] y;
    static double[] xi;
    static double left;
    static double right;

    // TODO test derivatives and definite/indefinite integrals
    // add tests for quadratic spline

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setArrays() {
        x = new double[]{0.9, 1.3, 1.9, 2.1};
        y = new double[]{1.3, 1.5, 1.85, 2.1};
        xi = new double[]{1.15, 1.8, 2.0};
        left = -0.5;
        right = 3.0;
    }

    @Test
    public void testNaturalSplineInterpolation() {
        double[] expected = {0.0, 2.2704, 9.0816, 20.4336, 36.3264, 56.760000000000005, 81.7344, 111.2496, 145.3056,
                183.9024, 227.04, 268.796, 303.248, 330.39599999999996, 350.24, 362.78, 376.0732, 398.17679999999996,
                429.09079999999994, 468.8152, 517.35, 562.8132, 593.3228, 611.5570222222223, 633.5852,
                662.0855555555555, 697.0580888888888, 738.5028, 786.4196888888889, 840.8087555555555, 901.67};
        double[] x = new double[]{0.0, 10.0, 15.0, 20.0, 22.5, 30.0};
        double[] y = new double[]{0.0, 227.04, 362.78, 517.35, 602.97, 901.67};
        QuadraticSpline qs = QuadraticSpline.newNaturalSpline(x, y);

        double[] xi = NumArrays.linSpace(0.0, 30.0, 31);
        double[] yi = new double[xi.length];
        // TODO maybe add evaluate at array?
        for (int i = 0; i < xi.length; ++i) {
            yi[i] = qs.evaluateAt(xi[i]);
        }
        assertArrayEquals(expected, yi, 1e-12);
    }

    @Test
    public void testParabolicallyTerminatedSplineInterpolation() {
        CubicSpline cspline = CubicSpline.newParabolicallyTerminatedSpline(x, y);

        double yi = cspline.evaluateAt(xi[0]);
        assertEquals(1.4321022727272725, yi, 1e-12);

        yi = cspline.evaluateAt(xi[1]);
        assertEquals(1.7594696969696972, yi, 1e-12);

        yi = cspline.evaluateAt(xi[2]);
        assertEquals(1.9632575757575759, yi, 1e-12);
    }

    @Test
    public void testClampedSplineInterpolation() {
        CubicSpline cspline = CubicSpline.newClampedSpline(x, y, 2, 1);

        double yi = cspline.evaluateAt(xi[0]);
        assertEquals(1.5100360576923078, yi, 1e-12);

        yi = cspline.evaluateAt(xi[1]);
        assertEquals(1.7361111111111118, yi, 1e-12);

        yi = cspline.evaluateAt(xi[2]);
        assertEquals(1.9814102564102565, yi, 1e-12);
    }

    @Test
    public void testNotAKnotSplineInterpolation() {
        CubicSpline cspline = CubicSpline.newNotAKnotSpline(x, y);

        double yi = cspline.evaluateAt(xi[0]);
        assertEquals(1.4394531249999998, yi, 1e-12);

        yi = cspline.evaluateAt(xi[1]);
        assertEquals(1.7593750000000004, yi, 1e-12);

        yi = cspline.evaluateAt(xi[2]);
        assertEquals(1.9622916666666668, yi, 1e-12);
    }

    @Test
    public void testAkimaSplineInterpolation() {
        double[] x = {0.5, 0.9, 1.3, 1.9, 2.1, 2.2};
        double[] y = {1.0, 1.3, 1.5, 1.85, 2.1, 2.4};
        CubicSpline cspline = CubicSpline.newAkimaSpline(x, y);

        double yi = cspline.evaluateAt(xi[0]);
        assertEquals(1.4258655894886363, yi, 1e-12);

        yi = cspline.evaluateAt(xi[1]);
        assertEquals(1.7887205387205394, yi, 1e-12);

        yi = cspline.evaluateAt(xi[2]);
        assertEquals(1.9470219435736678, yi, 1e-12);
    }

    @Test
    public void testNaturalSplineExtrapolateLeft() {
        CubicSpline cspline = CubicSpline.newNaturalSpline(x, y);

        cspline.setExtrapolationMethod(ExtrapolationMethod.CLAMP_TO_END_POINT);
        double yi = cspline.evaluateAt(left);
        assertEquals("Natural Spline ClampToEndPoint lower bound extrapolation", 1.3, yi, 0.0);

        cspline.setExtrapolationMethod(ExtrapolationMethod.CLAMP_TO_NAN);
        yi = cspline.evaluateAt(left);
        assertTrue("Natural Spline ClampToEndNaN lower bound extrapolation", Double.isNaN(yi));

        cspline.setExtrapolationMethod(ExtrapolationMethod.CLAMP_TO_ZERO);
        yi = cspline.evaluateAt(left);
        assertEquals("Natural Spline ClampToZero lower bound extrapolation", 0.0, yi, 0.0);

        cspline.setExtrapolationMethod(ExtrapolationMethod.LINEAR);
        yi = cspline.evaluateAt(left);
        assertEquals("Natural Spline Linear lower bound extrapolation", 0.5474178403755874, yi, 1e-12);

        cspline.setExtrapolationMethod(ExtrapolationMethod.NATURAL);
        yi = cspline.evaluateAt(left);
        assertEquals("Natural Spline Natural lower bound extrapolation", 1.1915492957746414, yi, 1e-12);

        cspline.setExtrapolationMethod(ExtrapolationMethod.PERIODIC);
        yi = cspline.evaluateAt(left);
        assertEquals("Natural Spline Periodic lower bound extrapolation", 1.8500000000000005, yi, 1e-12);

        cspline.setExtrapolationMethod(ExtrapolationMethod.THROW);
        exception.expect(IndexOutOfBoundsException.class);
        yi = cspline.evaluateAt(left);
    }

    @Test
    public void testNaturalSplineExtrapolateRight() {
        CubicSpline cspline = CubicSpline.newNaturalSpline(x, y);

        cspline.setExtrapolationMethod(ExtrapolationMethod.CLAMP_TO_END_POINT);
        double yi = cspline.evaluateAt(right);
        assertEquals("Natural Spline ClampToEndPoint upper bound extrapolation", 2.1, yi, 0.0);

        cspline.setExtrapolationMethod(ExtrapolationMethod.CLAMP_TO_NAN);
        yi = cspline.evaluateAt(right);
        assertTrue("Natural Spline ClampToEndNaN upper bound extrapolation", Double.isNaN(yi));

        cspline.setExtrapolationMethod(ExtrapolationMethod.CLAMP_TO_ZERO);
        yi = cspline.evaluateAt(right);
        assertEquals("Natural Spline ClampToZero upper bound extrapolation", 0.0, yi, 0.0);

        cspline.setExtrapolationMethod(ExtrapolationMethod.LINEAR);
        yi = cspline.evaluateAt(right);
        assertEquals("Natural Spline Linear upper bound extrapolation", 3.306338028169013, yi, 1e-12);

        cspline.setExtrapolationMethod(ExtrapolationMethod.NATURAL);
        yi = cspline.evaluateAt(right);
        assertEquals("Natural Spline Natural upper bound extrapolation", 1.6592429577464949, yi, 1e-12);

        cspline.setExtrapolationMethod(ExtrapolationMethod.PERIODIC);
        yi = cspline.evaluateAt(right);
        assertEquals("Natural Spline Periodic upper bound extrapolation", 1.7557218309859157, yi, 1e-12);

        cspline.setExtrapolationMethod(ExtrapolationMethod.THROW);
        exception.expect(IndexOutOfBoundsException.class);
        yi = cspline.evaluateAt(right);
    }
}