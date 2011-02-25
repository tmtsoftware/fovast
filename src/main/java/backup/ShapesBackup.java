/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package backup;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 *
 * @author vivekananda_moosani
 */
public class ShapesBackup {

    /**
     * TODO: Trying to make a something like LaTeX \oplus .. but the outplut not really
     * smooth yet
     *
     * @param centerPixel
     * @param halfSize
     * @return
     */
    private GeneralPath makeTargetShape(Point2D.Double centerPixel, double halfSize) {
        Point2D.Double north = new Point2D.Double(centerPixel.x, centerPixel.y - halfSize);
        Point2D.Double south = new Point2D.Double(centerPixel.x, centerPixel.y + halfSize);
        Point2D.Double east = new Point2D.Double(centerPixel.x + halfSize*2, centerPixel.y);
        Point2D.Double west = new Point2D.Double(centerPixel.x - halfSize*2, centerPixel.y);
        Point2D.Double eastA = new Point2D.Double(centerPixel.x + halfSize*4, centerPixel.y);
        Point2D.Double westA = new Point2D.Double(centerPixel.x - halfSize*4, centerPixel.y);

        // XXX Note: This is not an ellipse. What we really want is a "smooth polygon"...
        GeneralPath p = new GeneralPath();
        p.moveTo((float) north.x, (float) north.y);
        p.quadTo((float) westA.x, (float) westA.y, (float) south.x, (float) south.y);
        p.quadTo((float) eastA.x, (float) eastA.y, (float) north.x, (float) north.y);
        p.lineTo(south.x, south.y);
        p.moveTo(west.x, west.y);
        p.lineTo(east.x, east.y);
        //p.closePath();
        return p;
    }


}
