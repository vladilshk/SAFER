import SAFER.SAFER;
import SAFER.ImageConv;
import SAFER.Operations;

import java.io.IOException;
import java.util.Random;


public class Main {
    public static void main(String[] args) throws IOException {

        SAFER safer = new SAFER();
        int[][] image = ImageConv.getImage();
        int[][] copyImage = new int[image.length][image[0].length];
        for (int i = 0; i < image.length; i++){
            for (int j = 0; j < image[0].length; j++) {
                copyImage[i][j] = image[i][j];
            }
        }
        int[][] codedImage;


        System.out.println("Changing some pixels in image...");
        image = ImageConv.getImage();
        image = ImageConv.changePixel(image);
        ImageConv.makeImage(image, "with_changed_pixels");


        System.out.println("Default SAFER+ coding...");
        image = safer.codeText(image);
        System.out.println("Correlation coef after coding is: " + Operations.cofCor(copyImage, image));
        ImageConv.makeImage(image, "coded");

        System.out.println("Default SAFER+ decoding...");
        image = safer.decodeText(image);
        ImageConv.makeImage(image, "decoded");


        System.out.println();
        System.out.println("OFB SAFER+ coding...");
        image = safer.OfbCode(image);
        System.out.println("Correlation coef after OFB coding is: " + Operations.cofCor(copyImage, image));
        ImageConv.makeImage(image, "coded_OFB");

        System.out.println("OFB SAFER+ decoding...");
        image = safer.OfbDecode(image);
        ImageConv.makeImage(image, "decoded_OFB");



    }

}