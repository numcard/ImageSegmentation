package app.service;

import app.AppException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class KMeans
{
    private Image sourceImage;          // Исхожное изобажений
    private Image bufferedImage;        // Изображение в кластерах
    private Image outputImage;          // Выходное изображение
    private int[][] pixel_assignments;
    private int WHITE = -1;

    public Image getOutputImage() {
        return outputImage;
    }

    public Image getSourceImage() {
        return sourceImage;
    }

    public Image getBufferedImage() {
        return bufferedImage;
    }

    // Конструктор по файлу
    public KMeans(File file)
    {
        try
        {
            sourceImage = new Image(file);
            kmeansHelper();
        }
        catch(IOException e)
        {
            AppException.Throw(e);
        }
    }

    // Конструктор по изображению
    public KMeans(BufferedImage image)
    {
            sourceImage = new Image(image);
            kmeansHelper();
    }

    private void kmeansHelper()
    {
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        int [][] rgb = new int[width][height];

        // Создаем заготовку для нового изображения
        BufferedImage buffered = new BufferedImage(width, height, sourceImage.getBufferedImage().getType());
        Graphics2D graphics = buffered.createGraphics();
        graphics.drawImage(sourceImage.getBufferedImage(), 0, 0, width, height, null);

        for(int w = 0; w < width; w++)
        {
            for(int h = 0; h < height; h++)
            {
                rgb[w][h] = buffered.getRGB(w, h);
            }
        }
        // Вызываем метод "алгоритм", который обновляет значения rgb
        algorithm(rgb);

        // Записываем новые значения rgb в изображение
        for(int w = 0; w < width; w++)
        {
            for(int h = 0; h < height; h++)
            {
                buffered.setRGB(w, h, rgb[w][h]);
            }
        }
        // Записываем изображение
        bufferedImage = new Image(buffered);

        // Заранее создаем выходное изображение
        outputImage = new Image(sourceImage.getBufferedImage());
    }

    private void algorithm(int[][] rgb)
    {
        int NUMBER_OF_CLUSTERS = 2;
        int[] clusters_x = new int[NUMBER_OF_CLUSTERS];
        int[] clusters_y = new int[NUMBER_OF_CLUSTERS];
        int[] clusters = new int[NUMBER_OF_CLUSTERS];
        Random rand = new Random();

        // Выбираем первоначальные кластеры
        for(int i = 0; i < clusters.length; i++)
        {
            boolean contains_duplicate = true;
            // В первый кластер записываем центральную точку изображения
            if(i == 0)
            {
                // Центр фото
                clusters[i] = rgb[rgb.length / 2][rgb[0].length / 2];
                clusters_x[i] =  rgb.length;
                clusters_y[i] =  rgb[0].length;
            }
            // В остальные кластеры записываем рандомную точку отлич. от 1 кластера
            else
            {
                do
                {
                    int random_x, random_y;
                    random_x = rand.nextInt(rgb.length);
                    random_y = rand.nextInt(rgb[0].length);
                    clusters_x[i] =  random_x;
                    clusters_y[i] =  random_y;

                    for(int j = 0; j < i; j++)
                    {
                        if(j == i - 1 && clusters[j] != rgb[random_x][random_y])
                        {
                            // Если 2 кластер и цвета не совпадают, то условие выполнено
                            clusters[i] = rgb[random_x][random_y];
                            contains_duplicate = false;
                        }
                        else if(clusters[j] == rgb[random_x][random_y])
                        {
                            // Выходим из цикла for т.к. найден дубликат
                            // Пробуем снова, с новыми случ. значениями
                            j = i;
                        }
                    }
                } while(contains_duplicate);
            }
        }

        for(int i = 0; i < clusters.length; i++)
        {
            System.out.println("r: " + getRed(clusters[i]) + ", g: " + getGreen(clusters[i]) + ", b: " + getBlue(clusters[i]));
        }

        // Группируем пиксели изображения с центрами кластеров
        // pixel assignments (by their index)
        pixel_assignments = new int[rgb.length][rgb[0].length];
        int[] num_assignments = new int[NUMBER_OF_CLUSTERS];

        // Cluster sums for current cluster values (represented by index)
        int[] alpha_sum = new int[NUMBER_OF_CLUSTERS];
        int[] red_sum = new int[NUMBER_OF_CLUSTERS];
        int[] green_sum = new int[NUMBER_OF_CLUSTERS];
        int[] blue_sum = new int[NUMBER_OF_CLUSTERS];

        // Кол-во итераций до схождения. Больше 100 ставить не стоит
        int max_iterations = 100;
        int num_iterations = 1;
        int[] center_iterations = new int[NUMBER_OF_CLUSTERS];


        while(num_iterations <= max_iterations)
        {
            // Clear number of assignments list first
            for(int i = 0; i < clusters.length; i++)
            {
                num_assignments[i] = 0;
                alpha_sum[i] = 0;
                red_sum[i] = 0;
                green_sum[i] = 0;
                blue_sum[i] = 0;
            }

            // Go through all pixels in rgb
            for(int i = 0; i < rgb.length; i++)
            {
                for(int j = 0; j < rgb[0].length; ++j)
                {
                    // Set min_dist initially to infinity (or very large number that
                    // wouldn't appear as a distance anyways)
                    double min_dist = Double.MAX_VALUE;
                    int cluster_index = 0;
                    // compare instance's RGB value to each cluster point
                    for(int k = 0; k < clusters.length; k++)
                    {
                        float a_dist = (getAlpha(rgb[i][j]) - getAlpha(clusters[k])) / 255.f;
                        float r_dist = (getRed(rgb[i][j]) - getRed(clusters[k]))  / 255.f;
                        float g_dist = (getGreen(rgb[i][j]) - getGreen(clusters[k]))  / 255.f;
                        float b_dist = (getBlue(rgb[i][j]) - getBlue(clusters[k]))  / 255.f;
                        //float len = (clusters_x[k] - i) / ((float) rgb.length) + (clusters_y[k] - j) / ((float) rgb[0].length);
                        float dist = (float) Math.sqrt(a_dist * a_dist + r_dist * r_dist + g_dist * g_dist + b_dist * b_dist);
                        if(dist < min_dist)
                        {
                            min_dist = dist;
                            cluster_index = k;
                        }
                    }
                    // Assign pixel to cluster
                    pixel_assignments[i][j] = cluster_index;
                    num_assignments[cluster_index]++;
                    // Add pixel's individual argb values to respective sums for use
                    // later
                    alpha_sum[cluster_index] += getAlpha(rgb[i][j]);
                    red_sum[cluster_index] += getRed(rgb[i][j]);
                    green_sum[cluster_index] += getGreen(rgb[i][j]);
                    blue_sum[cluster_index] += getBlue(rgb[i][j]);
                }
            }

            // update previous assignments list
            for(int i = 0; i < clusters.length; i++)
            {
                int avg_alpha = (int) ((double) alpha_sum[i] / (double) num_assignments[i]);
                int avg_red = (int) ((double) red_sum[i] / (double) num_assignments[i]);
                int avg_green = (int) ((double) green_sum[i] / (double) num_assignments[i]);
                int avg_blue = (int) ((double) blue_sum[i] / (double) num_assignments[i]);
                clusters[i] = ((avg_alpha & 0x000000FF) << 24) | ((avg_red & 0x000000FF) << 16) | ((avg_green & 0x000000FF) << 8) | ((avg_blue & 0x000000FF));
            }

            if(num_iterations == 1)
            {
                System.arraycopy(clusters, 0, center_iterations, 0, clusters.length);
            }
            else
            {
                int counter = 0;
                for(int i = 0; i < clusters.length; i++)
                {
                    if(clusters[i] == center_iterations[i])
                        counter++;
                }

                if(counter == NUMBER_OF_CLUSTERS)
                    break;
                else
                    System.arraycopy(clusters, 0, center_iterations, 0, clusters.length);
            }
            num_iterations++;
        }

        // update RGB array
        for(int i = 0; i < rgb.length; i++)
        {
            for(int j = 0; j < rgb[0].length; ++j)
            {
                if(pixel_assignments[i][j] == 0) {
                    //rgb[i][j] = clusters[pixel_assignments[i][j]];
                }
                else
                    rgb[i][j] = WHITE;
            }
        }

        for(int i = 0; i < clusters.length; i++)
        {
            System.out.println("r: " + getRed(clusters[i]) + ", g: " + getGreen(clusters[i]) + ", b: " + getBlue(clusters[i]));
        }
    }

    public void removeBackground(BufferedImage image)
    {
        outputImage = new Image(image);
        int width = outputImage.getBufferedImage().getWidth();
        int height = outputImage.getBufferedImage().getHeight();
        int[][] rgb = new int[width][height];

        for(int w = 0; w < outputImage.getBufferedImage().getWidth(); w++)
        {
            for(int h = 0; h < outputImage.getBufferedImage().getHeight(); h++)
            {
                rgb[w][h] = outputImage.getBufferedImage().getRGB(w, h);
            }
        }

        // update RGB array
        for(int i = 0; i < rgb.length; i++)
        {
            for(int j = 0; j < rgb[0].length; ++j)
            {
                if(pixel_assignments[i][j] == 0) {
                    //rgb[i][j] = clusters[pixel_assignments[i][j]];
                }
                else
                    rgb[i][j] = 16777215;
            }
        }

        // Записываем новые значения rgb в изображение
        for(int w = 0; w < width; w++)
        {
            for(int h = 0; h < height; h++)
            {
                outputImage.getBufferedImage().setRGB(w, h, rgb[w][h]);
            }
        }
    }

    public void crop(BufferedImage image)
    {
        outputImage.crop(WHITE);
        int up = outputImage.getUp(),
                down = outputImage.getDown(),
                left = outputImage.getLeft(),
                right = outputImage.getRight();
        outputImage = new Image(image);
        outputImage.crop(up, down, left, right);
    }

    public void crop()
    {
        outputImage.crop(WHITE);
    }

    // HELPER FUNCTIONS - to get individual R, G, and B values
    private static int getRed(int pix)
    {
        return (pix >> 16) & 0xFF;
    }

    private static int getGreen(int pix)
    {
        return (pix >> 8) & 0xFF;
    }

    private static int getBlue(int pix)
    {
        return pix & 0xFF;
    }

    private static int getAlpha(int pix)
    {
        return (pix >> 24) & 0xFF;
    }
}