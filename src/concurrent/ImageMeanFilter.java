import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * This class provides functionality to apply a mean filter to an image.
 * The mean filter is used to smooth images by averaging the pixel values
 * in a neighborhood defined by a kernel size.
 * 
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * ImageMeanFilter.applyMeanFilter("input.jpg", "output.jpg", 3);
 * }
 * </pre>
 * 
 * <p>Supported image formats: JPG, PNG</p>
 * 
 * <p>Author: temmanuel@comptuacao.ufcg.edu.br</p>
 */
public class ImageMeanFilter {
    
    /**
     * Applies mean filter to an image
     * 
     * @param inputPath  Path to input image
     * @param outputPath Path to output image 
     * @param kernelSize Size of mean kernel
     * @throws IOException If there is an error reading/writing
     */
    public static void applyMeanFilter(String inputPath, String outputPath, int kernelSize, int threadCount) throws IOException {
        // Load image
        BufferedImage originalImage = ImageIO.read(new File(inputPath));
        
        // Create result image
        BufferedImage filteredImage = new BufferedImage(
            originalImage.getWidth(), 
            originalImage.getHeight(), 
            BufferedImage.TYPE_INT_RGB
        );
        
        Thread[] listaThreads = new Thread[threadCount];

        for(int i = 0; i < threadCount; i++) {
            listaThreads[i] = new Thread(new HelloRunnable(), "my-tread-HelloRunnable" + i);
        }

        // Image processing
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        // Dividindo a imagem em blocos para execucao das threads
        int tamanhoBloco = height / threadCount;
        int restoImagem = height - (tamanhoBloco * threadCount);
        
        //Executando as threads em cada bloco
        for (int i = 0; i < threadCount; i++) {
            int colunaInicial = i * tamanhoBloco;
            int colunaFinal = colunaInicial + tamanhoBloco;
            
            if (i == threadCount - 1) {
                colunaFinal += restoImagem;
            }

            listaThreads[i].start();
        }
        = 
        
        // Save filtered image
        ImageIO.write(filteredImage, "jpg", new File(outputPath));
    }
    
    /**
     * Calculates average colors in a pixel's neighborhood
     * 
     * @param image      Source image
     * @param centerX    X coordinate of center pixel
     * @param centerY    Y coordinate of center pixel
     * @param kernelSize Kernel size
     * @return Array with R, G, B averages
     */
    private static int[] calculateNeighborhoodAverage(BufferedImage image, int centerX, int centerY, int kernelSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        int pad = kernelSize / 2;
        
        // Arrays for color sums
        long redSum = 0, greenSum = 0, blueSum = 0;
        int pixelCount = 0;
        
        // Process neighborhood
        for (int dy = -pad; dy <= pad; dy++) {
            for (int dx = -pad; dx <= pad; dx++) {
                int x = centerX + dx;
                int y = centerY + dy;
                
                // Check image bounds
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    // Get pixel color
                    int rgb = image.getRGB(x, y);
                    
                    // Extract color components
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    
                    // Sum colors
                    redSum += red;
                    greenSum += green;
                    blueSum += blue;
                    pixelCount++;
                }
            }
        }
        
        // Calculate average
        return new int[] {
            (int)(redSum / pixelCount),
            (int)(greenSum / pixelCount),
            (int)(blueSum / pixelCount)
        };
    }
    
    /**
     * Main method for demonstration
     * 
     * Usage: java ImageMeanFilter <input_file>
     * 
     * Arguments:
     *   input_file - Path to the input image file to be processed
     *                Supported formats: JPG, PNG
     * 
     * Example:
     *   java ImageMeanFilter input.jpg
     * 
     * The program will generate a filtered output image named "filtered_output.jpg"
     * using a 7x7 mean filter kernel
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java ImageMeanFilter <input_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        try {
            applyMeanFilter(inputFile, "filtered_output.jpg", 7, 4);
        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
        }
    }
}

class HelloRunnable implements Runnable {

    private int colunaInicial;
    private int colunaFinal;
    private int width;
    private int kernelSize;

    public HelloRunnable(int colunaInicial, int colunaFinal, int width, int kernelSize) {
        this.colunaInicial = colunaInicial;
        this.colunaFinal = colunaFinal;
        this.width = width;
        this.kernelSize = kernelSize;
    }

    /*
     * Não Compila.
   
    public HelloRunnable() {

    }

    public static void main(String[] args) {
        HelloRunnable helloRunnable = new HelloRunnable();
        helloRunnable.run();

    }
    //Tentamos executar os blocos, mas ficamos sem tempo para resolver o problema
    @Override
    public void run() {
            for (int i = this.colunaInicial; i < this.colunaFinal; i++) {
                for (int x = 0; x < width; x++) {
                    // Calculate neighborhood average
                    int[] avgColor = calculateNeighborhoodAverage(originalImage, x, i, kernelSize);
                    
                    // Set filtered pixel
                    filteredImage.setRGB(x, y, 
                        (avgColor[0] << 16) | 
                        (avgColor[1] << 8)  | 
                        avgColor[2]
                    );
                }
            }
    }
*/
}