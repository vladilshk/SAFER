package SAFER;

import java.io.IOException;
import java.util.Random;

public class SAFER {

    int[][] text;
    int[][] editedText;
    int[][] keySet;
    int[][] matrix;
    int[][] matrix2;
    int[][] vector;
    int[][] info;

    int roundNum;

    public SAFER() {
        Key key = new Key();
        //keySet = key.getKeySet();
    }


    public int[][] codeText(int[][] text) throws IOException {
        int[][] info = new int[3][16];
        int[][] codeImage = new int[text.length][text[0].length];
        saveHeader(text);
        roundNum = 1;
        Key key = new Key();
        keySet = key.getKeySet(text.length * 2);
        this.text = text;
        createMatrix();
        editedText = text;
        for (int i = 0; i < 8; i++) {
            overlayKey();
            nonLinearTransformation();
            secondOverlayKey();
            linearTransformation(matrix);
            thirdKeyOverlay();
            codeImage = loadHeader(editedText);
            ImageConv.makeImage(codeImage, "codedRounds/CodeRound" + roundNum);
            roundNum++;
        }

        codeImage = loadHeader(editedText);

        return codeImage;
    }


    public int[][] decodeText(int[][] text){
        roundNum = 8;
        this.text = text;
        editedText = text;
        createMatrix();
        for (int i = 0; i < 8; i++) {
            thirdKeyOverlayDecode();
            linearTransformation(matrix2);
            secondOverlayKeyDecode();
            nonLinearTransformationDecode();
            overlayKeyDecode();
            roundNum--;
        }
        editedText = loadHeader(editedText);
        return editedText;
    }


    public int[][] OfbCode(int[][] inputText) throws IOException {
        saveHeader(inputText);
        createMatrix();
        roundNum = 1;
        Key key = new Key();
        text = generateVector(inputText.length, inputText[0].length);
        int[][] result = new int[inputText.length][inputText[0].length];
        editedText = text;
        keySet = key.getKeySet(text.length * 2);
        for (int i = 0; i < 8; i++) {
            overlayKey();
            nonLinearTransformation();
            secondOverlayKey();
            linearTransformation(matrix);
            thirdKeyOverlay();
            roundNum++;
        }
        result = XOR(editedText, inputText);
        result = loadHeader(result);
        return result;
    }

    public int[][] OfbDecode(int[][] inputText){
        roundNum = 8;
        editedText = XOR(editedText, inputText);
        editedText = loadHeader(editedText);
        return editedText;
    }

    //coding operations
    public void overlayKey() {
        for (int j = 0; j < text.length; j++) {
            for (int i = 0; i < text[0].length; i++) {
                if (thirstType(i)) {
                    editedText[j][i] = Operations.sumMod2(editedText[j][i], keySet[2 * j][i]);
                } else {
                    editedText[j][i] = Operations.sum(editedText[j][i], keySet[2 * j][i]);
                }
            }
        }
    }

    public void nonLinearTransformation() {
        for (int j = 0; j < text.length; j++) {
            for (int i = 0; i < text[0].length; i++) {
                if (thirstType(i)) {
                    editedText[j][i] = Operations.binPow(45 ,editedText[j][i], 257) % 256;
                } else {
                    editedText[j][i] = Operations.binLog(editedText[j][i]) % 256;
                }
            }
        }
    }

    public void secondOverlayKey() {
        for (int j = 0; j < text.length; j++) {
            for (int i = 0; i < text[0].length; i++) {
                if (!thirstType(i)) {
                    editedText[j][i] = Operations.sumMod2(editedText[j][i], keySet[2 * j + 1][i]);
                } else {
                    editedText[j][i] = Operations.sum(editedText[j][i], keySet[2 * j + 1][i]);
                }
            }
        }
    }

