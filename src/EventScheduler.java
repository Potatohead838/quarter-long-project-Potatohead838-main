import java.util.*;

/**
 * Keeps track of events that have been scheduled.
 */
public final class EventScheduler
{
    public PriorityQueue<Event> eventQueue;
    public Map<Entity, List<Event>> pendingEvents;
    public double timeScale;

    public EventScheduler(double timeScale) {
        this.eventQueue = new PriorityQueue<>(new EventComparator());
        this.pendingEvents = new HashMap<>();
        this.timeScale = timeScale;
    }

    public static void removePendingEvent(
            EventScheduler scheduler, Event event)
    {
        List<Event> pending = scheduler.pendingEvents.get(event.entity);

        if (pending != null) {
            pending.remove(event);
        }
    }

    public static void unscheduleAllEvents(
            EventScheduler scheduler, Entity entity)
    {
        List<Event> pending = scheduler.pendingEvents.remove(entity);

        if (pending != null) {
            for (Event event : pending) {
                scheduler.eventQueue.remove(event);
            }
        }
    }

    public static void scheduleEvent(
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

    public static void updateOnTime(EventScheduler scheduler, long time) {
        while (!scheduler.eventQueue.isEmpty()
                && scheduler.eventQueue.peek().time < time) {
            Event next = scheduler.eventQueue.poll();

            removePendingEvent(scheduler, next);

            Action.executeAction(next.action, scheduler);
        }
    }

    public static void scheduleActions(
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
                        Action.getAnimationPeriod(entity));
                break;

            case DUDE_NOT_FULL:
                scheduleEvent(scheduler, entity,
                        Action.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Action.createAnimationAction(entity, 0),
                        Action.getAnimationPeriod(entity));
                break;

            case OBSTACLE:
                scheduleEvent(scheduler, entity,
                        Action.createAnimationAction(entity, 0),
                        Action.getAnimationPeriod(entity));
                break;

            case FAIRY:
                scheduleEvent(scheduler, entity,
                        Action.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Action.createAnimationAction(entity, 0),
                        Action.getAnimationPeriod(entity));
                break;

            case SAPLING:
                scheduleEvent(scheduler, entity,
                        Action.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Action.createAnimationAction(entity, 0),
                        Action.getAnimationPeriod(entity));
                break;

            case TREE:
                scheduleEvent(scheduler, entity,
                        Action.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Action.createAnimationAction(entity, 0),
                        Action.getAnimationPeriod(entity));
                break;

            default:
        }
    }
}