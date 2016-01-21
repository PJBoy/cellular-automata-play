import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.BitSet;

// A drawing of a CA, with stages of progression being drawn line-by-line
// Default size is 63 columns of 31 rows, but will adapt to window resizes
class Ca extends JPanel
{
    // Each stage of progression is stored alternately in row
    // The current row to draw is held in rowIndex
    private BitSet row[] = {new BitSet(63), new BitSet(63)};
    private int rowIndex = 0, y = 0, width = 63, height = 31, newWidth = 63;
    private boolean clear = true, update = false, dummyPaint = true;
    int rule = 0;

    // Create the CA with default size
    Ca()
    {
        // Set middle column
        row[0].set(width/2);
        setPreferredSize(new Dimension(630, 310));
    }

    // Respond to request to move the animation on by one step
    void update()
    {
        // Set by graphics thread (so frames aren't skipped)
        if (update)
        {
            // Painted all of window, reset, set new width (if any), next rule
            if (y++ == height)
            {
                rule = (rule+1) % 256;
                width = newWidth;
                row[0].clear();
                row[0].set(width/2);
                row[1].clear();
                rowIndex = 0;
                y = 0;
                clear = true;
            }
            // Reflected binary code
            int rule_g = rule>>1 ^ rule;
            // First and last columns are special cases where only 2 bits of information can be retrieved from the row,
            // the sides of the window are treated as 0's
            // get returns a zero-length BitSet when no bits are set (unfortunately)
            try
            {
                row[1-rowIndex].set(0, (rule_g >> (row[rowIndex].get(0, 2).toByteArray()[0] & 3) & 1) == 1 ? true : false);
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                row[1-rowIndex].set(0, (rule_g & 1) == 1 ? true : false);
            }
            for (int i = 1; i < width-1; ++i)
            {
                try
                {
                    row[1-rowIndex].set(i, (rule_g >> (row[rowIndex].get(i-1, i+2).toByteArray()[0] & 7) & 1) == 1 ? true : false);
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    row[1-rowIndex].set(i, (rule_g & 1) == 1 ? true : false);
                }
            }
            try
            {
                row[1-rowIndex].set(width-1, (rule_g >> (row[rowIndex].get(61, 63).toByteArray()[0] & 3) & 1) == 1 ? true : false);
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                row[1-rowIndex].set(width-1, (rule_g & 1) == 1 ? true : false);
            }
            row[rowIndex].clear();
            rowIndex = 1 - rowIndex;
            update = false;
        }
    }

    // Draw the CA (called by the graphics thread, whenever it needs to, in
    // response to a repaint() request).
    public void paintComponent(Graphics g)
    {
        // The first time this function is called, nothing is drawn (for whatever reason)
        if (dummyPaint)
        {
            dummyPaint = false;
            return;
        }
        // Set when the last row has been drawn
        if (clear)
        {
            super.paintComponent(g);
            clear = false;
        }
        // Each bit in the row will represent a 10x10 square where:
        // 0 is the background (black) and
        // 1 is the foreground (red)
        // The motivation for not using a transparent background is rules such as 0 and 32
        for (int x = 0; x < width; ++x)
        {
            if (row[rowIndex].get(x))
                g.setColor(Color.RED);
            else
                g.setColor(Color.BLACK);
            g.fillRect(x * 10, y * 10, 10, 10);
        }
        update = true;
    }

    public void resize(ComponentEvent e)
    {
        newWidth = e.getComponent().getWidth()/10;
        height = e.getComponent().getHeight()/10;
    }
}
