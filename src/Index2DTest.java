import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Index2DTest {
    private static final double EPS = 1e-3;
    private Index2D p1, p2, p3;

    @BeforeEach
    void setUp() {
        //used in several tests
        p1 = new Index2D(13, 21);
        p2 = new Index2D(0, 0);
        p3 = new Index2D(-8, 34);
    }

    //constructors & getters
    @Test
    void basicConstructorAndGetters() {
        assertEquals(13, p1.getX());
        assertEquals(21, p1.getY());

        assertEquals(0, p2.getX());
        assertEquals(0, p2.getY());

        assertEquals(-8, p3.getX());
        assertEquals(34, p3.getY());
    }

    @Test
    void constructorWithNegativeValue() {
        Index2D p = new Index2D(42, -17);
        assertEquals(42, p.getX());
        assertEquals(-17, p.getY());
    }

    @Test
    void copyConstructorFromIndex2D() {
        Index2D original = new Index2D(99, 100);
        Index2D copy = new Index2D(original);

        assertEquals(99, copy.getX());
        assertEquals(100, copy.getY());
        assertEquals(original, copy);
    }

    @Test
    void constructorFromPixel2DReference() {
        Pixel2D src = new Index2D(256, 512);
        Index2D dst = new Index2D(src);

        assertEquals(src.getX(), dst.getX());
        assertEquals(src.getY(), dst.getY());
    }

    //distance
    @Test
    void distanceOfSamePointIsZero() {
        Pixel2D a = new Index2D(7, 11);
        Pixel2D b = new Index2D(7, 11);
        assertEquals(0.0, a.distance2D(b), EPS);
    }

    @Test
    void distanceForTriangle() {
        //5,12,13 triangle
        Pixel2D a = new Index2D(0, 0);
        Pixel2D b = new Index2D(5, 12);
        assertEquals(13.0, a.distance2D(b), EPS);
    }

    @Test
    void distanceUsingSetupPoints() {
        //p1 = (13,21), p2 = (0,0), p3 = (-8,34)

        double d12 = p1.distance2D(p2);
        double expected12 = Math.sqrt(13 * 13 + 21 * 21);
        assertEquals(expected12, d12, EPS);

        double d11 = p1.distance2D(p1);
        assertEquals(0.0, d11, EPS);

        double d13 = p1.distance2D(p3);
        int dx = 13 - (-8);
        int dy = 21 - 34;
        double expected13 = Math.sqrt(dx * dx + dy * dy);
        assertEquals(expected13, d13, EPS);
    }

    @Test
    void distanceThrowsOnNull() {
        Pixel2D p = new Index2D(1, 1);
        assertThrows(RuntimeException.class, () -> p.distance2D(null));
    }

    //equals
    @Test
    void equalsForSameCoordinates() {
        Index2D a = new Index2D(42, 99);
        Index2D b = new Index2D(42, 99);
        Index2D c = new Index2D(42, 100);

        assertEquals(a, b); //same x,y
        assertNotEquals(a, c); //y is different
    }

    @Test
    void equalsHandlesNullAndOtherTypes() {
        Index2D a = new Index2D(-13, 37);

        assertNotEquals(null, a); //should not be equal to null
        assertNotEquals("(-13,37)", a); //different type
    }

    //toString
    @Test
    void toStringFormat() {
        assertEquals("(13,21)", p1.toString());
        assertEquals("(0,0)", p2.toString());
        assertEquals("(-8,34)", p3.toString());

        Index2D p = new Index2D(123, -456);
        assertEquals("(123,-456)", p.toString());
    }

    //immutability
    @Test
    void valuesDoNotChangeAfterConstruction() {
        Index2D p = new Index2D(73, 19);
        int xBefore = p.getX();
        int yBefore = p.getY();

        assertEquals(xBefore, p.getX());
        assertEquals(yBefore, p.getY());
    }
}