    public void linearTransformation(int[][] matrix) {
        for (int blockNum = 0; blockNum < editedText.length; blockNum++) {
            int[] result = new int[16];
            for (int i = 0; i < editedText[0].length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    result[i] = (result[i] + Operations.multiply(editedText[blockNum][j], matrix[j][i])) % 256;
                }
            }
            editedText[blockNum] = result;
        }
    }

    //decoding operations
    public void overlayKeyDecode(){
        for (int j = 0; j < text.length; j++) {
            for (int i = 0; i < text[0].length; i++) {
                if (thirstType(i)) {
                    editedText[j][i] = Operations.sumMod2(editedText[j][i], keySet[2 * j][i]);
                } else {
                    editedText[j][i] = Operations.subtraction(editedText[j][i], keySet[2 * j][i]);
                }
            }
        }
    }

    public void nonLinearTransformationDecode() {
        for (int j = 0; j < text.length; j++) {
            for (int i = 0; i < text[0].length; i++) {
                if (!thirstType(i)) {
                    editedText[j][i] = Operations.binPow(45, editedText[j][i], 257) % 256;
                } else {
                    editedText[j][i] = Operations.binLog(editedText[j][i]) % 256;
                }
            }
        }
    }

    public void secondOverlayKeyDecode() {
        for (int j = 0; j < text.length; j++) {
            for (int i = 0; i < text[0].length; i++) {
                if (!thirstType(i)) {
                    editedText[j][i] = Operations.sumMod2(editedText[j][i], keySet[2 * j + 1][i]);
                } else {
                    editedText[j][i] = Operations.subtraction(editedText[j][i], keySet[2 * j + 1][i]);
                }
            }
        }
    }

    // 1 4 5 8 9 12 13 16
    public boolean thirstType(int number) {
        number++;
        if (number == 1 || number == 4 || number == 5 || number == 8 || number == 9 || number == 12 || number == 13 || number == 16) {
            return true;
        }
        return false;
    }

    // matrix for linearTransformation
    public void createMatrix() {
        matrix = new int[][]{
                {2, 2, 1, 1, 16, 8, 2, 1, 4, 2, 4, 2, 1, 1, 4, 4},
                {1, 1, 1, 1, 8, 4, 2, 1, 2, 1, 4, 2, 1, 1, 2, 2},
                {1, 1, 4, 4, 2, 1, 4, 2, 4, 2, 16, 8, 2, 2, 1, 1},
                {1, 1, 2, 2, 2, 1, 2, 1, 4, 2, 8, 4, 1, 1, 1, 1},
                {4, 4, 2, 1, 4, 2, 4, 2, 16, 8, 1, 1, 1, 1, 2, 2},
                {2, 2, 2, 1, 2, 1, 4, 2, 8, 4, 1, 1, 1, 1, 1, 1},
                {1, 1, 4, 2, 4, 2, 16, 8, 2, 1, 2, 2, 4, 4, 1, 1},
                {1, 1, 2, 1, 4, 2, 8, 4, 2, 1, 1, 1, 2, 2, 1, 1},
                {2, 1, 16, 8, 1, 1, 2, 2, 1, 1, 4, 4, 4, 2, 4, 2},
                {2, 1, 8, 4, 1, 1, 1, 1, 1, 1, 2, 2, 4, 2, 2, 1},
                {4, 2, 4, 2, 4, 4, 1, 1, 2, 2, 1, 1, 16, 8, 2, 1},
                {2, 1, 4, 2, 2, 2, 1, 1, 1, 1, 1, 1, 8, 4, 2, 1},
                {4, 2, 2, 2, 1, 1, 4, 4, 1, 1, 4, 2, 2, 1, 16, 8},
                {4, 2, 1, 1, 1, 1, 2, 2, 1, 1, 2, 1, 2, 1, 8, 4},
                {16, 8, 1, 1, 2, 2, 1, 1, 4, 4, 2, 1, 4, 2, 4, 2},
                {8, 4, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 4, 2}};

        matrix2 = new int[][]{
                {2, -2, 1, -2, 1, -1, 4, -8, 2, -4, 1, -1, 1, -2, 1, -1},
                {-4, 4, -2, 4, -2, 2, -8, 16, -2, 4, -1, 1, -1, 2, -1, 1},
                {1, -2, 1, -1, 2, -4, 1, -1, 1, -1, 1, -2, 2, -2, 4, -8},
                {-2, 4, -2, 2, -2, 4, -1, 1, -1, 1, -1, 2, -4, 4, -8, 16},
                {1, -1, 2, -4, 1, -1, 1, -2, 1, -2, 1, -1, 4, -8, 2, -2},
                {-1, 1, -2, 4, -1, 1, -1, 2, -2, 4, -2, 2, -8, 16, -4, 4},
                {2, -4, 1, -1, 1, -2, 1, -1, 2, -2, 4, -8, 1, -1, 1, -2},
                {-2, 4, -1, 1, -1, 2, -1, 1, -4, 4, -8, 16, -2, 2, -2, 4},
                {1, -1, 1, -2, 1, -1, 2, -4, 4, -8, 2, -2, 1, -2, 1, -1},
                {-1, 1, -1, 2, -1, 1, -2, 4, -8, 16, -4, 4, -2, 4, -2, 2},
                {1, -2, 1, -1, 4, -8, 2, -2, 1, -1, 1, -2, 1, -1, 2, -4},
                {-1, 2, -1, 1, -8, 16, -4, 4, -2, 2, -2, 4, -1, 1, -2, 4},
                {4, -8, 2, -2, 1, -2, 1, -1, 1, -2, 1, -1, 2, -4, 1, -1},
                {-8, 16, -4, 4, -2, 4, -2, 2, -1, 2, -1, 1, -2, 4, -1, 1},
                {1, -1, 4, -8, 2, -2, 1, -2, 1, -1, 2, -4, 1, -1, 1, -2},
                {-2, 2, -8, 16, -4, 4, -2, 4, -1, 1, -2, 4, -1, 1, -1, 2}
        };

    }


    public void thirdKeyOverlay(){
        for (int j = 0; j < text.length; j++) {
            for (int i = 0; i < text[0].length; i++) {
                if (thirstType(i)) {
                    editedText[j][i] = Operations.sumMod2(editedText[j][i], keySet[2 * roundNum + 1][i]);
                } else {
                    editedText[j][i] = Operations.sum(editedText[j][i], keySet[2 * roundNum + 1][i]);
                }
            }
        }
    }

    public void thirdKeyOverlayDecode(){
        for (int j = 0; j < text.length; j++) {
            for (int i = 0; i < text[0].length; i++) {
                if (thirstType(i)) {
                    editedText[j][i] = Operations.sumMod2(editedText[j][i], keySet[2 * roundNum + 1][i]);
                } else {
                    editedText[j][i] = Operations.subtraction(editedText[j][i], keySet[2 * roundNum + 1][i]);
                }
            }
        }
    }

    public int[][] generateVector(int blocks, int length){
        int[][] vector = new int[blocks][length];
        Random random = new Random();
        for(int j = 0; j < vector.length; j++) {
            for (int i = 0; i < vector[0].length; i++) {
                vector[j][i] = random.nextInt(256);
            }
        }
        return vector;
    }

    public int[][] XOR(int [][] thirstText, int[][] secondText){
        int[][] result = new int[thirstText.length][thirstText[0].length];
        for (int i = 0; i < thirstText.length; i++) {
            for (int j = 0; j < thirstText[0].length; j++) {
                result[i][j] = Operations.sumMod2(thirstText[i][j], secondText[i][j]);
            }
        }
        return result;
    }


    public void saveHeader(int[][] image){
        info = new int[3][16];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < image[0].length; j++) {
                info[i][j] = image[i][j];
            }
        }
    }

    public int[][] loadHeader(int[][] image){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < image[0].length; j++) {
                image[i][j] = info[i][j];
            }
        }
        return image;
    }


}
