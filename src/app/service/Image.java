package app.service;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image
{
    private BufferedImage image;        // Изображение
    private int height, width;          // Размеры изображения
    private int type;
    private int down, up, right, left;  // Расстояния от объекта изображения до границы
    private int ERROR_CLUSTERING = 250; // Погрешность обрезания

    Image(File file) throws IOException
    {
        this(ImageIO.read(file));
    }

    Image(BufferedImage image)
    {
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        type = image.getType();
    }

    public WritableImage getWritableImage()
    {
        return SwingFXUtils.toFXImage(image, null);
    }
    public BufferedImage getBufferedImage()
    {
        return image;
    }

    int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPixel(int w, int h) {
        return image.getRGB(w, h);
    }

    public void setPixel(int w, int h, int val) {
        image.setRGB(w, h, val);
    }

    int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }

    int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    void crop(int backgroundPixel)
    {
        up = upCropHelper(backgroundPixel, width, height);
        down = downCropHelper(backgroundPixel, width, height);
        left = leftCropHelper(backgroundPixel, width, height);
        right = rightCropHelper(backgroundPixel, width, height);
        crop(up, down, left, right);
    }

    void crop(int up, int down, int left, int right)
    {
        image = image.getSubimage(left, up, width - left - right, height - up - down);
        width = image.getWidth();
        height = image.getHeight();
    }

    @Override
    public String toString() {
        return "Image{" +
                ", height=" + height +
                ", width=" + width +
                ", type=" + type +
                ", down=" + down +
                ", up=" + up +
                ", right=" + right +
                ", left=" + left +
                ", ERROR_CLUSTERING=" + ERROR_CLUSTERING +
                '}';
    }

    @SuppressWarnings("Duplicates")
    private int leftCropHelper(int pureRGB, int width, int height)
    {
        int size = 0;
        for(int w = 0; w < width; ++w)
        {
            int goodRGB = 0;
            for(int h = 0; h < height; ++h)
            {
                if(pureRGB != image.getRGB(w, h))
                    goodRGB++;

            }
            if(goodRGB > height / ERROR_CLUSTERING)
                break;
            else
                size++;
        }
        return size;
    }

    @SuppressWarnings("Duplicates")
    private int rightCropHelper(int pureRGB, int width, int height)
    {
        int size = 0;
        for(int w = width - 1; w >= 0; --w)
        {
            int goodRGB = 0;
            for(int h = 0; h < height; ++h)
            {
                if(pureRGB != image.getRGB(w, h))
                    goodRGB++;

            }
            if(goodRGB > height / ERROR_CLUSTERING)
                break;
            else
                size++;
        }
        return size;
    }

    @SuppressWarnings("Duplicates")
    private int upCropHelper(int pureRGB, int width, int height)
    {
        int size = 0;
        for(int h = 0; h < height; ++h)
        {
            int goodRGB = 0;
            for(int w = 0; w < width; ++w)
            {
                if(pureRGB != image.getRGB(w, h))
                    goodRGB++;
            }
            if(goodRGB > height / ERROR_CLUSTERING)
                break;
            else
                size++;
        }
        return size;
    }

    @SuppressWarnings("Duplicates")
    private int downCropHelper(int pureRGB, int width, int height)
    {
        int size = 0;
        for(int h = height - 1; h >= 0; --h)
        {
            int goodRGB = 0;
            for(int w = 0; w < width; ++w)
            {
                if(pureRGB != image.getRGB(w, h))
                    goodRGB++;

            }
            if(goodRGB > height / ERROR_CLUSTERING)
                break;
            else
                size++;
        }
        return size;
    }
}
