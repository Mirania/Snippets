import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageSimilarity {

    // The size cap of the images.
    private static final int sizeCap = 300;

    // Proportions used for calcSignature.
    private static final float[] sigProps = new float[] { 1/10f, 3/10f, 5/10f, 7/10f, 9/10f };

    /**
     * Returns the difference between images as a numeric value.
     * The closer to 0 the value is, the more similar the images are.
     * A value of 0 means the images are equal.
     */
    public static double compare(final BufferedImage bx, final BufferedImage by) {
        final int scaledSize = getAppropriateScale(bx, by);
        final BufferedImage scaledX = rescale(bx, scaledSize, scaledSize, true);
        final BufferedImage scaledY = rescale(by, scaledSize, scaledSize, true);
        return calcDistance(scaledX, scaledY, scaledSize);
    }

    /**
     * Returns the difference between images as a numeric value.
     * The closer to 0 the value is, the more similar the images are.
     * A value of 0 means the images are equal.
     */
    public static double compare(final File fx, final File fy) throws IOException {
        return compare(ImageIO.read(fx), ImageIO.read(fy));
    }

    private static Color[][] calcSignature(final BufferedImage i, final int scaledSize) {
        // Get memory for the signature.
        final Color[][] sig = new Color[5][5];
        // For each of the 25 signature values average the pixels around it.
        // Note that the coordinate of the central pixel is in proportions.
        for (int x = 0; x < 5; x++)
            for (int y = 0; y < 5; y++)
                sig[x][y] = averageAround(i, scaledSize, sigProps[x], sigProps[y]);
        return sig;
    }

    private static double calcDistance(final BufferedImage bx, final BufferedImage by, final int scaledSize) {
        // Calculate the signature for the images.
        final Color[][] sigX = calcSignature(bx, scaledSize);
        final Color[][] sigY = calcSignature(by, scaledSize);
        // There are several ways to calculate distances between two vectors,
        // we will calculate the sum of the distances between the RGB values of
        // pixels in the same positions.
        double dist = 0;
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                int r1 = sigX[x][y].getRed();
                int g1 = sigX[x][y].getGreen();
                int b1 = sigX[x][y].getBlue();
                int r2 = sigY[x][y].getRed();
                int g2 = sigY[x][y].getGreen();
                int b2 = sigY[x][y].getBlue();
                dist += Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2));
            }
        }
        return dist;
    }

    private static Color averageAround(final BufferedImage i, final int scaledSize, final double px, final double py) {
        // Get memory for a pixel and for the accumulator.
        final double[] accumulator = new double[3];
        double[] pixel;
        // The size of the sampling area.
        final double sampleSize = 0.09 * scaledSize;
        int numPixels = 0;
        // Sample the pixels.
        for (double x = px * scaledSize - sampleSize; x < px * scaledSize + sampleSize; x++) {
            for (double y = py * scaledSize - sampleSize; y < py * scaledSize + sampleSize; y++) {
                final Color pix = new Color(i.getRGB((int) x, (int) y));
                pixel = new double[] { pix.getRed(), pix.getGreen(), pix.getBlue() };
                accumulator[0] += pixel[0];
                accumulator[1] += pixel[1];
                accumulator[2] += pixel[2];
                numPixels++;
            }
        }
        // Average the accumulated values.
        accumulator[0] /= numPixels;
        accumulator[1] /= numPixels;
        accumulator[2] /= numPixels;
        return new Color((int) accumulator[0], (int) accumulator[1], (int) accumulator[2]);
    }

    private static int getAppropriateScale(final BufferedImage bx, final BufferedImage by) {
        int r = bx.getWidth();
        if (r > sizeCap) r = sizeCap;
        if (r > bx.getHeight()) r = bx.getHeight();
        if (r > by.getWidth()) r = by.getWidth();
        if (r > by.getHeight()) r = by.getHeight();
        return r;
    }

    private static BufferedImage rescale(final BufferedImage img, final int targetWidth,
                                         final int targetHeight, final boolean higherQuality) {
        final int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        int w, h;

        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w < targetWidth) {
                w *= 1.2;
            }

            if (higherQuality && h < targetHeight) {
                h *= 1.2;
            }

            if (higherQuality && w > targetWidth) {
                w /= 2.5;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2.5;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            final BufferedImage tmp = new BufferedImage(w, h, type);
            final Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
}
