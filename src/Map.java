import java.io.Serializable;
import java.util.LinkedList;

/**
 * This class represents a 2D map (int[w][h]) as a "screen" or a raster matrix or maze over integers.
 * This is the main class needed to be implemented.
 *
 * @author Joshua Hall
 *
 */
public class Map implements Map2D, Serializable {
    private int[][] map;
    private int width, height;

    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    /**
     * Constructs a w*h 2D raster map with an init value v.
     *
     * @param w width (&gt; 0)
     * @param h height (&gt; 0)
     * @param v default pixel value
     */
    public Map(int w, int h, int v) {
        init(w, h, v);
    }

    /**
     * Constructs a square map (size*size). Has a default pixel value of v=0
     *
     * @param size used for width and height (&gt; 0)
     */
    public Map(int size) {
        this(size, size, 0);
    }

    /**
     * Constructs a map from a given 2D array.
     *
     * @param data 2D array of pixel values
     */
    public Map(int[][] data) {
        init(data);
    }

    /**
     * Initialises the map to given w,h dimensions with the given v value.
     *
     * @param w the width of the underlying 2D array.
     * @param h the height of the underlying 2D array.
     * @param v the init value of all the entries in the 2D array.
     */
    @Override
    public void init(int w, int h, int v) {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("invalid dimensions: h=" + h + ", w=" + w);
        }

