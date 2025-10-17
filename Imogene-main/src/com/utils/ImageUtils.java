package com.utils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class ImageUtils {

    public static BitMapImage invert(BitMapImage image) {
        int[][][] rgb = image.getRgb();
        int[][][] rgbOut = new int[rgb.length][rgb[0].length][3];
        for(int y = 0; y  < image.getHeight(); y++)
            for(int x = 0; x < image.getWidth(); x++)
                for(int c = 0; c < 3; c++)
                    rgbOut[y][x][c] = 255 - rgb[y][x][c];
        return new BitMapImage(rgbOut);
    }

    public static BitMapImage spectralProjection(BitMapImage image, String source, String target) {
        System.out.println("Called with " + source + " and " + target);
        int[][][] rgbSource = image.getRgb();
        int[][][] rgbTarget = image.getRgb();

        // TODO: find a better way to parametrise these
        double[][] sourceMetrics = new double[image.getHeight()][image.getWidth()];
        if(source.equalsIgnoreCase("Hue")) {
            sourceMetrics = hue(rgbSource);
        }
        else if(source.equalsIgnoreCase("Saturation")) {
            sourceMetrics = saturation(rgbSource);
        }
        else if(source.equalsIgnoreCase("Lightness")) {
            sourceMetrics = lightness(rgbSource);
        }
        else if(source.equalsIgnoreCase("Red")) {
            sourceMetrics = red(rgbSource);
        }
        else if(source.equalsIgnoreCase("Green")) {
            sourceMetrics = green(rgbSource);
        }
        else if(source.equalsIgnoreCase("Blue")) {
            sourceMetrics = blue(rgbSource);
        }

        System.out.println(Arrays.deepToString(sourceMetrics));

        if(target.equalsIgnoreCase("Hue")) {
            rgbTarget = projectOntoHue(sourceMetrics);
        }
        else if(target.equalsIgnoreCase("Saturation")) {
            rgbTarget = projectOntoSaturation(sourceMetrics);
        }
        else if(target.equalsIgnoreCase("Lightness")) {
            rgbTarget = projectOntoLightness(sourceMetrics);
        }
        else if(target.equalsIgnoreCase("Red")) {
            rgbTarget = projectOntoRed(sourceMetrics);
        }
        else if(target.equalsIgnoreCase("Green")) {
            rgbTarget = projectOntoGreen(sourceMetrics);
        }
        else if(target.equalsIgnoreCase("Blue")) {
            rgbTarget = projectOntoBlue(sourceMetrics);
        }

        return new BitMapImage(rgbTarget);
    }

    public static int[][][] projectOntoRed(double[][] values) {
        return projectOntoColour(values, 255, 0, 0);
    }

    public static int[][][] projectOntoGreen(double[][] values) {
        return projectOntoColour(values, 0, 255, 0);
    }

    public static int[][][] projectOntoBlue(double[][] values) {
        return projectOntoColour(values, 0, 0, 255);
    }

    // TODO: make this one universal, provide an array of colours for the spectrum gradient
    public static int[][][] projectOntoColour(double[][] values, double r, double g, double b) {
        double max = max(values);
        int[][][] rgb = new int[values.length][values[0].length][3];
        for(int y = 0; y < values.length; y++) {
            for(int x = 0; x < values[0].length; x++) {
                rgb[y][x][0] = (int) Math.round(r * values[y][x] / max);
                rgb[y][x][1] = (int) Math.round(g * values[y][x] / max);
                rgb[y][x][2] = (int) Math.round(b * values[y][x] / max);
            }
        }
        return rgb;
    }

    public static int[][][] projectOntoHue(double[][] values) {
        double max = max(values);
        System.out.println("Max = " + max);
        int[][][] rgb = new int[values.length][values[0].length][3];
        for(int y = 0; y < values.length; y++) {
            for(int x = 0; x < values[0].length; x++) {
                double proportion = values[y][x] / max;
                if(proportion < 0.25) {
                    int r = 255;
                    int g = (int) Math.round(255 * proportion * 2.0);
                    int b = 0;
                    rgb[y][x][0] = r;
                    rgb[y][x][1] = g;
                    rgb[y][x][2] = b;
                }
                else if(proportion < 0.5) {
                    int g = 255;
                    int r = (int) Math.round(255 * (proportion - 0.25) * 2.0);
                    int b = 0;
                    rgb[y][x][0] = r;
                    rgb[y][x][1] = g;
                    rgb[y][x][2] = b;
                }
                else if(proportion < 0.75) {
                    int g = 255;
                    int b = (int) Math.round(255 * (proportion - 0.5) * 2.0);
                    int r = 0;
                    rgb[y][x][0] = r;
                    rgb[y][x][1] = g;
                    rgb[y][x][2] = b;
                }
                else {
                    int b = 255;
                    int g = (int) Math.round(255 * (proportion - 0.75) * 2.0);
                    int r = 0;
                    rgb[y][x][0] = r;
                    rgb[y][x][1] = g;
                    rgb[y][x][2] = b;
                }
            }
        }
        return rgb;
    }

    // TODO: currently projects onto red, make parametric
    public static int[][][] projectOntoSaturation(double[][] values) {
        double max = max(values);
        System.out.println("Max = " + max);
        int[][][] rgb = new int[values.length][values[0].length][3];
        for(int y = 0; y < values.length; y++) {
            for(int x = 0; x < values[0].length; x++) {
                rgb[y][x] = rgbFromHSL(0, values[y][x] / max, 0.5);
            }
        }
        return rgb;
    }

    public static int[][][] projectOntoLightness(double[][] values) {
        double max = max(values);
        System.out.println("Max = " + max);
        int[][][] rgb = new int[values.length][values[0].length][3];
        for(int y = 0; y < values.length; y++) {
            for(int x = 0; x < values[0].length; x++) {
                rgb[y][x][0] = (int) Math.round(255.0 * values[y][x] / max);
                rgb[y][x][1] = (int) Math.round(255.0 * values[y][x] / max);
                rgb[y][x][2] = (int) Math.round(255.0 * values[y][x] / max);
            }
        }
        return rgb;
    }

    public static double[][] hue(int[][][] rgb) {
        double[][] out = new double[rgb.length][rgb[0].length];
        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                out[y][x] = hue(rgb[y][x][0], rgb[y][x][1], rgb[y][x][2]);
            }
        }
        return out;
    }

    public static double[][] saturation(int[][][] rgb) {
        double[][] out = new double[rgb.length][rgb[0].length];
        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                out[y][x] = saturation(rgb[y][x][0], rgb[y][x][1], rgb[y][x][2]);
            }
        }
        return out;
    }

    public static double[][] lightness(int[][][] rgb) {
        double[][] out = new double[rgb.length][rgb[0].length];
        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                out[y][x] = lightness(rgb[y][x][0], rgb[y][x][1], rgb[y][x][2]);
            }
        }
        return out;
    }

    public static double[][] red(int[][][] rgb) {
        double[][] out = new double[rgb.length][rgb[0].length];
        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                out[y][x] = 0.0 + rgb[y][x][0];
            }
        }
        return out;
    }

    public static double[][] green(int[][][] rgb) {
        double[][] out = new double[rgb.length][rgb[0].length];
        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                out[y][x] = 0.0 + rgb[y][x][1];
            }
        }
        return out;
    }

    public static double[][] blue(int[][][] rgb) {
        double[][] out = new double[rgb.length][rgb[0].length];
        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                out[y][x] = 0.0 + rgb[y][x][2];
            }
        }
        return out;
    }

    public static double max(double[][] arr) {
        double max = Double.MIN_VALUE;
        for(int y =  0; y < arr.length; y++) {
            for(int x = 0; x < arr[0].length; x++) {
                max = Math.max(max, arr[y][x]);
            }
        }
        return max;
    }

    public static double meanHue(double hue1, double hue2) {
        double mean = (hue1 + hue2) / 2.0;
        double opposite = (mean + 180.0) % 360.0;
        double diff1 = mean - Math.min(hue1, hue2);
        double diff2;
        if(opposite > 0)
            diff2 = Math.min(hue1, hue2) - opposite;
        else
            diff2 = opposite - Math.max(hue1, hue2);
        if(diff1 < diff2)
            return mean;
        else
            return opposite;
    }

    public static int[] rgbFromHSL(double hue, double saturation, double lightness) {
        if(saturation == 0.0) {
            int lightnessInt = (int) Math.round(lightness);
            return new int[]{lightnessInt, lightnessInt, lightnessInt};
        }
        double q = 0.0;
        if(lightness < 0.5) {
            q = lightness * (1.0 + saturation);
        }
        else {
            q = lightness + saturation - lightness * saturation;
        }
        double p = 2.0 * lightness - q;
        int r = (int) Math.floor(255.0 * hueToRgb(p, q, hue + 1.0 / 3.0));
        int g = (int) Math.floor(255.0 * hueToRgb(p, q, hue));
        int b = (int) Math.floor(255.0 * hueToRgb(p, q, hue - 1.0 / 3.0));
        return new int[] {r, g, b};
    }

    private static double hueToRgb(double p, double q, double t) {
        if(t < 0.0)
            t += 1.0;
        if(t > 1.0)
            t -= 1.0;
        if(t < 1.0 / 6.0)
            return p + (q - p) * 6.0 * t;
        if (t < 1.0 / 2.0)
            return q;
        if (t < 2.0 / 3.0)
            return p + (q - p) * (2.0 / 3.0 - t) * 6.0;
        return p;
    }

