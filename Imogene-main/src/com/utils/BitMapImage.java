package com.utils;

public class BitMapImage {
    private int width;
    private int height;
    private int[][][] rgb;

    public BitMapImage(int width, int height) {
        this.width = width;
        this.height = height;
        rgb = new int[height][width][3];
    }

    public BitMapImage(int[][][] rgb) {
        this.width = rgb[0].length;
        this.height = rgb.length;
        this.rgb = rgb;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Resets the image to black
     */
    public void resetImage() {
        rgb = new int[rgb.length][rgb[0].length][3];
    }

    /**
     * Sets the image dimensions and resets the image to black
     */
    public void resetImage(int width, int height) {
        this.width = width;
        this.height = height;
        rgb = new int[height][width][3];
    }

    /**
     * Sets the RGB values for a specified pixel
     */
    public void setPixel(int x, int y, int r, int g, int b) {
        rgb[y][x][0] = r;
        rgb[y][x][1] = g;
        rgb[y][x][2] = b;
    }

    public int[][][] getRgb() {
        return rgb;
    }

    public void setRgb(int[][][] rgb) {
        this.rgb = rgb;
        this.height = rgb.length;
        this.width = rgb[0].length;
    }

}
