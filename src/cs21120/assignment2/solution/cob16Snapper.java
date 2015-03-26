package cs21120.assignment2.solution;

import cs21120.assignment2.FloatImage;
import cs21120.assignment2.ISnapper;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.lang.Double;


/**
 * Implements ISnapper to provide Dijkstra’s shortest path algorithm.
 * <p>
 * Created by cormac brady on 26/03/15.
 * @author Cormac Brady
 * @see cs21120.assignment2.ISnapper
 * @version 1
 */
public class cob16Snapper implements ISnapper {

    private  FloatImage[] edges;           //weights of node paths
    private  Double[][] weights;           //weights of node paths
//    POSITIVE_INFINITY

    private  boolean[][] visited_nodes;
    private  Point[][] mapToSource;        //shortest path back to source
    private  Point source;


//    private final FloatImage[] edges;           //weights of node paths
//    private final boolean[][] visited_nodes;
//    private final Point[][] mapToSource;        //shortest path back to source
//    private final Point source;
////    private final Point destination;

    /**
     * Initialises the internal arrays to the dimensions specified.
     * @param  x the horizontal position of the pixel
     * @param  y the vertical position of the pixel in the image
     */
    private void setVars(Point source, int x, int y, FloatImage[] edges) {
        visited_nodes = new boolean[x][y];
        mapToSource   = new Point[x][y];
        this.edges    = edges;
        this.source   = source;
//        this.destination = null;
        //Double.POSITIVE_INFINITY
        weights = new Double[x][y];
        Fill2dArray(weights, Double.POSITIVE_INFINITY); //makes all node costs start at infinity
    }

    /**
     * Fills a given 2d array of doubles to a value
     * @param  array The array to iterate through
     * @param  value value all values in array are set to this
     */
    private void Fill2dArray(Double[][] array, Double value) {
        for(int i=0; i < array.length; i++) {
            for (int j=0; j < array[i].length; j++) {
                array[i][j] = value;
            }
        }
    }

    /**
     * Sets the Position of the source node/pixel and starts map building thread in
     * {@link #map() map()} method
     *
     * @param  x the horizontal position of the pixel
     * @param  y the vertical position of the pixel in the image
     * @param  edges an array of {@link FloatImage} containing  edge weights of nodes
     * @see ISnapper
     * overrides method in ISnapper
     */
    @Override
    public void setSeed(int x, int y, FloatImage[] edges)
    {
        setVars(new Point(x, y), edges[0].getWidth(), edges[0].getHeight(), edges);

        System.out.println(" - setSeed called - at " + source.toString());

        System.out.println("NORTH WEST: " + edges[0].get(x, y));
        System.out.println("     NORTH: " + edges[1].get(x, y));
        System.out.println("NORTH EAST: " + edges[2].get(x, y));
        System.out.println("      EAST: " + edges[3].get(x, y));

        System.out.println("SOUTH EAST: " + edges[0].get(x + 1, y - 1));
        System.out.println("     SOUTH: " + edges[1].get(x, y - 1));
        System.out.println("SOUTH WEST: " + edges[2].get(x - 1, y - 1));
        System.out.println("      WEST: " + edges[3].get(x - 1, y));

        map();
    }

    /**
     * starts a thread to map out the image run from
     * {@link #setSeed(int, int, FloatImage[]) setSeed()}
     * overrides method in ISnapper
     */
    public void map(){
        Thread map = new Thread(new Runnable() {
            public void run() {
                System.out.println("the tread started" + visited_nodes[0][0]);
                //System.out.println(edges[0].get_nocheck(0, 0)); //do stuff
            }
        });
        map.start();
    }

    @Override
    public LinkedList<Point> getPath(int i, int i1)
    {
        return null;
    }
}
