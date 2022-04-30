import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

public final class WorldView
{
    public PApplet screen;
    public WorldModel world;
    public int tileWidth;
    public int tileHeight;
    public Viewport viewport;

    public WorldView(
            int numRows,
            int numCols,
            PApplet screen,
            WorldModel world,
            int tileWidth,
            int tileHeight)
    {
        this.screen = screen;
        this.world = world;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.viewport = new Viewport(numRows, numCols);
    }

    private void shift(Viewport viewport, int col, int row) {
        viewport.col = col;
        viewport.row = row;
    }

    private int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }

    public Point viewportToWorld(Viewport viewport, int col, int row) {
        return new Point(col + viewport.col, row + viewport.row);
    }

    public void shiftView(WorldView view, int colDelta, int rowDelta) {
        int newCol = clamp(view.viewport.col + colDelta, 0,
                view.world.numCols - view.viewport.numCols);
        int newRow = clamp(view.viewport.row + rowDelta, 0,
                view.world.numRows - view.viewport.numRows);

        shift(view.viewport, newCol, newRow);
    }

    private void drawBackground(WorldView view) {
        for (int row = 0; row < view.viewport.numRows; row++) {
            for (int col = 0; col < view.viewport.numCols; col++) {
                Point worldPoint = viewportToWorld(view.viewport, col, row);
                Optional<PImage> image =
                        Background.getBackgroundImage(view.world, worldPoint);
                if (image.isPresent()) {
                    view.screen.image(image.get(), col * view.tileWidth,
                            row * view.tileHeight);
                }
            }
        }
    }

    private void drawEntities(WorldView view) {
        for (Entity entity : view.world.entities) {
            Point pos = entity.position;

            if (contains(view.viewport, pos)) {
                Point viewPoint = this.viewport.worldToViewport(view.viewport, pos.x, pos.y);
                view.screen.image(ImageStore.getCurrentImage(entity),
                        viewPoint.x * view.tileWidth,
                        viewPoint.y * view.tileHeight);
            }
        }
    }

    public void drawViewport(WorldView view) {
        drawBackground(view);
        drawEntities(view);
    }

    private boolean contains(Viewport viewport, Point p) {
        return p.y >= viewport.row && p.y < viewport.row + viewport.numRows
                && p.x >= viewport.col && p.x < viewport.col + viewport.numCols;
    }
}
