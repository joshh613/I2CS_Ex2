import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Intro2CS_2026A
 * This class represents a Graphical User Interface (GUI) for Map2D.
 * The class has save and load functions, and a GUI draw function.
 * You should implement this class, it is recommender to use the StdDraw class, as in:
 * https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html
 *
 *
 */
public class Ex2_GUI {
    public static void drawMap(Map2D map) {
        if (map == null) {
            System.err.println("map is null");
            return;
        }

        int width = map.getWidth();
        int height = map.getHeight();

        StdDraw.enableDoubleBuffering();
        StdDraw.clear();

        StdDraw.setXscale(-0.5, width - 0.5);
        StdDraw.setYscale(-0.5, height - 0.5);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int v = map.getPixel(x, y);
                StdDraw.setPenColor(colorOf(v));
                StdDraw.filledSquare(x, y, 0.5);
            }
        }

        StdDraw.show();
    }

    /**
     * @param mapFileName
     * @return
     */
    public static Map2D loadMap(String mapFileName) throws FileNotFoundException {
        try (Scanner in = new Scanner(new File(mapFileName))) {
            if (!in.hasNextInt()) {
                System.err.println("empty / no width");
                return null;
            }
            int w = in.nextInt();

            if (!in.hasNextInt()) {
                System.err.println("no height");
                return null;

            }
            int h = in.nextInt();

            if (w <= 0 || h <= 0) {
                System.err.println("invalid width or height");
                return null;
            }

            int[][] map = new int[w][h];
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    if (!in.hasNextInt()) {
                        System.err.println("invalid map");
                        return null;
                    }
                    map[i][j] = in.nextInt();
                }
            }

            return new Map(map);
        }
    }

    /**
     *
     * @param map
     * @param mapFileName
     */
    public static void saveMap(Map2D map, String mapFileName) {
        if (map == null) {
            System.err.println("map is null");
            return;
        }

        int w = map.getWidth();
        int h = map.getHeight();

        try (PrintWriter out = new PrintWriter(new File(mapFileName))) {
            out.println(w + " " + h);

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    out.print(map.getPixel(x, y));
                    if (x < w - 1) {
                        out.print(" ");
                    }
                }
                out.println();
            }

            System.out.println("saved successfully");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] a) throws FileNotFoundException {
        String mapFile = "map.txt";
        if (a.length > 0) {
            mapFile = a[0];
        }

        Map2D map = loadMap(mapFile);
        if (map == null) {
            System.err.println("map is null");
            return;
        }

        drawMap(map);
    }

    /// ///////////// Private functions ///////////////

    private static Color colorOf(int v) {
        switch (v) {
            case 0:
                return Color.WHITE;
            case 1:
                return Color.BLACK;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.RED;
            case 4:
                return Color.GREEN;
            case 5:
                return Color.YELLOW;
            case 6:
                return Color.ORANGE;
            case 7:
                return Color.CYAN;
            case 8:
                return Color.MAGENTA;
            case 9:
                return Color.PINK;
            default:
                return Color.LIGHT_GRAY;

        }
    }
}