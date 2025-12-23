public class Index2D implements Pixel2D {
    private final int _x, _y;

    /**
     * Constructors a new Index2D
     *
     * @param w x coord
     * @param h y coord
     */
    public Index2D(int w, int h) {
        this._x = w;
        this._y = h;
    }

    /**
     * Copy constructor from another Pixel2D
     *
     * @param other another pixel
     */
    public Index2D(Pixel2D other) {
        if (other == null) {
            throw new RuntimeException("other is null");
        }

        this._x = other.getX();
        this._y = other.getY();
    }

    @Override
    public int getX() {
        return this._x;
    }

    @Override
    public int getY() {
        return this._y;
    }

    @Override
    public double distance2D(Pixel2D p2) {
        if (p2 == null) {
            throw new RuntimeException("p2 is null");
        }

        int dx = this._x - p2.getX();
        int dy = this._y - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return "(" + this._x + ", " + this._y + ")";
    }

    @Override
    public boolean equals(Object p) {
        if (p == null || !(p instanceof Index2D)) {
            return false;
        }

        Index2D other = (Index2D) p;
        return this._x == other._x && this._y == other._y;
    }
}
