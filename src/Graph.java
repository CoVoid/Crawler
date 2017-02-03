import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.lang.String.valueOf;

/**
 * Created by Stephen on 11/10/2016.
 */
public class Graph extends JPanel {

    private static final int MIN_BAR_WIDTH = 4;
    SortedMap<Integer, Integer> sortMap;

    public Graph(Map<Integer, Integer> wordCounts) {
        this.sortMap = new TreeMap(wordCounts);
        int width = (sortMap.size() * MIN_BAR_WIDTH) + 500;
        Dimension minSize = new Dimension(width, 256);
        Dimension prefSize = new Dimension(width, 256);
        setMinimumSize(minSize);
        setPreferredSize(prefSize);
    }

    //Draws the histogram in the JFrame
    //paintComponent is overwritten to allow the JFrame to be resized dynamically
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sortMap != null) {
            //JComponent Border Dimensions
            int xOffset = 20;
            int yOffset = 35;
            int width = getWidth() - 1 - (xOffset * 2);
            int height = getHeight() - 1 - (yOffset * 2);

            //Create Graphics object
            Graphics2D graphics = (Graphics2D) g.create();

            //Draw Background
            graphics.setColor(Color.DARK_GRAY);
            graphics.drawRect(xOffset, yOffset, width, height);

            int barWidth = Math.max(MIN_BAR_WIDTH, (int) Math.floor((float) width / (float) sortMap.size()));

            //Determine the width of the bars
            int maxValue = 0;
            for (Integer key: sortMap.keySet()){
                int value = sortMap.get(key);
                maxValue = Math.max(maxValue, value);
            }
            int xPos = xOffset;
            for (Integer key: sortMap.keySet()){
                int value = sortMap.get(key);
                int barHeight = Math.round(((float) value / (float) maxValue) * height);
                int yPos = height + yOffset - barHeight;
                Rectangle2D bar = new Rectangle2D.Float(xPos, yPos, barWidth, barHeight);
                graphics.setColor(Color.BLUE);
                graphics.fill(bar);
                graphics.setColor(Color.DARK_GRAY);
                graphics.drawString(valueOf(value), (xPos + (barWidth/2)), yPos - 5);
                graphics.drawString(valueOf(key), (xPos + (barWidth)/2), height + yOffset + 15);
                graphics.draw(bar);
                xPos+= barWidth;
            }

            //draw labels
            drawRotate(graphics, 15.0, getHeight()  / 2, -90, "Frequency");
            graphics.drawString("Word Count", (getWidth() / 2) - 30, getHeight() - 5);
            graphics.dispose();
        }
    }

    //Helper method for drawing labels on histogram
    public static void drawRotate(Graphics2D g, double x, double y, int angle, String text){
        g.translate((float) x, (float) y);
        g.rotate(Math.toRadians(angle));
        g.drawString(text, 0,0);
        g.rotate(-Math.toRadians(angle));
        g.translate(-(float) x, -(float) y);
    }
}