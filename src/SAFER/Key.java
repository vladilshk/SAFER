package SAFER;


import java.util.Random;


//this class is used to generate keys
public class Key {
    private int[] key;
    private int[][] keySet;

    private final int BYTE_LENGTH = 8;

    private int[][] offsetWorlds;

    // method used to make and get keyset
    public  int[][] getKeySet(int amount){
        keySet = new int[amount][17];
        offsetWorlds = new int[amount][17];
        generateOffsetWords();
        generateKey();
        firstKey();
        makeKeys();
        return keySet;
    }

    public Key() {
        key = new int[16];
        keySet = new int[8][17];
        offsetWorlds = new int[8][17];
        /*generateOffsetWords();
        generateKey();
        firstKey();
        makeKeys();
        keysToString();*/
    }

    //make thirst key for our keyset
    public void firstKey() {
        for (int i = 0; i < key.length; i++) {
            keySet[0][i] = key[i];
        }
        int keySum = key[0];
        for (int i = 0; i < key.length - 1; i += 1) {
            keySum ^= key[i];
        }
        keySet[0][16] = keySum;
    }

    //fill keyset
    public void makeKeys(){
        for (int keyNum = 1; keyNum < keySet.length; keyNum++) {
            int[] bytes = new int[17];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (int) (keySet[0][i] << 3);
            }
            int counter = 0;
            for (int j = keyNum; j < keyNum + 17; j++){
                keySet[keyNum][counter] = bytes[j % 16];
                keySet[keyNum][counter] = (int)((keySet[keyNum][counter] + offsetWorlds[keyNum][counter]) % 256);
                counter++;
            }
        }
    }


    //random base key
    public void generateKey(){
        Random random = new Random();
        for (int i = 0; i < key.length; i++) {
            key[i] = random.nextInt(256);
        }
    }


    //test method to get keys
    public void keysToString(){
        for(int keyNum = 0; keyNum < keySet.length; keyNum++) {
            System.out.println("Safer.Key " + (keyNum + 1) + ":");
            for (int i = 0; i < keySet[keyNum].length; i++) {
                System.out.print(keySet[keyNum][i] + "; ");
            }
            System.out.println();
        }
    }


    //method to generate offsetWords
    public void generateOffsetWords(){
        for (int i = 2; i < keySet.length; i++) {
            int worlds[] = new int[17];
            for(int j = 0; j < 16; j++){
                worlds[j] = (int) (Math.pow(45, Math.pow(45, 17*i + j) % 257) % 257);
            }
            offsetWorlds[i] = worlds;
        }
    }



}
