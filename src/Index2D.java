public class Index2D implements Pixel2D {
    private final int x, y;

    /**
     * Constructors a new Index2D
     *
     * @param w x coord
     * @param h y coord
     */
    public Index2D(int w, int h) {
        this.x = w;
        this.y = h;
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

        this.x = other.getX();
        this.y = other.getY();
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public double distance2D(Pixel2D p2) {
        if (p2 == null) {
            throw new RuntimeException("p2 is null");
        }

        int dx = this.x - p2.getX();
        int dy = this.y - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    @Override
    public boolean equals(Object p) {
        if (!(p instanceof Pixel2D)) {
            return false;
        }

        Pixel2D other = (Pixel2D) p;
        return this==p || this.x == other.getX() && this.y == other.getY();
    }
}
