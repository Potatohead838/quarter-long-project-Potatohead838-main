import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Entity
{
    public static final String SAPLING_KEY = "sapling";
    private static final String TREE_KEY = "tree";
    private static final String STUMP_KEY = "stump";
    private static final String FAIRY_KEY = "fairy";
    private static final String HOUSE_KEY = "house";
    private static final String DUDE_KEY = "dude";
    private static final String OBSTACLE_KEY = "obstacle";
    private static final String BGND_KEY = "background";
    private static final List<String> PATH_KEYS = new ArrayList<>(Arrays.asList("bridge", "dirt", "dirt_horiz", "dirt_vert_left", "dirt_vert_right",
            "dirt_bot_left_corner", "dirt_bot_right_up", "dirt_vert_left_bot"));
    private static final int PROPERTY_KEY = 0;
    private static final int TREE_HEALTH_MIN = 1;
    private static final int TREE_HEALTH_MAX = 3;
    private static final int TREE_ACTION_MIN = 1000;
    private static final int TREE_ACTION_MAX = 1400;
    private static final int TREE_ANIMATION_MIN = 50;
    private static final int TREE_ANIMATION_MAX = 600;

    private static final int OBSTACLE_NUM_PROPERTIES = 5;
    private static final int OBSTACLE_ID = 1;
    private static final int OBSTACLE_COL = 2;
    private static final int OBSTACLE_ROW = 3;
    private static final int OBSTACLE_ANIMATION_PERIOD = 4;

    private static final int DUDE_NUM_PROPERTIES = 7;
    private static final int DUDE_ID = 1;
    private static final int DUDE_COL = 2;
    private static final int DUDE_ROW = 3;
    private static final int DUDE_LIMIT = 4;
    private static final int DUDE_ACTION_PERIOD = 5;
    private static final int DUDE_ANIMATION_PERIOD = 6;

    private static final int HOUSE_NUM_PROPERTIES = 4;
    private static final int HOUSE_ID = 1;
    private static final int HOUSE_COL = 2;
    private static final int HOUSE_ROW = 3;

    private static final int FAIRY_NUM_PROPERTIES = 6;
    private static final int FAIRY_ID = 1;
    private static final int FAIRY_COL = 2;
    private static final int FAIRY_ROW = 3;
    private static final int FAIRY_ANIMATION_PERIOD = 4;
    private static final int FAIRY_ACTION_PERIOD = 5;

    private static final int TREE_NUM_PROPERTIES = 7;
    private static final int TREE_ID = 1;
    private static final int TREE_COL = 2;
    private static final int TREE_ROW = 3;
    private static final int TREE_ANIMATION_PERIOD = 4;
    private static final int TREE_ACTION_PERIOD = 5;
    private static final int TREE_HEALTH = 6;

    private static final int SAPLING_HEALTH_LIMIT = 5;
    private static final int SAPLING_ACTION_ANIMATION_PERIOD = 1000; // have to be in sync since grows and gains health at same time
    private static final int SAPLING_NUM_PROPERTIES = 4;
    private static final int SAPLING_ID = 1;
    private static final int SAPLING_COL = 2;
    private static final int SAPLING_ROW = 3;
    private static final int SAPLING_HEALTH = 4;


    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;
    public int resourceLimit;
    public int resourceCount;
    public int actionPeriod;
    public int animationPeriod;
    public int health;
    public int healthLimit;

    public Entity(
            EntityKind kind,
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod,
            int health,
            int healthLimit)
    {
        this.kind = kind;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }

    public static Entity createHouse(
            String id, Point position, List<PImage> images)
    {
        return new Entity(EntityKind.HOUSE, id, position, images, 0, 0, 0,
                0, 0, 0);
    }

    public static Entity createObstacle(
            String id, Point position, int animationPeriod, List<PImage> images)
    {
        return new Entity(EntityKind.OBSTACLE, id, position, images, 0, 0, 0,
                animationPeriod, 0, 0);
    }

    public static Entity createTree(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int health,
            List<PImage> images)
    {
        return new Entity(EntityKind.TREE, id, position, images, 0, 0,
                actionPeriod, animationPeriod, health, 0);
    }

    public static Entity createStump(
            String id,
            Point position,
            List<PImage> images)
    {
        return new Entity(EntityKind.STUMP, id, position, images, 0, 0,
                0, 0, 0, 0);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public static Entity createSapling(
            String id,
            Point position,
            List<PImage> images)
    {
        return new Entity(EntityKind.SAPLING, id, position, images, 0, 0,
                SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT);
    }

    public static Entity createFairy(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Entity(EntityKind.FAIRY, id, position, images, 0, 0,
                actionPeriod, animationPeriod, 0, 0);
    }

    // need resource count, though it always starts at 0
    public static Entity createDudeNotFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images)
    {
        return new Entity(EntityKind.DUDE_NOT_FULL, id, position, images, resourceLimit, 0,
                actionPeriod, animationPeriod, 0, 0);
    }

    // don't technically need resource count ... full
    public static Entity createDudeFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images) {
        return new Entity(EntityKind.DUDE_FULL, id, position, images, resourceLimit, 0,
                actionPeriod, animationPeriod, 0, 0);
    }

    public static boolean transformNotFull(
            Entity entity,
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (entity.resourceCount >= entity.resourceLimit) {
            Entity miner = createDudeFull(entity.id,
                    entity.position, entity.actionPeriod,
                    entity.animationPeriod,
                    entity.resourceLimit,
                    entity.images);

            WorldModel.removeEntity(world, entity);
            EventScheduler.unscheduleAllEvents(scheduler, entity);

            WorldModel.addEntity(world, miner);
            EventScheduler.scheduleActions(miner, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public static void transformFull(
            Entity entity,
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        Entity miner = createDudeNotFull(entity.id,
                entity.position, entity.actionPeriod,
                entity.animationPeriod,
                entity.resourceLimit,
                entity.images);

        WorldModel.removeEntity(world, entity);
        EventScheduler.unscheduleAllEvents(scheduler, entity);

        WorldModel.addEntity(world, miner);
        EventScheduler.scheduleActions(miner, scheduler, world, imageStore);
    }

    public static boolean transformPlant(Entity entity,
                                         WorldModel world,
                                         EventScheduler scheduler,
                                         ImageStore imageStore)
    {
        if (entity.kind == EntityKind.TREE)
        {
            return transformTree(entity, world, scheduler, imageStore);
        }
        else if (entity.kind == EntityKind.SAPLING)
        {
            return transformSapling(entity, world, scheduler, imageStore);
        }
        else
        {
            throw new UnsupportedOperationException(
                    String.format("transformPlant not supported for %s", entity));
        }
    }

    public static boolean transformTree(
            Entity entity,
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (entity.health <= 0) {
            Entity stump = createStump(entity.id,
                    entity.position,
                    ImageStore.getImageList(imageStore, STUMP_KEY));

            WorldModel.removeEntity(world, entity);
            EventScheduler.unscheduleAllEvents(scheduler, entity);

            WorldModel.addEntity(world, stump);
            EventScheduler.scheduleActions(stump, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public static boolean transformSapling(
            Entity entity,
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (entity.health <= 0) {
            Entity stump = createStump(entity.id,
                    entity.position,
                    ImageStore.getImageList(imageStore, STUMP_KEY));

            WorldModel.removeEntity(world, entity);
            EventScheduler.unscheduleAllEvents(scheduler, entity);

            WorldModel.addEntity(world, stump);
            EventScheduler.scheduleActions(stump, scheduler, world, imageStore);

            return true;
        }
        else if (entity.health >= entity.healthLimit)
        {
            Entity tree = createTree("tree_" + entity.id,
                    entity.position,
                    getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN),
                    getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN),
                    getNumFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN),
                    ImageStore.getImageList(imageStore, TREE_KEY));

            WorldModel.removeEntity(world, entity);
            EventScheduler.unscheduleAllEvents(scheduler, entity);

            WorldModel.addEntity(world, tree);
            EventScheduler.scheduleActions(tree, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public static boolean moveToFairy(
            Entity fairy,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (adjacent(fairy.position, target.position)) {
            WorldModel.removeEntity(world, target);
            EventScheduler.unscheduleAllEvents(scheduler, target);
            return true;
        }
        else {
            Point nextPos = nextPositionFairy(fairy, world, target.position);

            if (!fairy.position.equals(nextPos)) {
                Optional<Entity> occupant = WorldModel.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    EventScheduler.unscheduleAllEvents(scheduler, occupant.get());
                }

                WorldModel.moveEntity(world, fairy, nextPos);
            }
            return false;
        }
    }

    public static boolean moveToNotFull(
            Entity dude,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (adjacent(dude.position, target.position)) {
            dude.resourceCount += 1;
            target.health--;
            return true;
        }
        else {
            Point nextPos = nextPositionDude(dude, world, target.position);

            if (!dude.position.equals(nextPos)) {
                Optional<Entity> occupant = WorldModel.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    EventScheduler.unscheduleAllEvents(scheduler, occupant.get());
                }

                WorldModel.moveEntity(world, dude, nextPos);
            }
            return false;
        }
    }

    public static boolean moveToFull(
            Entity dude,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (adjacent(dude.position, target.position)) {
            return true;
        }
        else {
            Point nextPos = nextPositionDude(dude, world, target.position);

            if (!dude.position.equals(nextPos)) {
                Optional<Entity> occupant = WorldModel.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    EventScheduler.unscheduleAllEvents(scheduler, occupant.get());
                }

                WorldModel.moveEntity(world, dude, nextPos);
            }
            return false;
        }
    }

    public static Point nextPositionFairy(
            Entity entity, WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - entity.position.x);
        Point newPos = new Point(entity.position.x + horiz, entity.position.y);

        if (horiz == 0 || WorldModel.isOccupied(world, newPos)) {
            int vert = Integer.signum(destPos.y - entity.position.y);
            newPos = new Point(entity.position.x, entity.position.y + vert);

            if (vert == 0 || WorldModel.isOccupied(world, newPos)) {
                newPos = entity.position;
            }
        }

        return newPos;
    }

    public static Point nextPositionDude(
            Entity entity, WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - entity.position.x);
        Point newPos = new Point(entity.position.x + horiz, entity.position.y);

        if (horiz == 0 || WorldModel.isOccupied(world, newPos) && WorldModel.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
            int vert = Integer.signum(destPos.y - entity.position.y);
            newPos = new Point(entity.position.x, entity.position.y + vert);

            if (vert == 0 || WorldModel.isOccupied(world, newPos) &&  WorldModel.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
                newPos = entity.position;
            }
        }

        return newPos;
    }

    public static boolean adjacent(Point p1, Point p2) {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y
                && Math.abs(p1.x - p2.x) == 1);
    }

    public static int getNumFromRange(int max, int min)
    {
        Random rand = new Random();
        return min + rand.nextInt(
                max
                        - min);
    }

    public static boolean parseSapling(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[SAPLING_COL]),
                    Integer.parseInt(properties[SAPLING_ROW]));
            String id = properties[SAPLING_ID];
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            Entity entity = new Entity(EntityKind.SAPLING, id, pt, ImageStore.getImageList(imageStore, SAPLING_KEY), 0, 0,
                    SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, health, SAPLING_HEALTH_LIMIT);
            WorldModel.tryAddEntity(world, entity);
        }

        return properties.length == SAPLING_NUM_PROPERTIES;
    }

    public static boolean parseDude(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[DUDE_COL]),
                    Integer.parseInt(properties[DUDE_ROW]));
            Entity entity = createDudeNotFull(properties[DUDE_ID],
                    pt,
                    Integer.parseInt(properties[DUDE_ACTION_PERIOD]),
                    Integer.parseInt(properties[DUDE_ANIMATION_PERIOD]),
                    Integer.parseInt(properties[DUDE_LIMIT]),
                    ImageStore.getImageList(imageStore, DUDE_KEY));
            WorldModel.tryAddEntity(world, entity);
        }

        return properties.length == DUDE_NUM_PROPERTIES;
    }

    public static boolean parseFairy(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[FAIRY_COL]),
                    Integer.parseInt(properties[FAIRY_ROW]));
            Entity entity = createFairy(properties[FAIRY_ID],
                    pt,
                    Integer.parseInt(properties[FAIRY_ACTION_PERIOD]),
                    Integer.parseInt(properties[FAIRY_ANIMATION_PERIOD]),
                    ImageStore.getImageList(imageStore, FAIRY_KEY));
            WorldModel.tryAddEntity(world, entity);
        }

        return properties.length == FAIRY_NUM_PROPERTIES;
    }

    public static boolean parseTree(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[TREE_COL]),
                    Integer.parseInt(properties[TREE_ROW]));
            Entity entity = createTree(properties[TREE_ID],
                    pt,
                    Integer.parseInt(properties[TREE_ACTION_PERIOD]),
                    Integer.parseInt(properties[TREE_ANIMATION_PERIOD]),
                    Integer.parseInt(properties[TREE_HEALTH]),
                    ImageStore.getImageList(imageStore, TREE_KEY));
            WorldModel.tryAddEntity(world, entity);
        }

        return properties.length == TREE_NUM_PROPERTIES;
    }

    public static boolean parseObstacle(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[OBSTACLE_COL]),
                    Integer.parseInt(properties[OBSTACLE_ROW]));
            Entity entity = createObstacle(properties[OBSTACLE_ID], pt,
                    Integer.parseInt(properties[OBSTACLE_ANIMATION_PERIOD]),
                    ImageStore.getImageList(imageStore,
                            OBSTACLE_KEY));
            WorldModel.tryAddEntity(world, entity);
        }

        return properties.length == OBSTACLE_NUM_PROPERTIES;
    }

    public static boolean parseHouse(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[HOUSE_COL]),
                    Integer.parseInt(properties[HOUSE_ROW]));
            Entity entity = createHouse(properties[HOUSE_ID], pt,
                    ImageStore.getImageList(imageStore,
                            HOUSE_KEY));
            WorldModel.tryAddEntity(world, entity);
        }

        return properties.length == HOUSE_NUM_PROPERTIES;
    }

    public static boolean processLine(
            String line, WorldModel world, ImageStore imageStore)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[PROPERTY_KEY]) {
                case BGND_KEY:
                    return Background.parseBackground(properties, world, imageStore);
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
}