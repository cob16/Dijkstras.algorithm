package cs21120.assignment2.solution;

import cs21120.assignment2.FloatImage;
import cs21120.assignment2.ISnapper;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by cormac brady on 26/03/15.
 * <p/>
 * Implements ISnapper to provide Dijkstra’s shortest path algorithm.
 * <p/>
 * Dijkstra's algorithm is an algorithm for finding the shortest paths between nodes in a graph.
 *
 * @author Cormac Brady
 * @version 1
 * @see cs21120.assignment2.ISnapper
 */
public class cob16Snapper implements ISnapper {

    PriorityBlockingQueue<PixelNodes> pixels;
    private FloatImage[] edges;           //weights of node paths
    private boolean[][] visited_nodes;
    private Point[][] mapToSource;        //shortest path back to source
    private Point source;

    /**
     * Initialises the internal arrays to the dimensions specified.
     *
     * @param x the horizontal position of the pixel
     * @param y the vertical position of the pixel in the image
     */
    private void setVars(Point source, int x, int y, FloatImage[] edges)
    {
        pixels = new PriorityBlockingQueue<>();

        this.edges = edges.clone();
        this.source = new Point(source);
        visited_nodes = new boolean[x][y];
        mapToSource = new Point[x][y];

        pixels.add(new PixelNodes(source, 0.0));
//        this.destination = null;
    }
//    private final Point destination;

    /**
     * Sets the Position of the source node/pixel and starts map building thread in
     * {@link #map() map()} method
     *
     * @param x     the horizontal position of the pixel
     * @param y     the vertical position of the pixel in the image
     * @param edges an array of {@link FloatImage} containing  edge weights of nodes
     * @see ISnapper
     * overrides method in ISnapper
     */
    @Override
    public void setSeed(int x, int y, FloatImage[] edges)
    {
        Point seed = new Point(x, y);

        setVars(seed, edges[0].getWidth(), edges[0].getHeight(), edges);

        printEdges(seed, edges);

        map();
    }

    /**
     * Debugging, testing and research method that prints the edges for a specified node.
     * @param seed Point to print edges for
     * @param edges Array to reference
     */
    private void printEdges(Point seed, FloatImage[] edges)
    {
        int x = seed.x;
        int y = seed.y;

        System.out.println(" - printing edges at at: " + source.toString());

        System.out.println("NORTH WEST: " + edges[0].get(x, y));
        System.out.println("     NORTH: " + edges[1].get(x, y));
        System.out.println("NORTH EAST: " + edges[2].get(x, y));
        System.out.println("      EAST: " + edges[3].get(x, y));

        System.out.println("SOUTH EAST: " + edges[0].get(x + 1, y - 1));
        System.out.println("     SOUTH: " + edges[1].get(x, y - 1));
        System.out.println("SOUTH WEST: " + edges[2].get(x - 1, y - 1));
        System.out.println("      WEST: " + edges[3].get(x - 1, y));
    }

    /**
     * starts a thread to map out the image run from
     * {@link #setSeed(int, int, FloatImage[]) setSeed()}
     * overrides method in ISnapper
     */
    private void map()
    {
        Thread map = new Thread(new Runnable() {
            public void run()
            {
                System.out.println("map tread started");

                Point tempTest = pixels.poll().getPosition();

                printEdges(tempTest, edges);
                //System.out.println(edges[0].get_nocheck(0, 0)); //do stuff

                while (!pixels.isEmpty()) {
                    PixelNodes current = pixels.poll();
                    examineEdges(current);
                }
            }
        });
        map.start();
    }

    /**
     * Core of Dijkstra’s shortest path algorithm.
     * <p/>
     * This method is run for every node in oder to generate the {@link #mapToSource mapToSource}
     *
     * @param current the current node to handel
     */
    private void examineEdges(PixelNodes current)
    {
        Point cp = current.getPosition();
        for (Direction dir : Direction.values()) {
            Point neighbor = pointTranslator(cp, dir);
            if (!visited_nodes[neighbor.x][neighbor.y]) //has node been visited
            {
                double newWeight = pointEdge(cp, dir) + current.getTotalWeight();
                PixelNodes neighborOb = findObject(neighbor);
                double oldWeight = neighborOb.getTotalWeight();
                System.out.println("old weight: " + oldWeight + " newWeight " + newWeight);
                if (neighborOb == null) {
                    pixels.add(new PixelNodes(neighbor, newWeight));
                    mapToSource[neighbor.x][neighbor.y] = cp;
                } else if (newWeight < oldWeight) {
                    pixels.remove(neighborOb);
                    neighborOb.setTotalWeight(newWeight);
                    mapToSource[neighbor.x][neighbor.y] = cp;
                    pixels.add(neighborOb);
                }
            }
        }
        visited_nodes[cp.x][cp.y] = true;
    }