//    public static int[] rgbFromHSL(double hue, double saturation, double lightness) {
//        double chroma = (1 - Math.abs(2.0 * lightness - 1.0)) * saturation;
//        double hPrime = hue / 60.0;
//        double x = chroma * (1.0 - Math.abs((hPrime % 2.0) - 1.0));
//        double r1, g1, b1;
//        if(hPrime < 1.0) {
//            r1 = chroma;
//            g1 = x;
//            b1 = 0.0;
//        }
//        else if(hPrime < 2.0) {
//            r1 = x;
//            g1 = chroma;
//            b1 = 0.0;
//        }
//        else if(hPrime < 3.0) {
//            r1 = 0.0;
//            g1 = chroma;
//            b1 = x;
//        }
//        else if(hPrime < 4.0) {
//            r1 = 0.0;
//            g1 = x;
//            b1 = chroma;
//        }
//        else if(hPrime < 5.0) {
//            r1 = x;
//            g1 = 0.0;
//            b1 = chroma;
//        }
//        else {
//            r1 = chroma;
//            g1 = 0;
//            b1 = x;
//        }
//        double m = lightness - chroma / 2.0;
//        int r = (int) Math.round(r1 + m);
//        int g = (int) Math.round(g1 + m);
//        int b = (int) Math.round(b1 + m);
//        return new int[] {r, g, b};
//    }

    /**
     * Calculates the lightness of an rgb colour, scales from 0.0 to 1.0
     *
     * @param r
     * @param g
     * @param b
     * @return Normalised lightness of the colour
     */
    public static double lightness(int r, int g, int b) {
        double rd = r / 255.0;
        double gd = g / 255.0;
        double bd = b / 255.0;
        double max = Math.max(rd, Math.max(gd, bd));
        double min = Math.min(rd, Math.min(gd, bd));
        double lightness = (max + min) / 2.0;
        return lightness;
    }

    public static double saturation(int r, int g, int b) {
        double rd = r / 255.0;
        double gd = g / 255.0;
        double bd = b / 255.0;
        double max = Math.max(rd, Math.max(gd, bd));
        double min = Math.min(rd, Math.min(gd, bd));
        double lightness = (max + min) / 2.0;
        //lightness = lightness / 255.0;
        double delta = max - min;
        if(delta == 0)
            return 0.0;
        return delta / (1.0 - Math.abs(2.0 * lightness - 1.0));
    }

    public static double hue(int r, int g, int b) {
        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        double lightness = (0.0 + max + min) / 2.0;
        double delta = 0.0 + max - min;
        if(delta == 0)
            return 0.0;
        double hue;
        if(max == r)
            hue = ((0.0 + g - b) / delta) % 6.0;
        else if(max == g)
            hue = ((0.0 + b - r) / delta) + 2.0;
        else
            hue = ((0.0 + r - g) / delta) + 4.0;
        hue = Math.round(hue * 60.0);
        if(hue < 0.0)
            hue += 360.0;
        return hue / 360.0;
    }

    // TODO: parametrise these
    static double centerWeight = 0.2;
    static double edgeWeight = 0.1;

    public static BitMapImage smoothFilter(BitMapImage image) {
        return smoothFilter(image, 0.6, 0.05);
    }

    public static BitMapImage smoothFilter(BitMapImage image, double centerWeight, double edgeWeight) {
        int[][][] rgb = image.getRgb();
        int[][][] adjusted = new int[image.getHeight()][image.getWidth()][3];
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                for(int c = 0; c < 3; c++) {
                    double weightSum = 0.0;
                    double weightedColourSum = 0.0;
                    for(int i = Math.max(0, y - 1); i <= Math.min(image.getHeight() - 1, y + 1); i++) {
                        for(int j = Math.max(0, x - 1); j <= Math.min(image.getWidth() - 1, x + 1); j++) {
                            if(y == i && x == j) {
                                weightSum += centerWeight;
                                weightedColourSum += centerWeight * rgb[y][x][c];
                            }
                            else {
                                weightSum += edgeWeight;
                                weightedColourSum += edgeWeight * rgb[i][j][c];
                            }
                        }
                    }
                    int newValue = (int) Math.round(weightedColourSum / weightSum);
                    adjusted[y][x][c] = newValue;
                }
            }
        }
        return new BitMapImage(adjusted);
    }


