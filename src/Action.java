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

    public void executeAction(EventScheduler scheduler) {
        switch (this.kind) {
            case ACTIVITY:
                executeActivityAction(this, scheduler);
                break;

            case ANIMATION:
                executeAnimationAction(this, scheduler);
                break;
        }
    }

    private void executeAnimationAction(
            Action action, EventScheduler scheduler)
    {
        nextImage(action.entity);

        if (action.repeatCount != 1) {
            scheduler.scheduleEvent(scheduler, action.entity,
                    createAnimationAction(action.entity,
                            Math.max(action.repeatCount - 1,
                                    0)),
                    scheduler.getAnimationPeriod(action.entity));
        }
    }

    private void executeActivityAction(
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

    private void executeSaplingActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        entity.health++;
        if (!entity.transformPlant(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(scheduler, entity,
                    createActivityAction(entity, world, imageStore),
                    entity.actionPeriod);
        }
    }

    private void executeTreeActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {

        if (!entity.transformPlant(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(scheduler, entity,
                    createActivityAction(entity, world, imageStore),
                    entity.actionPeriod);
        }
    }

    private void executeFairyActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fairyTarget =
                world.findNearest(world, entity.position, new ArrayList<>(Arrays.asList(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (entity.moveToFairy(entity, world, fairyTarget.get(), scheduler)) {
                Entity sapling = entity.createSapling("sapling_" + entity.id, tgtPos,
                        imageStore.getImageList(imageStore, world.SAPLING_KEY));

                world.addEntity(world, sapling);
                scheduler.scheduleActions(sapling, scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(scheduler, entity,
                createActivityAction(entity, world, imageStore),
                entity.actionPeriod);
    }

    private void executeDudeNotFullActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> target =
                world.findNearest(world, entity.position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (!target.isPresent() || !entity.moveToNotFull(entity, world,
                target.get(),
                scheduler)
                || !entity.transformNotFull(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(scheduler, entity,
                    createActivityAction(entity, world, imageStore),
                    entity.actionPeriod);
        }
    }

    private void executeDudeFullActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
                world.findNearest(world, entity.position, new ArrayList<>(Arrays.asList(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && entity.moveToFull(entity, world,
                fullTarget.get(), scheduler))
        {
            entity.transformFull(world, scheduler, imageStore);
        }
        else {
            scheduler.scheduleEvent(scheduler, entity,
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

    private void nextImage(Entity entity) {
        entity.imageIndex = (entity.imageIndex + 1) % entity.images.size();
    }
}