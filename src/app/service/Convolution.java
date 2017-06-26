package app.service;

import app.AppException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

public class Convolution
{
    private BufferedImage sourceImage;          // Исходное изображений
    private BufferedImage outputImage;          // Конечное изображение
    private BufferedImage preImage;             // Временное изображение (1 раз размытое)
    private final int SIZE = 80;                // Размер матрицы

    public BufferedImage getBufferedImage()
    {
        return outputImage;
    }

    public BufferedImage getSourceImage() {
        return sourceImage;
    }

    public Convolution(File file)
    {
        try {
            sourceImage = ImageIO.read(file);

            outputImage = convolutionHelper(sourceImage);
        } catch (IOException e) {
            AppException.Throw(e);
        }
    }

    private BufferedImage convolutionHelper(BufferedImage sourceImage) {

        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();

        // Создаем заготовку для нового изображения
        outputImage = new BufferedImage(width, height, sourceImage.getType());
        preImage = new BufferedImage(width, height, sourceImage.getType());

        float[] matrix = new float[SIZE];
        for (int i = 0; i < SIZE; i++)
            matrix[i] = 1.0f/(float) SIZE;
        // y
        BufferedImageOp op = new ConvolveOp( new Kernel(1, SIZE, matrix), ConvolveOp.EDGE_NO_OP, null );
        op.filter(sourceImage, preImage);
        // x
        op = new ConvolveOp( new Kernel(SIZE, 1, matrix), ConvolveOp.EDGE_NO_OP, null );
        op.filter(preImage, outputImage);

        return outputImage;
    }
}
