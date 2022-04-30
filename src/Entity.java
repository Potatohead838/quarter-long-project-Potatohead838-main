import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Entity
{


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
    public Entity createSapling(
            String id,
            Point position,
            List<PImage> images)
    {
        return new Entity(EntityKind.SAPLING, id, position, images, 0, 0,
                WorldModel.SAPLING_ACTION_ANIMATION_PERIOD, WorldModel.SAPLING_ACTION_ANIMATION_PERIOD, 0, WorldModel.SAPLING_HEALTH_LIMIT);
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
    public Entity createDudeFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images) {
        return new Entity(EntityKind.DUDE_FULL, id, position, images, resourceLimit, 0,
                actionPeriod, animationPeriod, 0, 0);
    }

    public boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.resourceCount >= this.resourceLimit) {
            Entity miner = createDudeFull(this.id,
                    this.position, this.actionPeriod,
                    this.animationPeriod,
                    this.resourceLimit,
                    this.images);

            world.removeEntity(world, this);
            scheduler.unscheduleAllEvents(scheduler, this);

            world.addEntity(world, miner);
            scheduler.scheduleActions(miner, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        Entity miner = createDudeNotFull(this.id,
                this.position, this.actionPeriod,
                this.animationPeriod,
                this.resourceLimit,
                this.images);

        world.removeEntity(world, this);
        scheduler.unscheduleAllEvents(scheduler, this);

        world.addEntity(world, miner);
        scheduler.scheduleActions(miner, scheduler, world, imageStore);
    }

    public boolean transformPlant(WorldModel world,
                                         EventScheduler scheduler,
                                         ImageStore imageStore)
    {
        if (this.kind == EntityKind.TREE)
        {
            return transformTree(world, scheduler, imageStore);
        }
        else if (this.kind == EntityKind.SAPLING)
        {
            return transformSapling(world, scheduler, imageStore);
        }
        else
        {
            throw new UnsupportedOperationException(
                    String.format("transformPlant not supported for %s", this));
        }
    }

    public boolean transformTree(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.health <= 0) {
            Entity stump = createStump(this.id,
                    this.position,
                    imageStore.getImageList(imageStore, world.STUMP_KEY));

            world.removeEntity(world, this);
            scheduler.unscheduleAllEvents(scheduler, this);

            world.addEntity(world, stump);
            scheduler.scheduleActions(stump, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean transformSapling(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.health <= 0) {
            Entity stump = createStump(this.id,
                    this.position,
                    imageStore.getImageList(imageStore, world.STUMP_KEY));

            world.removeEntity(world, this);
            scheduler.unscheduleAllEvents(scheduler, this);

            world.addEntity(world, stump);
            scheduler.scheduleActions(stump, scheduler, world, imageStore);

            return true;
        }
        else if (this.health >= this.healthLimit)
        {
            Entity tree = createTree("tree_" + this.id,
                    this.position,
                    getNumFromRange(world.TREE_ACTION_MAX, world.TREE_ACTION_MIN),
                    getNumFromRange(world.TREE_ANIMATION_MAX, world.TREE_ANIMATION_MIN),
                    getNumFromRange(world.TREE_HEALTH_MAX, world.TREE_HEALTH_MIN),
                    imageStore.getImageList(imageStore, world.TREE_KEY));

            world.removeEntity(world, this);
            scheduler.unscheduleAllEvents(scheduler, this);

            world.addEntity(world, tree);
            scheduler.scheduleActions(tree, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean moveToFairy(
            Entity fairy,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (adjacent(fairy.position, target.position)) {
            world.removeEntity(world, target);
            scheduler.unscheduleAllEvents(scheduler, target);
            return true;
        }
        else {
            Point nextPos = nextPositionFairy(fairy, world, target.position);

            if (!fairy.position.equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(scheduler, occupant.get());
                }

                world.moveEntity(world, fairy, nextPos);
            }
            return false;
        }
    }

    public boolean moveToNotFull(
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
                Optional<Entity> occupant = world.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(scheduler, occupant.get());
                }

                world.moveEntity(world, dude, nextPos);
            }
            return false;
        }
    }

    public boolean moveToFull(
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
                Optional<Entity> occupant = world.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(scheduler, occupant.get());
                }

                world.moveEntity(world, dude, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionFairy(
            Entity entity, WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - entity.position.x);
        Point newPos = new Point(entity.position.x + horiz, entity.position.y);

        if (horiz == 0 || world.isOccupied(world, newPos)) {
            int vert = Integer.signum(destPos.y - entity.position.y);
            newPos = new Point(entity.position.x, entity.position.y + vert);

            if (vert == 0 || world.isOccupied(world, newPos)) {
                newPos = entity.position;
            }
        }

        return newPos;
    }

    public Point nextPositionDude(
            Entity entity, WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - entity.position.x);
        Point newPos = new Point(entity.position.x + horiz, entity.position.y);

        if (horiz == 0 || world.isOccupied(world, newPos) && world.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
            int vert = Integer.signum(destPos.y - entity.position.y);
            newPos = new Point(entity.position.x, entity.position.y + vert);

            if (vert == 0 || world.isOccupied(world, newPos) &&  world.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
                newPos = entity.position;
            }
        }

        return newPos;
    }

    private boolean adjacent(Point p1, Point p2) {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y
                && Math.abs(p1.x - p2.x) == 1);
    }

    private int getNumFromRange(int max, int min)
    {
        Random rand = new Random();
        return min + rand.nextInt(
                max
                        - min);
    }

}