package common;

import java.awt.*;

public class Watermark {
    public boolean fillground;
    public float alpha = 1.0f;
    public Point pos = new Point(20, 40);
    public String text = "hello";
    public Color col = Color.BLACK;
    public Font font = new Font("Monospaced", Font.PLAIN, 36);

    @Override
    public String toString() {
        return "Marker{" +
                "alpha=" + alpha +
                ", pos=" + pos +
                ", text='" + text + '\'' +
                ", col=" + col +
                ", font=" + font +
                '}';
    }
}
