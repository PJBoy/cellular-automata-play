import javax.swing.*;
import java.awt.event.*;

// A play program, displaying the 1d cellular automata rules in the reflected binary code sequence.
class Play implements Runnable
{
    private JFrame w = null;
    private Ca ca = new Ca();

    // Create a program object, arrange for the graphics thread to call its run
    // method when ready, and then use the main thread to run the animation.
    public static void main(String[] args)
    {
        Play program = new Play();
        SwingUtilities.invokeLater(program);
        program.animate();
    }

    // Create the main window (called by the graphics thread)
    public void run()
    {
        w = new JFrame();
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        w.add(ca);
        w.pack();
        w.setLocationByPlatform(true);
        w.setVisible(true);
        // Listen for window resizes to call the CA's resize
        w.addComponentListener(new ComponentAdapter(){public void componentResized(ComponentEvent e){ca.resize(e);}});
    }

    // Animate the CA, updating it 10 times per second (called by the main thread)
    void animate()
    {
        // Loops forever (until program termination)
        while (true)
        {
            try {Thread.sleep(10);}
            catch (InterruptedException interruption){}
            ca.update();
            // Set title to the current rule
            if (w != null)
                w.setTitle("Rule " + Integer.toString(ca.rule>>1 ^ ca.rule));
            ca.repaint();
        }
    }
}
