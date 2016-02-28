/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.impl.card;

import java.awt.geom.AffineTransform;

/**
 *
 * @author Jirka
 */
public class CardGeometry {

    private int x;
    private int y;
    private double r;
    private AffineTransform cachedM;
    private boolean dirtyM = true;
    private AffineTransform cachedR;
    private boolean dirtyR = true;

    public CardGeometry(CardGeometry cardGeometry) {
        this.x = cardGeometry.x;
        this.y = cardGeometry.y;
        this.r = cardGeometry.r;
    }

    public CardGeometry(int x, int y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public String getSentence() {
        StringBuilder sb = new StringBuilder();

        sb.append(getX()).append(":").
                append(getY()).append(":").
                append(getR());
        return sb.toString();
    }

    public AffineTransform getArchRotatedMatrix(int i, int i0) {
        return getArchRotateMatrix((double) i, (double) i0);
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
        dirtyM = true;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
        dirtyM = true;
    }

    /**
     * @return the r
     */
    public double getR() {
        return r;
    }

    /**
     * @param r the r to set
     */
    public void setR(double r) {
        this.r = r;
        dirtyR = true;
    }

    public AffineTransform getMovementMatrix() {
        if (cachedM != null && dirtyM == false) {
            return cachedM;
        }
        cachedM = AffineTransform.getTranslateInstance((double) x, (double) y);
        return cachedM;
    }

//    private AffineTransform getRotateMatrix(){
//        //                                          -         -     ?
//        return AffineTransform.getRotateInstance(r,(double)x, (double)y);
//    }
    public AffineTransform getArchRotateMatrix(double xx, double yy) {
        if (cachedR != null && dirtyR == false) {
            return cachedR;
        }
        cachedR = AffineTransform.getRotateInstance(r, (double) x + xx, (double) y + yy);
        return cachedR;

    }
//    private AffineTransform getPureRotateMatrix(){
//        return AffineTransform.getRotateInstance(r);
//    }
}