//    public static BitMapImage adjustBrightness(BitMapImage image, double brightness) {
//        int[][][] rgb = image.getRgb();
//        int[][][] adjusted = new int[image.getHeight()][image.getWidth()][3];
//
//        for (int y = 0; y < image.getHeight(); y++) {
//            for (int x = 0; x < image.getWidth(); x++) {
//                for (int c = 0; c < 3; c++) {
//                    int adjustedValue = (int) Math.round(rgb[y][x][c] * (1.0 - brightness) + 255 * brightness);
//                    if (adjustedValue < 0) adjustedValue = 0;
//                    if (adjustedValue > 255) adjustedValue = 255;
//
//                    adjusted[y][x][c] = adjustedValue;
//                }
//            }
//        }
//
//        return new BitMapImage(adjusted);
//    }

    // Assumes the input image is grayscale
    public static BitMapImage spectrumMaping(BitMapImage image, int[][] colours) {
        int[][][] rgb = image.getRgb();
        int[][][] adjusted = new int[image.getHeight()][image.getWidth()][3];
        int[] thresholds = new int[colours.length];

        int lowestValue = 255;
        int highestValue = 0;
        for(int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int value = rgb[y][x][0]; // Assumes green and blue are same values as red
                if(value < lowestValue) {
                    lowestValue = value;
                }
                if(value > highestValue) {
                    highestValue = value;
                }
            }
        }

        for(int t = 0; t < thresholds.length; t++) {
            thresholds[t] = lowestValue + t * (highestValue - lowestValue) / (colours.length - 1);
        }

        System.out.println("thresholds: " + Arrays.toString(thresholds));

        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                int value = rgb[y][x][0]; // Assumes green and blue are same values as red
                for(int t = 0; t < thresholds.length; t++) {
                    if(value > thresholds[t])
                        continue;
                    if(value == thresholds[t]) {
                        adjusted[y][x][0] = colours[t][0];
                        adjusted[y][x][1] = colours[t][1];
                        adjusted[y][x][2] = colours[t][2];
                        break;
                    }
                    int t1 = thresholds[t - 1];
                    int t2 = thresholds[t];
                    int[] colour1 = colours[t - 1];
                    int[] colour2 = colours[t];
                    double distance1 = Math.abs(t1 - value);
                    double distance2 = Math.abs(t2 - value);
                    double ratio1 = distance2 / (distance1 + distance2);
                    double ratio2 = distance1 / (distance1 + distance2);
                    //System.out.println(ratio1 + " " + ratio2);
                    for(int c = 0; c < 3; c++) {
                        adjusted[y][x][c] = (int) Math.round((0.0 + colour1[c]) * ratio1 + (0.0 + colour2[c]) * ratio2);
                    }
                    break;
                }
            }
        }
        return new BitMapImage(adjusted);
    }

    public static BitMapImage rgbBalancing(BitMapImage image, double rWeight, double gWeight, double bWeight) {
        int[][][] rgb = image.getRgb();
        int[][][] adjusted = new int[image.getHeight()][image.getWidth()][3];
        double[] rgbWeights = new double[] {rWeight, gWeight, bWeight};
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                for(int c = 0; c < 3; c++) {
                    adjusted[y][x][c] = (int) Math.round(rgb[y][x][c] * rgbWeights[c]);
                    if(adjusted[y][x][c] < 0) adjusted[y][x][c] = 0;
                    if(adjusted[y][x][c] > 255) adjusted[y][x][c] = 255;
                }
            }
        }
        return new BitMapImage(adjusted);
    }

    public static BitMapImage weightedGrayscale(BitMapImage image, double rWeight, double gWeight, double bWeight) {
        int[][][] rgb = image.getRgb();
        int[][][] gray = new int[image.getHeight()][image.getWidth()][3];
        double[] rgbWeights = new double[] {rWeight, gWeight, bWeight};
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                double total = 0;
                for(int c = 0; c < 3; c++) {
                    total += rgb[y][x][c] * rgbWeights[c];
                }
                double average = total / (rWeight + gWeight + bWeight);
                for(int c = 0; c < 3; c++) {
                    gray[y][x][c] = (int) Math.round(average);
                }
            }
        }
        return new BitMapImage(gray);
    }

    public static BitMapImage grayscale(BitMapImage image) {
        int[][][] rgb = image.getRgb();
        int[][][] gray = new int[image.getHeight()][image.getWidth()][3];
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                int total = 0;
                for(int c = 0; c < 3; c++) {
                    total += rgb[y][x][c];
                }
                int average = total / 3;
                for(int c = 0; c < 3; c++) {
                    gray[y][x][c] = average;
                }
            }
        }
        return new BitMapImage(gray);
    }

    /**
     * TODO: Untested AI-generated implementation
     */
    public static BitMapImage resize(BitMapImage image, int newHeight, int newWidth) {
        int oldHeight = image.getHeight();
        int oldWidth = image.getWidth();
        int[][][] rgb = image.getRgb();

        int[][][] output = new int[newHeight][newWidth][3];

        double scaleY = (double) oldHeight / newHeight;
        double scaleX = (double) oldWidth / newWidth;

        for (int y = 0; y < newHeight; y++) {
            double srcY = y * scaleY;
            int y0 = (int) Math.floor(srcY);
            int y1 = Math.min(y0 + 1, oldHeight - 1);
            double wy = srcY - y0;

            for (int x = 0; x < newWidth; x++) {
                double srcX = x * scaleX;
                int x0 = (int) Math.floor(srcX);
                int x1 = Math.min(x0 + 1, oldWidth - 1);
                double wx = srcX - x0;

                // For each channel: R, G, B
                for (int c = 0; c < 3; c++) {
                    int topLeft     = rgb[y0][x0][c];
                    int topRight    = rgb[y0][x1][c];
                    int bottomLeft  = rgb[y1][x0][c];
                    int bottomRight = rgb[y1][x1][c];

                    // Bilinear interpolation
                    double top    = topLeft * (1 - wx) + topRight * wx;
                    double bottom = bottomLeft * (1 - wx) + bottomRight * wx;
                    double value  = top * (1 - wy) + bottom * wy;

                    output[y][x][c] = (int) Math.round(value);
                }
            }
        }

        return new BitMapImage(output);
    }


    public static BitMapImage blendImages(BitMapImage image1, BitMapImage image2) {
        int[][][] rgb1 = image1.getRgb();
        int[][][] rgb2 = image2.getRgb();
        int[][][] rgb = new int[image1.getRgb().length][image1.getRgb()[0].length][3];

        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                rgb[y][x][0] = (rgb1[y][x][0] + rgb2[y][x][0]) / 2;
                rgb[y][x][1] = (rgb1[y][x][1] + rgb2[y][x][1]) / 2;
                rgb[y][x][2] = (rgb1[y][x][2] + rgb2[y][x][2]) / 2;
            }
        }

        return new BitMapImage(rgb);
    }

    public static BitMapImage blendImagesWeighted(BitMapImage image1, BitMapImage image2, double weight1, double weight2) {
        int[][][] rgb1 = image1.getRgb();
        int[][][] rgb2 = image2.getRgb();
        int[][][] rgb = new int[image1.getRgb().length][image1.getRgb()[0].length][3];

        double proportion1 = weight1 / (weight1 + weight2);
        double proportion2 = weight2 / (weight1 + weight2);

        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                Double r = ((0.0 + rgb1[y][x][0]) * proportion1) + ((0.0 + rgb2[y][x][0])  * proportion2);
                Double g = ((0.0 + rgb1[y][x][1]) * proportion1) + ((0.0 + rgb2[y][x][1])  * proportion2);
                Double b = ((0.0 + rgb1[y][x][2]) * proportion1) + ((0.0 + rgb2[y][x][2])  * proportion2);
                rgb[y][x][0] = r.intValue();
                rgb[y][x][1] = g.intValue();
                rgb[y][x][2] = b.intValue();
            }
        }

        return new BitMapImage(rgb);
    }

    public static BitMapImage rotateCW(BitMapImage image) {
        int[][][] rgb = new int[image.getWidth()][image.getHeight()][3];
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                rgb[x][image.getHeight() - y - 1][0] = image.getRgb()[y][x][0];
                rgb[x][image.getHeight() - y - 1][1] = image.getRgb()[y][x][1];
                rgb[x][image.getHeight() - y - 1][2] = image.getRgb()[y][x][2];
            }
        }
        return new BitMapImage(rgb);
    }

    public static BitMapImage rotateCCW(BitMapImage image) {
        int[][][] rgb = new int[image.getWidth()][image.getHeight()][3];
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                rgb[x][y][0] = image.getRgb()[y][x][0];
                rgb[x][y][1] = image.getRgb()[y][x][1];
                rgb[x][y][2] = image.getRgb()[y][x][2];
            }
        }
        return new BitMapImage(rgb);
    }

    public static BitMapImage paintCan(BitMapImage image, int x, int y, int r, int  g, int b) {
        int[][][] rgb = image.getRgb();
        int height = image.getHeight();
        int width = image.getWidth();
        boolean[][] searched = new boolean[image.getHeight()][image.getWidth()];
        searched[y][x] = true;
        ArrayList<Integer> xSet = new ArrayList<Integer>();
        ArrayList<Integer> ySet = new ArrayList<Integer>();
        // Add the coordinate of the first pixel
        xSet.add(x);
        ySet.add(y);
        // Repeatedly check pixels until no more pixels are searchable

        for(int nextSearchIndex = 0; nextSearchIndex < xSet.size(); nextSearchIndex++) {
            // Extract the coordinates of the next searchable pixel
            int xSearch = xSet.get(nextSearchIndex);
            int ySearch = ySet.get(nextSearchIndex);
            //System.out.println("Searching coordinate " + xSearch + ", " + ySearch);
            // Search the pixel to the left of the current pixel
            if (xSearch > 0) {
                // Skip it if it has already been searched
                if (!searched[ySearch][xSearch - 1])
                    // Skip  it if it's the wrong colour
                    if (rgb[ySearch][xSearch - 1][0] == rgb[y][x][0])
                        if (rgb[ySearch][xSearch - 1][1] == rgb[y][x][1])
                            if (rgb[ySearch][xSearch - 1][2] == rgb[y][x][2]) {
                                // If it's an unexplored pixel of the right colour, add it to the search set
                                xSet.add(xSearch - 1);
                                ySet.add(ySearch);
                                searched[ySearch][xSearch - 1] = true;
                            }
            }
            // Search the pixel to the right of the current pixel
            if (xSearch < width - 1) {
                // Skip it if it has already been searched
                if (!searched[ySearch][xSearch + 1])
                    // Skip  it if it's the wrong colour
                    if (rgb[ySearch][xSearch + 1][0] == rgb[y][x][0])
                        if (rgb[ySearch][xSearch + 1][1] == rgb[y][x][1])
                            if (rgb[ySearch][xSearch + 1][2] == rgb[y][x][2]) {
                                // If it's an unexplored pixel of the right colour, add it to the search set
                                xSet.add(xSearch + 1);
                                ySet.add(ySearch);
                                searched[ySearch][xSearch + 1] = true;
                            }
            }
            // Search the pixel above the current pixel
            if (ySearch > 0) {
                // Skip it if it has already been searched
                if (!searched[ySearch - 1][xSearch])
                    // Skip  it if it's the wrong colour
                    if (rgb[ySearch - 1][xSearch][0] == rgb[y][x][0])
                        if (rgb[ySearch - 1][xSearch][1] == rgb[y][x][1])
                            if (rgb[ySearch - 1][xSearch][2] == rgb[y][x][2]) {
                                // If it's an unexplored pixel of the right colour, add it to the search set
                                xSet.add(xSearch);
                                ySet.add(ySearch - 1);
                                searched[ySearch - 1][xSearch] = true;
                            }
            }
            // Search the pixel below the current pixel
            if (ySearch < height - 1) {
                // Skip it if it has already been searched
                if (!searched[ySearch + 1][xSearch])
                    // Skip  it if it's the wrong colour
                    if (rgb[ySearch + 1][xSearch][0] == rgb[y][x][0])
                        if (rgb[ySearch + 1][xSearch][1] == rgb[y][x][1])
                            if (rgb[ySearch + 1][xSearch][2] == rgb[y][x][2]) {
                                // If it's an unexplored pixel of the right colour, add it to the search set
                                xSet.add(xSearch);
                                ySet.add(ySearch + 1);
                                searched[ySearch + 1][xSearch] = true;
                            }
            }
        }

        int[][][] rgbOut = new int[height][width][3];
        for(int i = 0; i < height; i++)
            for(int j = 0; j < width; j++) {
                if(searched[i][j]) {
                    rgbOut[i][j][0] = r;
                    rgbOut[i][j][1] = g;
                    rgbOut[i][j][2] = b;
                }
                else {
                    rgbOut[i][j][0] = rgb[i][j][0];
                    rgbOut[i][j][1] = rgb[i][j][1];
                    rgbOut[i][j][2] = rgb[i][j][2];
                }
            }

        return new BitMapImage(rgbOut);
    }

    /**
     * Finds the coordinates of all pixels within the largest single-coloured area of the image
     * @return a 2d boolean array, true values correspond to the selected area
     *
     * TODO: does not work because of incorrect logic with continue; statements
     */
    public static boolean[][] largestColourArea(BitMapImage image) {
        // Extract the image pixels and its dimensions
        int height = image.getHeight();
        int width = image.getWidth();
        int[][][] rgb = new int[height][width][3];

        // Initialise search variables
        boolean[][] searched = new boolean[height][width];
        int largestArea = 0;
        boolean[][] bestArea = new boolean[height][width];

        // Iterate through every pixel on the  image
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                // Skip pixels that were already searched
                if(searched[y][x]) continue;
                // Extract the colour of the current pixel
                int r = rgb[y][x][0];
                int g = rgb[y][x][1];
                int b = rgb[y][x][2];
                // Create a set of all coordinates that belong to the area
                ArrayList<Integer> xSet = new ArrayList<Integer>();
                ArrayList<Integer> ySet = new ArrayList<Integer>();
                // Add the coordinate of the first pixel
                xSet.add(x);
                ySet.add(y);
                // Repeatedly check pixels until no more pixels are searchable
                int nextSearchIndex = 0;
                while(nextSearchIndex < xSet.size()) {
                    // Extract the coordinates of the next searchable pixel
                    int xSearch = xSet.get(nextSearchIndex);
                    int ySearch = ySet.get(nextSearchIndex);
                    // Search the pixel to the left of the current pixel
                    if(xSearch > 0) {
                        // Skip it if it has already been searched
                        if(searched[ySearch][xSearch - 1]) continue; // TODO: wrong use of continue in this method
                        // Skip  it if it's the wrong colour
                        if(rgb[ySearch][xSearch - 1][0] != r) continue;
                        if(rgb[ySearch][xSearch - 1][1] != g) continue;
                        if(rgb[ySearch][xSearch - 1][2] != b) continue;
                        // If it's an unexplored pixel of the right colour, add it to the search set
                        xSet.add(xSearch - 1);
                        ySet.add(ySearch);
                    }
                    // Search the pixel to the right of the current pixel
                    if(xSearch < width - 1) {
                        // Skip it if it has already been searched
                        if(searched[ySearch][xSearch + 1]) continue;
                        // Skip  it if it's the wrong colour
                        if(rgb[ySearch][xSearch + 1][0] != r) continue;
                        if(rgb[ySearch][xSearch + 1][1] != g) continue;
                        if(rgb[ySearch][xSearch + 1][2] != b) continue;
                        // If it's an unexplored pixel of the right colour, add it to the search set
                        xSet.add(xSearch + 1);
                        ySet.add(ySearch);
                    }
                    // Search the pixel above the current pixel
                    if(ySearch > 0) {
                        // Skip it if it has already been searched
                        if(searched[ySearch - 1][xSearch]) continue;
                        // Skip  it if it's the wrong colour
                        if(rgb[ySearch - 1][xSearch][0] != r) continue;
                        if(rgb[ySearch - 1][xSearch][1] != g) continue;
                        if(rgb[ySearch - 1][xSearch][2] != b) continue;
                        // If it's an unexplored pixel of the right colour, add it to the search set
                        xSet.add(xSearch);
                        ySet.add(ySearch - 1);
                    }
                    // Search the pixel below the current pixel
                    if(ySearch < height - 1) {
                        // Skip it if it has already been searched
                        if(searched[ySearch + 1][xSearch]) continue;
                        // Skip  it if it's the wrong colour
                        if(rgb[ySearch + 1][xSearch][0] != r) continue;
                        if(rgb[ySearch + 1][xSearch][1] != g) continue;
                        if(rgb[ySearch + 1][xSearch][2] != b) continue;
                        // If it's an unexplored pixel of the right colour, add it to the search set
                        xSet.add(xSearch);
                        ySet.add(ySearch + 1);
                    }
                    // Flag the current pixel as already searched
                    searched[ySearch][xSearch] = true;
                    // Advance to the next pixel in the search set
                    nextSearchIndex++;
                }
                int size = xSet.size();
                if(size > largestArea) {
                    largestArea = size;
                    bestArea = new boolean[height][width];
                    for(int i = 0; i < size; i++) {
                        bestArea[ySet.get(i)][xSet.get(i)] = true;
                    }
                }
            }
        }
        return bestArea;
    }

    public static BufferedImage arrayToBufferedImage(int[][][] rgb) {
        int height = rgb.length;
        int width = rgb[0].length;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = rgb[y][x][0];
                int g = rgb[y][x][1];
                int b = rgb[y][x][2];
                int colour = (r << 16) | (g << 8) | b;
                img.setRGB(x, y, colour);
            }
        }
        return img;
    }

}
