import java.io.Serializable;

/**
 * This class represents a 2D map (int[w][h]) as a "screen" or a raster matrix or maze over integers.
 * This is the main class needed to be implemented.
 *
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D, Serializable {
    private int[][] map;

    /**
     * Constructs a w*h 2D raster map with an init value v.
     *
     * @param w
     * @param h
     * @param v
     */
    public Map(int w, int h, int v) {
        init(w, h, v);
    }

    /**
     * Constructs a square map (size*size).
     *
     * @param size
     */
    public Map(int size) {
        this(size, size, 0);
    }

    /**
     * Constructs a map from a given 2D array.
     *
     * @param data
     */
    public Map(int[][] data) {
        init(data);
    }

    @Override
    public void init(int w, int h, int v) {
        if (w <= 0 || h <= 0) {
            throw new RuntimeException("w/h invalid");
        }

        this.map = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                this.map[i][j] = v;
            }
        }
    }

    @Override
    public void init(int[][] arr) {
        if (arr == null || arr.length == 0 || arr[0].length == 0) {
            throw new RuntimeException("null/empty array");
        }

        int w = arr.length;
        int h = arr[0].length;

        for (int i = 0; i < w; i++) {
            if (arr[i].length != h) {
                throw new RuntimeException("ragged array");
            }
        }

        this.map = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                this.map[i][j] = arr[i][j];
            }
        }
    }

    @Override
    public int[][] getMap() {
        int w = getWidth();
        int h = getHeight();

        int[][] ans = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                ans[i][j] = map[i][j];
            }
        }
        return ans;
    }

    @Override
    public int getWidth() {
        return this.map.length;
    }

    @Override
    public int getHeight() {
        return this.map[0].length;
    }

    @Override
    public int getPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            throw new IndexOutOfBoundsException("x/y out of bounds");
        }
        return this.map[x][y];
    }

    @Override
    public int getPixel(Pixel2D p) {
        return getPixel(p.getX(), p.getY());
    }

    @Override
    public void setPixel(int x, int y, int v) {
        if (!isInside(x, y)) {
            throw new IndexOutOfBoundsException("x/y out of bounds");
        }
        this.map[x][y] = v;
    }

    @Override
    public void setPixel(Pixel2D p, int v) {
        this.setPixel(p.getX(), p.getY(), v);
    }

    @Override
    public boolean isInside(Pixel2D p) {
        if (p == null) {
            return false;
        }

        return isInside(p.getX(), p.getY());
    }

    @Override
    public boolean sameDimensions(Map2D p) {
        if (p == null) {
            return false;
        }

        if (p.getWidth() == getWidth()) {
            if (p.getHeight() == getHeight()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addMap2D(Map2D p) {
        if (!sameDimensions(p)) {
            return;
        }

        int w = p.getWidth();
        int h = p.getHeight();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                this.map[i][j] += p.getPixel(i, j);
            }
        }
    }

    @Override
    public void mul(double scalar) {
        int w = getWidth();
        int h = getHeight();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                this.map[i][j] = (int) Math.round(this.map[i][j] * scalar);
            }
        }
    }

    @Override
    public void rescale(double sx, double sy) {
        if (sx <= 0 || sy <= 0) {
            throw new RuntimeException("sx/sy invalid");
        }

        int oldW = getWidth();
        int oldH = getHeight();
        int newW = (int) (oldW * sx);
        int newH = (int) (oldH * sy);

        Map newM = new Map(newW, newH, 0);

        //interpolate
        for (int i = 0; i < newW; i++) {
            for (int j = 0; j < newH; j++) {
                int interW = clamp((int) (i / sx), 0, oldW - 1);
                int interH = clamp((int) (j / sy), 0, oldH - 1);
                newM.map[i][j] = this.map[interW][interH];
            }
        }
        this.map = newM.map;
    }

    @Override
    public void drawCircle(Pixel2D center, double rad, int color) {
        if (!isInside(center)) {
            return;
        }

        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                int offsetX = i - center.getX();
                int offsetY = j - center.getY();
                if (isInCircle(rad, offsetX, offsetY)) {
                    setPixel(i, j, color);
                }
            }
        }
    }

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

    @Override
    public void drawRect(Pixel2D p1, Pixel2D p2, int color) {
        if (p1 == null || p2 == null || !isInside(p1) || !isInside(p2)) {
            return;
        }

        for (int x = p1.getX(); x <= p2.getX(); x++) {
            for (int y = p1.getY(); y <= p2.getY(); y++) {
                setPixel(x, y, color);
            }
        }
    }

    @Override
    public boolean equals(Object ob) {
        if (!(ob instanceof Map)) {
            return false;
        }

        Map op = (Map) ob;
        if (!sameDimensions(op)) {
            return false;
        }

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if (op.getPixel(x, y) != getPixel(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    /**
     * Fills this map with the new color (new_v) starting from p.
     * https://en.wikipedia.org/wiki/Flood_fill
     */
    public int fill(Pixel2D xy, int new_v, boolean cyclic) {
        int ans = -1;
        return ans;
    }

    @Override
    /**
     * BFS like shortest the computation based on iterative raster implementation of BFS, see:
     * https://en.wikipedia.org/wiki/Breadth-first_search
     */
    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic) {
        Pixel2D[] ans = null;  // the result.
        return ans;
    }

    @Override
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        Map2D ans = null;  // the result.
        return ans;
    }

    /// /////////////////// Private Methods ///////////////////////

    private boolean isInCircle(double rad, int x, int y) {
        return x * x + y * y <= rad * rad;
    }

    private int clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        }
        if (val > max) {
            return max;
        }
        return val;
    }

    private boolean isInside(int x, int y) {
        if (x >= 0 && x < getWidth()) {
            if (y >= 0 && y < getHeight()) {
                return true;
            }
        }
        return false;
    }

    private void drawLineHelperX(int x1, int x2, int y1, int y2, int color) {
        double m = (double) (y2 - y1) / (x2 - x1);
        double b = y1 - m * x1;

        for (int x = x1; x <= x2; x++) {
            int y = (int) Math.round(x * m + b);
            setPixel(x, y, color);
        }
    }

    private void drawLineHelperY(int x1, int x2, int y1, int y2, int color) {
        double m = (double) (x2 - x1) / (y2 - y1);
        double b = x1 - m * y1;

        for (int y = y1; y <= y2; y++) {
            int x = (int) Math.round(y * m + b);
            setPixel(x, y, color);
        }
    }
}
