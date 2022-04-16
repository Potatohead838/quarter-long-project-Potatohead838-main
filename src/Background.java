import java.util.List;
import java.util.Optional;

import processing.core.PImage;

/**
 * Represents a background for the 2D world.
 */
public final class Background
{

    public static final int BGND_NUM_PROPERTIES = 4;
    public static final int BGND_ID = 1;
    public static final int BGND_COL = 2;
    public static final int BGND_ROW = 3;

    public String id;
    public List<PImage> images;
    public int imageIndex;



    public Background(String id, List<PImage> images) {
        this.id = id;
        this.images = images;
    }

    public static boolean parseBackground(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                    Integer.parseInt(properties[BGND_ROW]));
            String id = properties[BGND_ID];
            setBackground(world, pt,
                    new Background(id, ImageStore.getImageList(imageStore, id)));
        }

        return properties.length == BGND_NUM_PROPERTIES;
    }

    public static Optional<PImage> getBackgroundImage(
            WorldModel world, Point pos)
    {
        if (WorldModel.withinBounds(world, pos)) {
            return Optional.of(ImageStore.getCurrentImage(WorldModel.getBackgroundCell(world, pos)));
        }
        else {
            return Optional.empty();
        }
    }

    public static void setBackground(
            WorldModel world, Point pos, Background background)
    {
        if (WorldModel.withinBounds(world, pos)) {
            WorldModel.setBackgroundCell(world, pos, background);
        }
    }
}