package app;

import javax.swing.*;

public class AppBuilder {
    public static final int HEIGHT = 300;
    public static final int WIDTH = 400;

    public JFrame build() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("GeoLearn");
        frame.setSize(WIDTH, HEIGHT);

        return frame;
    }
}
