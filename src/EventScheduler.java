import java.util.*;

/**
 * Keeps track of events that have been scheduled.
 */
public final class EventScheduler
{
    private PriorityQueue<Event> eventQueue;
    private Map<Entity, List<Event>> pendingEvents;
    private double timeScale;

    public EventScheduler(double timeScale) {
        this.eventQueue = new PriorityQueue<>(new EventComparator());
        this.pendingEvents = new HashMap<>();
        this.timeScale = timeScale;
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

    private void removePendingEvent(
            Event event)
    {
        List<Event> pending = this.pendingEvents.get(event.entity);

        if (pending != null) {
            pending.remove(event);
        }
    }

    public void unscheduleAllEvents(
            EventScheduler scheduler, Entity entity)
    {
        List<Event> pending = scheduler.pendingEvents.remove(entity);

        if (pending != null) {
            for (Event event : pending) {
                scheduler.eventQueue.remove(event);
            }
        }
    }

    public void scheduleEvent(
            EventScheduler scheduler,
            Entity entity,
            Action action,
            long afterPeriod)
    {
        long time = System.currentTimeMillis() + (long)(afterPeriod
                * scheduler.timeScale);
        Event event = new Event(action, time, entity);

        scheduler.eventQueue.add(event);

        // update list of pending events for the given entity
        List<Event> pending = scheduler.pendingEvents.getOrDefault(entity,
                new LinkedList<>());
        pending.add(event);
        scheduler.pendingEvents.put(entity, pending);
    }

    public void updateOnTime(long time) {
        while (!this.eventQueue.isEmpty()
                && this.eventQueue.peek().time < time) {
            Event next = this.eventQueue.poll();

            removePendingEvent(next);

            next.action.executeAction(this);
        }
    }

    public void scheduleActions(
            Entity entity,
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        switch (entity.kind) {
            case DUDE_FULL:
                scheduleEvent(scheduler, entity,
                        Action.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Action.createAnimationAction(entity, 0),
                        scheduler.getAnimationPeriod(entity));
                break;

            case DUDE_NOT_FULL:
                scheduleEvent(scheduler, entity,
                        Action.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Action.createAnimationAction(entity, 0),
                        scheduler.getAnimationPeriod(entity));
                break;

            case OBSTACLE:
                scheduleEvent(scheduler, entity,
                        Action.createAnimationAction(entity, 0),
                        scheduler.getAnimationPeriod(entity));
                break;

            case FAIRY:
                scheduleEvent(scheduler, entity,
                        Action.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Action.createAnimationAction(entity, 0),
                        scheduler.getAnimationPeriod(entity));
                break;

            case SAPLING:
                scheduleEvent(scheduler, entity,
                        Action.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Action.createAnimationAction(entity, 0),
                        scheduler.getAnimationPeriod(entity));
                break;

            case TREE:
                scheduleEvent(scheduler, entity,
                        Action.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Action.createAnimationAction(entity, 0),
                        scheduler.getAnimationPeriod(entity));
                break;

            default:
        }
    }
}
