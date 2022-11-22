package SAFER;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.awt.image.*;
import java.util.Random;

public class ImageConv {


    public static int[][] getImage() throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File("/home/voldi/Рабочий стол/5 semester/Крипта/SAFER_Plus_Cript_Algorithm/src/pepaPig.bmp"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "bmp", bos );
        byte [] data = bos.toByteArray();
        int[][] blockImage = new int[data.length/16 + 1][16];

        int blockNum = 0;
        int byteNum = 0;

        for (int i = 0; i < data.length; i++) {
            blockImage[blockNum][byteNum] = ((int)data[i] + 128);
            byteNum++;
            if (byteNum == 16){
                byteNum = 0;
                blockNum ++;
            }
        }

        /*if ((blockImage.length + 1) * (blockImage[0].length + 1) > data.length){
            for (int i = data.length % 16; i < 16; i++) {
                blockImage
            }
        }*/
        return blockImage;

    }

    public static void makeImage(int[][] image, String name) throws IOException {
        byte[] data = new byte[(image.length + 1) * (image[0].length + 1)];
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                if (image.length -1 == i){
                    if (data[i] == 0 && data[i + 1] == 0 & data[i + 3] == 0){
                        break;
                    }
                }
                else {
                    data[image[0].length * i + j] = (byte) (image[i][j] - 128);
                }
            }
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, "bmp", new File(  name + ".bmp") );
        System.out.println("image created");
    }

    public static int[][] changePixel(int[][] image){
        int width = image.length / 2;
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            image[width][i] = random.nextInt(256);
        }
        return image;
    }
}
