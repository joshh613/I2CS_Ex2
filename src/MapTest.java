import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapTest {

    private Map empty3x3;
    private Map filled4x2;
    private Map fromArray;

    @BeforeEach
    void setUp() {
        empty3x3 = new Map(3, 3, 0);
        filled4x2 = new Map(4, 2, 7);

        int[][] arr = {
                {1, 2, 3},
                {4, 5, 6}
        };
        fromArray = new Map(arr);
    }


    //constructors & getters
    @Test
    void constructorWidthHeightValue() {
        assertEquals(4, filled4x2.getWidth());
        assertEquals(2, filled4x2.getHeight());

        for (int x = 0; x < filled4x2.getWidth(); x++) {
            for (int y = 0; y < filled4x2.getHeight(); y++) {
                assertEquals(7, filled4x2.getPixel(x, y));
            }
        }
    }

    @Test
    void constructorSquareSize() {
        Map m = new Map(5);
        assertEquals(5, m.getWidth());
        assertEquals(5, m.getHeight());
    }

    @Test
    void constructorFromArrayDeepCopy() {
        assertEquals(2, fromArray.getWidth());
        assertEquals(3, fromArray.getHeight());
        assertEquals(5, fromArray.getPixel(1, 1));

        int[][] src = {
                {1, 2, 3},
                {4, 5, 6}
        };
        Map m = new Map(src);
        src[1][1] = 999;
        assertEquals(5, m.getPixel(1, 1));
    }

    @Test
    void initFromArrayRaggedThrows() {
        int[][] ragged = {
                {1, 2},
                {3}
        };

        assertThrows(IllegalArgumentException.class, () -> empty3x3.init(ragged));
    }

    @Test
    void getMapReturnsDeepCopy() {
        empty3x3.setPixel(1, 1, 9);
        int[][] copy = empty3x3.getMap();

        // same values initially
        assertEquals(9, copy[1][1]);

        // modify copy, original should stay unchanged
        copy[1][1] = 0;
        assertEquals(9, empty3x3.getPixel(1, 1));
    }


    // get/set pixel
    @Test
    void setAndGetPixelByCoordinatesAndObject() {
        empty3x3.setPixel(2, 1, 5);
        assertEquals(5, empty3x3.getPixel(2, 1));

        Pixel2D p = new Index2D(0, 0);
        empty3x3.setPixel(p, 7);
        assertEquals(7, empty3x3.getPixel(0, 0));
        assertEquals(7, empty3x3.getPixel(p));
    }

    @Test
    void getPixelOutOfBoundsThrows() {
        Map m = new Map(2, 2, 0);
        assertThrows(IndexOutOfBoundsException.class, () -> m.getPixel(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> m.getPixel(2, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> m.getPixel(0, 2));
    }


    //main functions
    @Test
    void isInsideWorksOnBordersAndOutside() {
        Map m = new Map(3, 4, 0);

        assertTrue(m.isInside(new Index2D(0, 0)));
        assertTrue(m.isInside(new Index2D(2, 3)));

        assertFalse(m.isInside(new Index2D(-1, 0)));
        assertFalse(m.isInside(new Index2D(3, 0)));
        assertFalse(m.isInside(new Index2D(0, 4)));
    }

    @Test
    void sameDimensionsComparesWidthAndHeight() {
        Map a = new Map(3, 4, 0);
        Map b = new Map(3, 4, 1);
        Map c = new Map(4, 3, 1);

        assertTrue(a.sameDimensions(b));
        assertFalse(a.sameDimensions(c));
        assertFalse(a.sameDimensions(null));
    }

    @Test
    void equalsComparesContentAndType() {
        int[][] arr1 = {
                {0, 1},
                {2, 3}
        };
        int[][] arr2 = {
                {0, 1},
                {2, 3}
        };
        int[][] arr3 = {
                {0, 9},
                {2, 3}
        };

        Map a = new Map(arr1);
        Map b = new Map(arr2);
        Map c = new Map(arr3);

        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals("not a map", a);
    }


    @Test
    void addMap2DSumsMatchingCells() {
        Map a = new Map(new int[][]{
                {1, 2},
                {3, 4}
        });
        Map b = new Map(new int[][]{
                {10, 20},
                {30, 40}
        });

        a.addMap2D(b);

        assertEquals(11, a.getPixel(0, 0));
        assertEquals(22, a.getPixel(0, 1));
        assertEquals(33, a.getPixel(1, 0));
        assertEquals(44, a.getPixel(1, 1));
    }

    @Test
    void addMap2DDifferentDimensionsDoesNothing() {
        Map a = new Map(2, 2, 1);
        Map b = new Map(3, 2, 7);

        a.addMap2D(b);

        assertEquals(1, a.getPixel(0, 0));
        assertEquals(1, a.getPixel(1, 1));
    }

    @Test
    void mulScalesValuesAndCastsToInt() {
        Map a = new Map(new int[][]{
                {3, -3},
                {2, 1}
        });

        a.mul(2.5); //e.g. (int)(3*2.5)=7

        assertEquals(7, a.getPixel(0, 0));
        assertEquals(-7, a.getPixel(0, 1));
        assertEquals(5, a.getPixel(1, 0));
        assertEquals(2, a.getPixel(1, 1));
    }

    @Test
    void rescaleDoublesSizeWithNearestNeighborLikeMapping() {
        Map a = new Map(new int[][]{
                {1, 2},
                {3, 4}
        });

        a.rescale(2.0, 2.0);

        assertEquals(4, a.getWidth());
        assertEquals(4, a.getHeight());

        assertEquals(1, a.getPixel(0, 0));
        assertEquals(1, a.getPixel(1, 1));
        assertEquals(2, a.getPixel(0, 3));
        assertEquals(3, a.getPixel(3, 0));
        assertEquals(4, a.getPixel(3, 3));
    }


    //drawing
    @Test
    void drawRectFillsAxisAlignedRectangle() {
        Map a = new Map(5, 5, 0);

        a.drawRect(new Index2D(1, 1), new Index2D(3, 2), 7);

        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 2; y++) {
                assertEquals(7, a.getPixel(x, y));
            }
        }

        assertEquals(0, a.getPixel(0, 0));
        assertEquals(0, a.getPixel(4, 4));
    }

    @Test
    void drawLineSinglePointHorizontalAndVertical() {
        Map a = new Map(5, 5, 0);

        // single point
        a.drawLine(new Index2D(2, 2), new Index2D(2, 2), 9);
        assertEquals(9, a.getPixel(2, 2));

        // horizontal line
        a = new Map(5, 5, 0);
        a.drawLine(new Index2D(0, 2), new Index2D(4, 2), 3);
        for (int x = 0; x <= 4; x++) {
            assertEquals(3, a.getPixel(x, 2));
        }

        // vertical line
        a = new Map(5, 5, 0);
        a.drawLine(new Index2D(3, 0), new Index2D(3, 4), 4);
        for (int y = 0; y <= 4; y++) {
            assertEquals(4, a.getPixel(3, y));
        }
    }

    @Test
    void drawLineDiagonalMain() {
        Map a = new Map(5, 5, 0);

        a.drawLine(new Index2D(0, 0), new Index2D(4, 4), 5);

        for (int i = 0; i <= 4; i++) {
            assertEquals(5, a.getPixel(i, i));
        }
    }

    @Test
    void drawCircleRadiusZeroDoesNothing() {
        Map a = new Map(5, 5, 0);

        a.drawCircle(new Index2D(2, 2), 0.0, 8);

        assertEquals(0, a.getPixel(2, 2));
    }


    //algorithms
    @Test
    void fillNonCyclicComponent() {
        int[][] arr = {
                {1, 1, 0},
                {1, 0, 0},
                {9, 9, 0}
        };
        Map a = new Map(arr);

        int filled = a.fill(new Index2D(0, 0), 7, false);

        assertEquals(3, filled);
        assertEquals(7, a.getPixel(0, 0));
        assertEquals(7, a.getPixel(1, 0));
        assertEquals(7, a.getPixel(0, 1));

        assertEquals(0, a.getPixel(2, 2));
        assertEquals(9, a.getPixel(2, 0));
        assertEquals(9, a.getPixel(2, 1));
    }

    @Test
    void fillCyclicWrapsAroundEdges() {
        int[][] arr = {{1}, {0}, {1}};
        Map a = new Map(arr);

        int count = a.fill(new Index2D(0, 0), 7, true);

        assertEquals(2, count);
        assertEquals(7, a.getPixel(0, 0));
        assertEquals(7, a.getPixel(2, 0));
        assertEquals(0, a.getPixel(1, 0));
    }

    @Test
    void allDistanceWithoutObstaclesBasicDistances() {
        Map a = new Map(4, 4, 0);

        Map2D dist = a.allDistance(new Index2D(0, 0), 9, false);

        assertEquals(0, dist.getPixel(0, 0));
        assertEquals(1, dist.getPixel(1, 0));
        assertEquals(1, dist.getPixel(0, 1));
        assertEquals(2, dist.getPixel(1, 1));
        assertEquals(6, dist.getPixel(3, 3));
    }

    @Test
    void allDistanceWithObstacle() {
        Map a = new Map(3, 3, 0);
        a.setPixel(1, 0, 9);

        Map2D dist = a.allDistance(new Index2D(0, 0), 9, false);

        assertEquals(0, dist.getPixel(0, 0));
        assertEquals(-1, dist.getPixel(1, 0));
        assertEquals(1, dist.getPixel(0, 1));
        assertEquals(2, dist.getPixel(1, 1));
    }

    @Test
    void allDistanceCyclicShorterPathUsingWrap() {
        Map a = new Map(5, 1, 0);

        Map2D distNoCyclic = a.allDistance(new Index2D(0, 0), 9, false);
        Map2D distCyclic = a.allDistance(new Index2D(0, 0), 9, true);

        assertEquals(4, distNoCyclic.getPixel(4, 0)); // normal path
        assertEquals(1, distCyclic.getPixel(4, 0));   // wrap from 0 to 4
    }

    @Test
    void shortestPathSimpleHorizontalLine() {
        Map a = new Map(4, 1, 0);

        Pixel2D[] path = a.shortestPath(
                new Index2D(0, 0),
                new Index2D(3, 0),
                9,
                false
        );

        assertNotNull(path);
        assertEquals(4, path.length);
        assertEquals(0, path[0].getX());
        assertEquals(3, path[3].getX());
    }

    @Test
    void shortestPathNoPathWhenBlocked() {
        Map a = new Map(3, 3, 0);

        a.setPixel(1, 0, 9);
        a.setPixel(1, 1, 9);
        a.setPixel(1, 2, 9);

        Pixel2D[] path = a.shortestPath(
                new Index2D(0, 0),
                new Index2D(2, 0),
                9,
                false
        );

        assertNull(path);
    }

    @Test
    void shortestPathCyclicCanWrapAround() {
        Map a = new Map(5, 1, 0);

        Pixel2D[] path = a.shortestPath(
                new Index2D(0, 0),
                new Index2D(4, 0),
                9,
                true
        );

        assertNotNull(path);
        assertEquals(2, path.length);
        assertEquals(0, path[0].getX());
        assertEquals(4, path[1].getX());
    }
}