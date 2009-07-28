package edu.umd.cs.piccolo.examples;

import java.awt.geom.Point2D;

import javax.swing.JOptionPane;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PHtml;
import edu.umd.cs.piccolox.PFrame;

public class HTMLExample extends PFrame {
    private static final long serialVersionUID = 1L;
    private StringBuffer html;

    public HTMLExample() {
        this(null);
    }

    public HTMLExample(final PCanvas aCanvas) {
        super("HTMLExample", false, aCanvas);
    }

    public void initialize() {
        html = new StringBuffer();
        html.append("<p style='margin-bottom: 10px;'>");
        html.append("This is an example <a href='#testing' title=\"<a href='FIXME!'\">of what can</a> be done with PHtml.");
        html.append("</p>");
        html.append("<p>It supports:</p>");
        appendFeatures();

        final PHtml htmlNode = new PHtml(html.toString());
        htmlNode.setBounds(0, 0, 400, 400);
        getCanvas().getLayer().addChild(htmlNode);

        getCanvas().addInputEventListener(new PBasicInputEventHandler() {
            public void mouseClicked(final PInputEvent event) {
                final PNode clickedNode = event.getPickedNode();
                if (!(clickedNode instanceof PHtml)) {
                    return;
                }

                final Point2D clickPoint = event.getPositionRelativeTo(clickedNode);
                final PHtml htmlNode = (PHtml) clickedNode;

                final String url = htmlNode.getClickedAddress(clickPoint);
                JOptionPane.showMessageDialog(null, url);
            }
        });
    }

    private void appendFeatures() {
        html.append("<ul>");
        html.append("<li><b>HTML</b> 3.2</li>");
        html.append("<li><font style='color:red; font-style: italic;'>Limited CSS 1.0</font></li>");
        html.append("<li>Tables:");
        appendTable();
        html.append("</li>");
        html.append("</ul>");
    }

    private void appendTable() {
        html.append("<table border='1' cellpadding='2' cellspacing='0'>");
        html.append("<tr><th>Col 1</th><th>Col 2</th></tr>");
        html.append("<tr><td>Col 1 val</td><td>Col 2 val</td></tr>");
        html.append("</table>");
    }

    public static void main(final String[] args) {
        new HTMLExample();
    }
}