        this.width = w;
        this.height = h;
        map = new int[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                map[i][j] = v;
            }
        }
    }

    /**
     * Initialises the map from a 2D array (using a deep copy).
     *
     * @param arr a 2D int array.
     */
    @Override
    public void init(int[][] arr) {
        if (arr == null || arr.length == 0 || arr[0].length == 0) {
            throw new IllegalArgumentException("null/empty array");
        }

        this.width = arr.length;
        this.height = arr[0].length;

        for (int i = 0; i < this.width; i++) {
            if (arr[i].length != this.height) {
                throw new IllegalArgumentException("ragged array");
            }
        }

        map = new int[this.width][height];
        for (int i = 0; i < this.width; i++) {
            System.arraycopy(arr[i], 0, map[i], 0, this.height);
        }
    }

    /**
     * Returns a deep copy of the 2D pixel array.
     *
     * @return a new 2D array of size {@code width} * {@code height}
     */
    @Override
    public int[][] getMap() {
        int[][] newMap = new int[width][height];
        for (int i = 0; i < width; i++) {
            System.arraycopy(map[i], 0, newMap[i], 0, height);
        }
        return newMap;
    }

    /**
     * Returns the number of columns in the map
     *
     * @return the {@code width} of the map
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of rows in the map
     *
     * @return the {@code height} of the map
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Returns the pixel value at a given coord.
     *
     * @param x the x coordinate in range 0 to {@code width -1}
     * @param y the y coordinate in range 0 to {@code height -1
     * @return
     */
    @Override
    public int getPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            throw new IndexOutOfBoundsException("x/y out of bounds");
        }
        return map[x][y];
    }

    /**
     * Returns the pixel value at a given coord.
     *
     * @param p the x,y coordinate (non null)
     * @return the pixel value at {@code p}
     */
    @Override
    public int getPixel(Pixel2D p) {
        if (p == null) {
            throw new NullPointerException("null pixel");
        }
        return getPixel(p.getX(), p.getY());
    }

    /**
     * Sets the pixel at (x,y) to the given v value.
     *
     * @param x the x coordinate in range 0 to {@code width -1}
     * @param y the y coordinate in range 0 to {@code height -1}
     * @param v the value that the entry at the coordinate [x][y] is set to.
     */
    @Override
    public void setPixel(int x, int y, int v) {
        if (!isInside(x, y)) {
            throw new IndexOutOfBoundsException("x/y out of bounds");
        }
        map[x][y] = v;
    }

    /**
     * Sets the pixel at (x,y) to the given v value.
     *
     * @param p the coordinate in the map.
     * @param v the value that the entry at the coordinate [p.x][p.y] is set to.
     */
    @Override
    public void setPixel(Pixel2D p, int v) {
        if (p == null) {
            throw new NullPointerException("null pixel");
        }
        setPixel(p.getX(), p.getY(), v);
    }

    /**
     * Returns {@code true} iff the given pixel lies within the bounds of the map.
     *
     * @param p the 2D coordinate.
     * @return {@code true} if {@code p} is non-null AND inside the map
     */
    @Override
    public boolean isInside(Pixel2D p) {
        return p != null && isInside(p.getX(), p.getY());
    }

    /**
     * Checks if a given map has the same dimensions as this map.
     *
     * @param p another map (possibly {@code null}
     * @return {@code true} iff {@code p} is non-null AND has the same width,height
     */
    @Override
    public boolean sameDimensions(Map2D p) {
        return p != null && p.getWidth() == getWidth() && p.getHeight() == getHeight();
    }

    /**
     * Performs element-wise addition of pixel values from another map. (Assuming the other map is NOT {@code null})
     *
     * @param p the map that should be added to this map.
     */
    @Override
    public void addMap2D(Map2D p) {
        if (p == null || !sameDimensions(p)) {
            return;
        }

        for (int i = 0; i < p.getWidth(); i++) {
            for (int j = 0; j < p.getHeight(); j++) {
                map[i][j] += p.getPixel(i, j);
            }
        }
    }

    /**
     * Multiplies all pixel values by a given scalar. (If needed, we truncate using casting - NOT rounding)
     *
     * @param scalar the factor by which to multiple (double)
     */
    @Override
    public void mul(double scalar) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = (int) (map[i][j] * scalar); //maybe Math.round would be nicer
            }
        }
    }

    /**
     * Rescales the map by a given scale factor. Uses the nearest neighbor to fill in any newly created pixels.
     *
     * @param sx x direction scale factor (&gt; 0)
     * @param sy y direction scale factor (&gt; 0)
     */
    @Override
    public void rescale(double sx, double sy) {
        if (sx <= 0 || sy <= 0) {
            throw new IllegalArgumentException("sx/sy must be >0. you have: sx=" + sx + ", sy=" + sy);
        }

        int newW = (int) (width * sx);
        int newH = (int) (height * sy);
        if (newW == 0 || newH == 0) {
            throw new RuntimeException("size is 0");
        }

        int[][] newM = new int[newW][newH];
        for (int i = 0; i < newW; i++) {
            for (int j = 0; j < newH; j++) {
                int interW = clamp((int) (i / sx), width - 1);
                int interH = clamp((int) (j / sy), height - 1);
                newM[i][j] = getPixel(interW, interH);
            }
        }

        this.map = newM;
        this.width = newW;
        this.height = newH;
    }

    /**
     * Draws a circle, centred at {@code center} with radius {@code rad}, in the colour {@code color}. We use {@code x^2+y^2<=r^2} to ensure the correct pixels are coloured.
     * Points outside the map are ignored. Also, if the centre is outside the circle, then nothing happens.
     *
     * @param center centre of the circle (must be inside the map)
     * @param rad    circle radius (&gt; 0)
     * @param color  - the (new) color to be used in the drawing.
     */
    @Override
    public void drawCircle(Pixel2D center, double rad, int color) {
        if (!isInside(center) || rad <= 0) {
            return;
        }

        for (int i = 0; i < width; i++) { //maybe use the enclosing square
            for (int j = 0; j < height; j++) {
                int offsetX = i - center.getX();
                int offsetY = j - center.getY();
                if (isInCircle(rad, offsetX, offsetY)) {
                    setPixel(i, j, color);
                }
            }
        }
    }

    /**
     * Draws a straight lines between two given pixels (both of which must be inside the map).
     * If {@code p1==p2}, the that single pixel is coloured.
     *
     * @param p1    start point (non-null, inside the map)
     * @param p2    end point (non-null, inside the map)
     * @param color colour used to draw the line.
     */
    @Override
    public void drawLine(Pixel2D p1, Pixel2D p2, int color) {
        if (p1 == null || p2 == null || !isInside(p1) || !isInside(p2)) {
            return;
        }

        if (p1.equals(p2)) {
            setPixel(p1, color);
            return;
        }

        int x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2 = p2.getY();
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        if (dx >= dy) { //mostly horizontal
            if (x1 < x2) { //left to right
                drawLineHelperX(x1, x2, y1, y2, color);
            } else { //right to left
                drawLineHelperX(x2, x1, y2, y1, color);
            }
        } else { //mostly vertical
            if (y1 < y2) { //down to up
                drawLineHelperY(x1, x2, y1, y2, color);
            } else { //up to down
                drawLineHelperY(x2, x1, y2, y1, color);
            }
        }
    }

    /**
     * Fills in the rectangle create by the two corners {@code p1,p2} (both of which must be inside the map).
     *
     * @param p1    one corner
     * @param p2    opposite corner
     * @param color value used to fill in the rectangle
     */
    @Override
    public void drawRect(Pixel2D p1, Pixel2D p2, int color) {
        if (p1 == null || p2 == null || !isInside(p1) || !isInside(p2)) {
            return;
        }

        int x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2 = p2.getY();
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                setPixel(x, y, color);
            }
        }
    }

    /**
     * Compares this map to another {@code Object}.
     * They are equal if {@code ob} 1. is  an instance of {@link Map2D}, 2. has the same dimensions, 3. has the same pixel values.
     *
     * @param ob the reference object with which to compare.
     * @return {@code true} iff the above 3 conditions are met.
     */
    @Override
    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }

        if (!(ob instanceof Map2D)) {
            return false;
        }

        Map2D other = (Map2D) ob;
        if (!sameDimensions(other)) {
            return false;
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (getPixel(x, y) != other.getPixel(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Flood fills the region starting from the pixel {@code xy}, implementing the algorithm <a href="https://en.wikipedia.org/wiki/Flood_fill">given here.</a>
     * The option to "loop" around is toggled with {@code cyclic}. This is done using modular arithmetics.
     *
     * @param xy     the starting pixel
     * @param new_v  the fill colour
     * @param cyclic {@code true} iff we want to loop around the edge
     * @return the number of pixels successfully filled
     */
    @Override
    public int fill(Pixel2D xy, int new_v, boolean cyclic) {
        if (xy == null || !isInside(xy)) {
            return 0;
        }

        int old_v = getPixel(xy);
        if (old_v == new_v) {
            return 0;
        }

        boolean[][] visited = new boolean[width][height];
        LinkedList<Pixel2D> pixels = new LinkedList<>();

        visited[xy.getX()][xy.getY()] = true;
        pixels.add(xy);
        int count = 0;

        while (!pixels.isEmpty()) {
            Pixel2D curr = pixels.remove();
            int x = curr.getX(), y = curr.getY();
            setPixel(x, y, new_v);
            count++;

            for (int[] dir : DIRS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (cyclic) {
                    newX = (newX + width) % width; //newX+width handles newX=-1
                    newY = (newY + height) % height;
                } else {
                    if (!isInside(newX, newY)) {
                        continue;
                    }
                }

                if (!visited[newX][newY] && getPixel(newX, newY) == old_v) {
                    visited[newX][newY] = true;
                    pixels.add(new Index2D(newX, newY));
                }
            }
        }
        return count;
    }

    /**
     * Compute the shorted path between two given pixels using the <a href="https://en.wikipedia.org/wiki/Breadth-first_search">BFS algorithm</a>, avoiding obstacles (given by the value {@code obsColor})
     * The option to "loop" around is toggled with {@code cyclic}. This is done using modular arithmetics.
     *
     * @param p1       starting pixel
     * @param p2       finishing pixel
     * @param obsColor the color which is addressed as an obstacle.
     * @param cyclic   {@code true} iff we want to loop around the edge
     * @return an array of pixels representing the path from {@code p1} to {@code p2}
     */
    @Override
    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic) {
        if (p1 == null || p2 == null || !isInside(p1) || !isInside(p2)) {
            return null;
        }
        if (getPixel(p1) == obsColor || getPixel(p2) == obsColor) {
            return null;
        }

        if (p1.equals(p2)) {
            return new Pixel2D[]{p1};
        }

        boolean[][] visited = new boolean[width][height];
        Pixel2D[][] prev = new Pixel2D[width][height];
        LinkedList<Pixel2D> pixels = new LinkedList<>();

        int x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2 = p2.getY();
        visited[x1][y1] = true;
        pixels.add(new Index2D(x1, y1));

        while (!pixels.isEmpty()) {
            Pixel2D curr = pixels.remove();
            int x = curr.getX(), y = curr.getY();

            if (x == x2 && y == y2) {
                break;
            }

            for (int[] dir : DIRS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (cyclic) {
                    newX = (newX + width) % width; //newX+width handles newX=-1
                    newY = (newY + height) % height;
                } else {
                    if (!isInside(newX, newY)) {
                        continue;
                    }
                }

                if (visited[newX][newY] || getPixel(newX, newY) == obsColor) {
                    continue;
                }

                visited[newX][newY] = true;
                prev[newX][newY] = curr;
                pixels.add(new Index2D(newX, newY));
            }
        }

        if (!visited[x2][y2]) {
            return null;
        }
        return finalPath(prev, x2, y2);
    }

    /**
     * Creates a distance map from a given starting point. Unreachable pixels are assigned the value -1.
     * The option to "loop" around is toggled with {@code cyclic}. This is done using modular arithmetics.
     *
     * @param start    starting point
     * @param obsColor the color representing obstacles
     * @param cyclic   {@code true} iff we want to loop around the edge
     * @return a new {@link Map} with values representing the distance from {@code start}
     */
    @Override
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        if (start == null || !isInside(start)) {
            return new Map(width, height, -1);
        }

        int x1 = start.getX(), y1 = start.getY();
        if (getPixel(x1, y1) == obsColor) {
            return new Map(width, height, -1);
        }

        boolean[][] visited = new boolean[width][height];
        visited[x1][y1] = true;

        int[][] dist = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                dist[x][y] = -1;
            }
        }
        dist[x1][y1] = 0;

        LinkedList<Pixel2D> pixels = new LinkedList<>();
        pixels.add(new Index2D(x1, y1));

        while (!pixels.isEmpty()) {
            Pixel2D curr = pixels.remove();
            int x = curr.getX(), y = curr.getY();

            for (int[] dir : DIRS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (cyclic) {
                    newX = (newX + width) % width; //newX+width handles newX=-1
                    newY = (newY + height) % height;
                } else {
                    if (!isInside(newX, newY)) {
                        continue;
                    }
                }

                if (visited[newX][newY] || getPixel(newX, newY) == obsColor) {
                    continue;
                }

                visited[newX][newY] = true;
                dist[newX][newY] = dist[x][y] + 1;
                pixels.add(new Index2D(newX, newY));
            }
        }
        return new Map(dist);
    }

    /// /////////////////// Private Methods ///////////////////////

    private boolean isInCircle(double rad, int x, int y) {
        return x * x + y * y <= rad * rad;
    }

    private int clamp(int val, int max) {
        if (val < 0) {
            return 0;
        }
        return Math.min(val, max);
    }

    private boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private void drawLineHelperX(int x1, int x2, int y1, int y2, int color) {
        double m = (double) (y2 - y1) / (x2 - x1);
        double b = y1 - m * x1;

        for (int x = x1; x <= x2; x++) {
            int y = (int) Math.round(x * m + b);
            if (isInside(x, y)) {
                setPixel(x, y, color);
            }
        }
    }

    private void drawLineHelperY(int x1, int x2, int y1, int y2, int color) {
        double m = (double) (x2 - x1) / (y2 - y1);
        double b = x1 - m * y1;

        for (int y = y1; y <= y2; y++) {
            int x = (int) Math.round(y * m + b);
            if (isInside(x, y)) {
                setPixel(x, y, color);
            }
        }
    }

    private Pixel2D[] finalPath(Pixel2D[][] prev, int x2, int y2) {
        int len = 0;
        Pixel2D pixel = new Index2D(x2, y2);
        while (pixel != null) {
            len++;
            pixel = prev[pixel.getX()][pixel.getY()];
        }

        Pixel2D[] path = new Pixel2D[len];
        pixel = new Index2D(x2, y2);
        for (int i = len - 1; i >= 0; i--) {
            path[i] = pixel;
            pixel = prev[pixel.getX()][pixel.getY()];
        }
        return path;
    }
}