    /**
     * Finds object within the priory queue.
     * @param target the coordinates of the node
     * @return The matching object, null if not found
     */
    PixelNodes findObject(Point target)
    {
        Iterator it = pixels.iterator();
        while (it.hasNext()) {
            PixelNodes temp = (PixelNodes) it.next(); //cast to make compiler happy
            if (temp.getPosition().equals(target)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Gets the point that is next to the origin in the provided direction.
     *
     * @param origin    point of reference for translation
     * @param direction enum describing the provided direction to translate Point to
     * @return Newly translated Point
     */
    Point pointTranslator(Point origin, Direction direction)
    {
        switch (direction) {
            case NORTH:
                origin.translate(0, 1);
                return origin;
            //return makes break unnecessary
            case NORTHEAST:
                origin.translate(1, 1);
                return origin;
            //return makes break unnecessary;
            case EAST:
                origin.translate(1, 0);
                return origin;
            //return makes break unnecessary;
            case SOUTHEAST:
                origin.translate(1, -1);
                return origin;
            //return makes break unnecessary;
            case SOUTH:
                origin.translate(0, -1);
                return origin;
            //return makes break unnecessary;
            case SOUTHWEST:
                origin.translate(-1, -1);
                return origin;
            //return makes break unnecessary;
            case WEST:
                origin.translate(-1, 0);
                return origin;
            //return makes break unnecessary;
            case NORTHWEST:
                origin.translate(-1, 1);
                return origin;
            //return makes break unnecessary;
            default:
                return null;
        }
    }

    /**
     * Gets the Edge weight of the specified direction.
     *
     * @param origin    the current point of reference
     * @param direction Enum of the Edge weight to retrieve
     * @return Edge weight
     */
    private float pointEdge(Point origin, Direction direction)
    {

        int x = origin.x;
        int y = origin.y;

        switch (direction) {
            case NORTH:
                return edges[2].get(x, y);
            //return makes break unnecessary
            case NORTHEAST:
                return edges[1].get(x, y);
            //return makes break unnecessary
            case EAST:
                return edges[0].get(x, y);
            //return makes break unnecessary
            case SOUTHEAST:
                return edges[3].get(x + 1, y - 1);
            //return makes break unnecessary
            case SOUTH:
                return edges[2].get(x, y - 1);
            //return makes break unnecessary
            case SOUTHWEST:
                return edges[1].get(x - 1, y - 1);
            //return makes break unnecessary
            case WEST:
                return edges[0].get(x - 1, y);
            //return makes break unnecessary
            case NORTHWEST:
                return edges[3].get(x, y);
            //return makes break unnecessary
            default:
                System.out.println("undefined enum provided");
                System.exit(1);
                return 0;
            //return makes break unnecessary
        }
    }

    @Override
    public LinkedList<Point> getPath(int i, int i1)
    {
        return null;
    }

    /**
     * The eight points of a compass e.g. north, north-east ect...
     */
    public enum Direction {
        NORTH,
        NORTHEAST,
        EAST,
        SOUTHEAST,
        SOUTH,
        SOUTHWEST,
        WEST,
        NORTHWEST
    }

    /**
     * Internal class data-type that stores the TotalWeight of a node
     * (the cost to get back the source node)
     */
    private class PixelNodes implements Comparable<PixelNodes> {

        Point position;
        double TotalWeight;

        public PixelNodes(Point position, double totalWeight)
        {
            this.position = position;
            TotalWeight = totalWeight;
        }

        public Point getPosition()
        {
            return position;
        }

        public double getTotalWeight()
        {
            return TotalWeight;
        }

        public void setTotalWeight(double totalWeight)
        {
            this.TotalWeight = totalWeight;
        }

        @Override
        public int compareTo(PixelNodes otherNode)
        {
            return Double.compare(getTotalWeight(), otherNode.getTotalWeight());
        }
    }
    //is destnaon marked visited
}