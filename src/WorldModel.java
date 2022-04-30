import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel
{
    public final String SAPLING_KEY = "sapling";
    final String TREE_KEY = "tree";
    final String STUMP_KEY = "stump";
    private final String FAIRY_KEY = "fairy";
    private final String HOUSE_KEY = "house";
    private final String DUDE_KEY = "dude";
    private final String OBSTACLE_KEY = "obstacle";
    private final String BGND_KEY = "background";
    private final List<String> PATH_KEYS = new ArrayList<>(Arrays.asList("bridge", "dirt", "dirt_horiz", "dirt_vert_left", "dirt_vert_right",
            "dirt_bot_left_corner", "dirt_bot_right_up", "dirt_vert_left_bot"));
    private final int PROPERTY_KEY = 0;
    final int TREE_HEALTH_MIN = 1;
    final int TREE_HEALTH_MAX = 3;
    final int TREE_ACTION_MIN = 1000;
    final int TREE_ACTION_MAX = 1400;
    final int TREE_ANIMATION_MIN = 50;
    final int TREE_ANIMATION_MAX = 600;
    private final int OBSTACLE_NUM_PROPERTIES = 5;
    private final int OBSTACLE_ID = 1;
    private final int OBSTACLE_COL = 2;
    private final int OBSTACLE_ROW = 3;
    private final int OBSTACLE_ANIMATION_PERIOD = 4;
    private final int DUDE_NUM_PROPERTIES = 7;
    private final int DUDE_ID = 1;
    private final int DUDE_COL = 2;
    private final int DUDE_ROW = 3;
    private final int DUDE_LIMIT = 4;
    private final int DUDE_ACTION_PERIOD = 5;
    private final int DUDE_ANIMATION_PERIOD = 6;
    private final int HOUSE_NUM_PROPERTIES = 4;
    private final int HOUSE_ID = 1;
    private final int HOUSE_COL = 2;
    private final int HOUSE_ROW = 3;
    private final int FAIRY_NUM_PROPERTIES = 6;
    private final int FAIRY_ID = 1;
    private final int FAIRY_COL = 2;
    private final int FAIRY_ROW = 3;
    private final int FAIRY_ANIMATION_PERIOD = 4;
    private final int FAIRY_ACTION_PERIOD = 5;
    private final int TREE_NUM_PROPERTIES = 7;
    private final int TREE_ID = 1;
    private final int TREE_COL = 2;
    private final int TREE_ROW = 3;
    private final int TREE_ANIMATION_PERIOD = 4;
    private final int TREE_ACTION_PERIOD = 5;
    private final int TREE_HEALTH = 6;
    static final int SAPLING_HEALTH_LIMIT = 5;
    static final int SAPLING_ACTION_ANIMATION_PERIOD = 1000; // have to be in sync since grows and gains health at same time
    private final int SAPLING_NUM_PROPERTIES = 4;
    private final int SAPLING_ID = 1;
    private final int SAPLING_COL = 2;
    private final int SAPLING_ROW = 3;
    private final int SAPLING_HEALTH = 4;
    private final int BGND_NUM_PROPERTIES = 4;
    private final int BGND_ID = 1;
    private final int BGND_COL = 2;
    private final int BGND_ROW = 3;
    public int numRows;
    public int numCols;
    public Background background[][];
    public Entity occupancy[][];
    public Set<Entity> entities;

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }

    public boolean parseBackground(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                    Integer.parseInt(properties[BGND_ROW]));
            String id = properties[BGND_ID];
            new Background(id, imageStore.getImageList(imageStore, id)).setBackground(world, pt);
        }

        return properties.length == BGND_NUM_PROPERTIES;
    }

    private boolean parseSapling(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[SAPLING_COL]),
                    Integer.parseInt(properties[SAPLING_ROW]));
            String id = properties[SAPLING_ID];
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            Entity entity = new Entity(EntityKind.SAPLING, id, pt, imageStore.getImageList(imageStore, SAPLING_KEY), 0, 0,
                    SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, health, SAPLING_HEALTH_LIMIT);
            tryAddEntity(world, entity);
        }

        return properties.length == SAPLING_NUM_PROPERTIES;
    }

    private boolean parseDude(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[DUDE_COL]),
                    Integer.parseInt(properties[DUDE_ROW]));
            Entity entity = Entity.createDudeNotFull(properties[DUDE_ID],
                    pt,
                    Integer.parseInt(properties[DUDE_ACTION_PERIOD]),
                    Integer.parseInt(properties[DUDE_ANIMATION_PERIOD]),
                    Integer.parseInt(properties[DUDE_LIMIT]),
                    imageStore.getImageList(imageStore, DUDE_KEY));
            tryAddEntity(world, entity);
        }

        return properties.length == DUDE_NUM_PROPERTIES;
    }

    private boolean parseFairy(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[FAIRY_COL]),
                    Integer.parseInt(properties[FAIRY_ROW]));
            Entity entity = Entity.createFairy(properties[FAIRY_ID],
                    pt,
                    Integer.parseInt(properties[FAIRY_ACTION_PERIOD]),
                    Integer.parseInt(properties[FAIRY_ANIMATION_PERIOD]),
                    imageStore.getImageList(imageStore, FAIRY_KEY));
            tryAddEntity(world, entity);
        }

        return properties.length == FAIRY_NUM_PROPERTIES;
    }

    private boolean parseTree(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[TREE_COL]),
                    Integer.parseInt(properties[TREE_ROW]));
            Entity entity = Entity.createTree(properties[TREE_ID],
                    pt,
                    Integer.parseInt(properties[TREE_ACTION_PERIOD]),
                    Integer.parseInt(properties[TREE_ANIMATION_PERIOD]),
                    Integer.parseInt(properties[TREE_HEALTH]),
                    imageStore.getImageList(imageStore, TREE_KEY));
            tryAddEntity(world, entity);
        }

        return properties.length == TREE_NUM_PROPERTIES;
    }

    private boolean parseObstacle(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[OBSTACLE_COL]),
                    Integer.parseInt(properties[OBSTACLE_ROW]));
            Entity entity = Entity.createObstacle(properties[OBSTACLE_ID], pt,
                    Integer.parseInt(properties[OBSTACLE_ANIMATION_PERIOD]),
                    imageStore.getImageList(imageStore,
                            OBSTACLE_KEY));
            tryAddEntity(world, entity);
        }

        return properties.length == OBSTACLE_NUM_PROPERTIES;
    }

    private boolean parseHouse(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[HOUSE_COL]),
                    Integer.parseInt(properties[HOUSE_ROW]));
            Entity entity = Entity.createHouse(properties[HOUSE_ID], pt,
                    imageStore.getImageList(imageStore,
                            HOUSE_KEY));
            tryAddEntity(world, entity);
        }

        return properties.length == HOUSE_NUM_PROPERTIES;
    }

    private boolean processLine(
            String line, WorldModel world, ImageStore imageStore)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[PROPERTY_KEY]) {
                case BGND_KEY:
                    return parseBackground(properties, world, imageStore);
                case DUDE_KEY:
                    return parseDude(properties, world, imageStore);
                case OBSTACLE_KEY:
                    return parseObstacle(properties, world, imageStore);
                case FAIRY_KEY:
                    return parseFairy(properties, world, imageStore);
                case HOUSE_KEY:
                    return parseHouse(properties, world, imageStore);
                case TREE_KEY:
                    return parseTree(properties, world, imageStore);
                case SAPLING_KEY:
                    return parseSapling(properties, world, imageStore);
            }
        }

        return false;
    }

    public void load(
            Scanner in, WorldModel world, ImageStore imageStore)
    {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                if (!processLine(in.nextLine(), world, imageStore)) {
                    System.err.println(String.format("invalid entry on line %d",
                            lineNumber));
                }
            }
            catch (NumberFormatException e) {
                System.err.println(
                        String.format("invalid entry on line %d", lineNumber));
            }
            catch (IllegalArgumentException e) {
                System.err.println(
                        String.format("issue on line %d: %s", lineNumber,
                                e.getMessage()));
            }
            lineNumber++;
        }
    }

    private void tryAddEntity(WorldModel world, Entity entity) {
        if (isOccupied(world, entity.position)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        addEntity(world, entity);
    }

    public boolean withinBounds(WorldModel world, Point pos) {
        return pos.y >= 0 && pos.y < world.numRows && pos.x >= 0
                && pos.x < world.numCols;
    }

    public boolean isOccupied(WorldModel world, Point pos) {
        return withinBounds(world, pos) && getOccupancyCell(world, pos) != null;
    }

    /*
           Assumes that there is no entity currently occupying the
           intended destination cell.
        */
    public void addEntity(WorldModel world, Entity entity) {
        if (withinBounds(world, entity.position)) {
            setOccupancyCell(world, entity.position, entity);
            world.entities.add(entity);
        }
    }

    public void moveEntity(WorldModel world, Entity entity, Point pos) {
        Point oldPos = entity.position;
        if (withinBounds(world, pos) && !pos.equals(oldPos)) {
            setOccupancyCell(world, oldPos, null);
            removeEntityAt(world, pos);
            setOccupancyCell(world, pos, entity);
            entity.position = pos;
        }
    }

    public void removeEntity(WorldModel world, Entity entity) {
        removeEntityAt(world, entity.position);
    }

    public void removeEntityAt(WorldModel world, Point pos) {
        if (withinBounds(world, pos) && getOccupancyCell(world, pos) != null) {
            Entity entity = getOccupancyCell(world, pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.position = new Point(-1, -1);
            world.entities.remove(entity);
            setOccupancyCell(world, pos, null);
        }
    }

    public Optional<Entity> getOccupant(WorldModel world, Point pos) {
        if (isOccupied(world, pos)) {
            return Optional.of(getOccupancyCell(world, pos));
        }
        else {
            return Optional.empty();
        }
    }

    public Entity getOccupancyCell(WorldModel world, Point pos) {
        return world.occupancy[pos.y][pos.x];
    }

    public void setOccupancyCell(
            WorldModel world, Point pos, Entity entity)
    {
        world.occupancy[pos.y][pos.x] = entity;
    }

    public void setBackgroundCell(
            WorldModel world, Point pos, Background background)
    {
        world.background[pos.y][pos.x] = background;
    }

    private Optional<Entity> nearestEntity(
            List<Entity> entities, Point pos)
    {
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        else {
            Entity nearest = entities.get(0);
            int nearestDistance = distanceSquared(nearest.position, pos);

            for (Entity other : entities) {
                int otherDistance = distanceSquared(other.position, pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    private int distanceSquared(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public Optional<Entity> findNearest(
            WorldModel world, Point pos, List<EntityKind> kinds)
    {
        List<Entity> ofType = new LinkedList<>();
        for (EntityKind kind: kinds)
        {
            for (Entity entity : world.entities) {
                if (entity.kind == kind) {
                    ofType.add(entity);
                }
            }
        }

        return nearestEntity(ofType, pos);
    }
}