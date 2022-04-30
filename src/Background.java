import java.util.List;
import java.util.Optional;

import processing.core.PImage;

/**
 * Represents a background for the 2D world.
 */
public final class Background
{

    public String id;
    public List<PImage> images;
    public int imageIndex;


    public Background(String id, List<PImage> images) {
        this.id = id;
        this.images = images;
    }

    public static Optional<PImage> getBackgroundImage(
            WorldModel world, Point pos)
    {
        if (world.withinBounds(world, pos)) {
            return Optional.of(ImageStore.getCurrentImage(getBackgroundCell(world, pos)));
        }
        else {
            return Optional.empty();
        }
    }

    public void setBackground(
            WorldModel world, Point pos)
    {
        if (world.withinBounds(world, pos)) {
            world.setBackgroundCell(world, pos, this);
        }
    }

    private static Background getBackgroundCell(WorldModel world, Point pos) {
        return world.background[pos.y][pos.x];
    }
}
