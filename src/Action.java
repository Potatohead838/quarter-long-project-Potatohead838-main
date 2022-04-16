import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * An action that can be taken by an entity
 */
public final class Action
{
    public ActionKind kind;
    public Entity entity;
    public WorldModel world;
    public ImageStore imageStore;
    public int repeatCount;

    public Action(
            ActionKind kind,
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.kind = kind;
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public static int getAnimationPeriod(Entity entity) {
        switch (entity.kind) {
            case DUDE_FULL:
            case DUDE_NOT_FULL:
            case OBSTACLE:
            case FAIRY:
            case SAPLING:
            case TREE:
                return entity.animationPeriod;
            default:
                throw new UnsupportedOperationException(
                        String.format("getAnimationPeriod not supported for %s",
                                entity.kind));
        }
    }

    public static void executeAction(Action action, EventScheduler scheduler) {
        switch (action.kind) {
            case ACTIVITY:
                executeActivityAction(action, scheduler);
                break;

            case ANIMATION:
                executeAnimationAction(action, scheduler);
                break;
        }
    }

    public static void executeAnimationAction(
            Action action, EventScheduler scheduler)
    {
        nextImage(action.entity);

        if (action.repeatCount != 1) {
            EventScheduler.scheduleEvent(scheduler, action.entity,
                    createAnimationAction(action.entity,
                            Math.max(action.repeatCount - 1,
                                    0)),
                    getAnimationPeriod(action.entity));
        }
    }

    public static void executeActivityAction(
            Action action, EventScheduler scheduler)
    {
        switch (action.entity.kind) {
            case SAPLING:
                executeSaplingActivity(action.entity, action.world,
                        action.imageStore, scheduler);
                break;

            case TREE:
                executeTreeActivity(action.entity, action.world,
                        action.imageStore, scheduler);
                break;

            case FAIRY:
                executeFairyActivity(action.entity, action.world,
                        action.imageStore, scheduler);
                break;

            case DUDE_NOT_FULL:
                executeDudeNotFullActivity(action.entity, action.world,
                        action.imageStore, scheduler);
                break;

            case DUDE_FULL:
                executeDudeFullActivity(action.entity, action.world,
                        action.imageStore, scheduler);
                break;

            default:
                throw new UnsupportedOperationException(String.format(
                        "executeActivityAction not supported for %s",
                        action.entity.kind));
        }
    }

    public static void executeSaplingActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        entity.health++;
        if (!Entity.transformPlant(entity, world, scheduler, imageStore))
        {
            EventScheduler.scheduleEvent(scheduler, entity,
                    createActivityAction(entity, world, imageStore),
                    entity.actionPeriod);
        }
    }

    public static void executeTreeActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {

        if (!Entity.transformPlant(entity, world, scheduler, imageStore)) {

            EventScheduler.scheduleEvent(scheduler, entity,
                    createActivityAction(entity, world, imageStore),
                    entity.actionPeriod);
        }
    }

    public static void executeFairyActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fairyTarget =
                WorldModel.findNearest(world, entity.position, new ArrayList<>(Arrays.asList(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (Entity.moveToFairy(entity, world, fairyTarget.get(), scheduler)) {
                Entity sapling = Entity.createSapling("sapling_" + entity.id, tgtPos,
                        ImageStore.getImageList(imageStore, Entity.SAPLING_KEY));

                WorldModel.addEntity(world, sapling);
                EventScheduler.scheduleActions(sapling, scheduler, world, imageStore);
            }
        }

        EventScheduler.scheduleEvent(scheduler, entity,
                createActivityAction(entity, world, imageStore),
                entity.actionPeriod);
    }

    public static void executeDudeNotFullActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> target =
                WorldModel.findNearest(world, entity.position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (!target.isPresent() || !Entity.moveToNotFull(entity, world,
                target.get(),
                scheduler)
                || !Entity.transformNotFull(entity, world, scheduler, imageStore))
        {
            EventScheduler.scheduleEvent(scheduler, entity,
                    createActivityAction(entity, world, imageStore),
                    entity.actionPeriod);
        }
    }

    public static void executeDudeFullActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
                WorldModel.findNearest(world, entity.position, new ArrayList<>(Arrays.asList(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && Entity.moveToFull(entity, world,
                fullTarget.get(), scheduler))
        {
            Entity.transformFull(entity, world, scheduler, imageStore);
        }
        else {
            EventScheduler.scheduleEvent(scheduler, entity,
                    createActivityAction(entity, world, imageStore),
                    entity.actionPeriod);
        }
    }

    public static Action createAnimationAction(Entity entity, int repeatCount) {
        return new Action(ActionKind.ANIMATION, entity, null, null,
                repeatCount);
    }

    public static Action createActivityAction(
            Entity entity, WorldModel world, ImageStore imageStore)
    {
        return new Action(ActionKind.ACTIVITY, entity, world, imageStore, 0);
    }

    private static void nextImage(Entity entity) {
        entity.imageIndex = (entity.imageIndex + 1) % entity.images.size();
    }
}
