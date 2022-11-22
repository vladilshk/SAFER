package SAFER;

public class Operations {

    public static int sum(int num_1, int num_2){
        int result = (num_1 + num_2) % 256;
        if(result < 0)
            return result +256;
        else
            return result;

    }

    public static int sumMod2(int num_1, int num_2){
        int result = (num_1 ^ num_2) % 256;
        if(result < 0)
            return result +256;
        else
            return result;
    }

    public static int subtraction(int num_1, int num_2){
        int result = (num_1 - num_2) % 256;
        if(result < 0)
            return result +256;
        else
            return result;

    }

    public static int log(int num_1){
        return (int) (Math.log(num_1)/Math.log(45)) % 256;
    }

    public static int pow(int num_1){
        //return (int) (Math.pow(45, num_1) % 256);
        int result = 0;
        for (int i = 0; i < num_1; i++) {
            result = (result + (45 * 45) % 256) %256;
        }
        return result;
    }




    public  static int multiply(int num_1, int num_2){
        int result = (num_1 * num_2) % 256;
        if(result < 0)
            return result +256;
        else
            return result;
    }

    public static int binPow(int base, int degree, int mod) {
        base %= mod;
        if (degree == 0) return 1;
        else if (degree % 2 == 0) {
            return binPow((base * base) % mod, degree / 2, mod);
        }
        else return (base * binPow(base, degree - 1, mod)) % mod;
    }

    public static int binLog(int num){
        if(num == 0){
            return 128;
        }
        for (int i = 0; i < 257; i++) {
            if (binPow(45, i, 257) == num)
                return i;
        }
        return -1;
    }

    //коэффициент коррелиции
    public static double cofCor(int[][] mass1, int[][] mass2){
        double resultCof = 0;
        double[] cofCor = new double[mass1.length];
        double[] xy = new double[mass1.length];
        double x_medium[] = new double[mass1.length];
        double y_medium[] = new double[mass1.length];
        double x_square[] = new double[mass1.length];
        double y_square[] = new double[mass1.length];

        for (int j = 0; j < mass1.length; j++) {
            for (int i = 0; i < mass1[0].length; i++) {
                if(i == 0){
                    xy[j] = mass1[j][i] * mass2[j][i];
                    x_medium[j] = mass1[j][i];
                    y_medium[j] = mass2[j][i];
                    x_square[j] = mass1[j][i] * mass1[j][i];
                    y_square[j] = mass2[j][i] * mass2[j][i];
                }
                else {
                    xy[j] += mass1[j][i] * mass2[j][i];
                    x_medium[j] += mass1[j][i];
                    y_medium[j] += mass2[j][i];
                    x_square[j] += mass1[j][i] * mass1[j][i];
                    y_square[j] += mass2[j][i] * mass2[j][i];
                }
            }
            xy[j] /= 16;
            x_medium[j] /= 16;
            y_medium[j] /= 16;
            x_square[j] /= 16;
            y_square[j] /= 16;
        }


        double dispX[] = new double[mass1.length];
        double dispY[] = new double[mass1.length];
        for(int j = 0; j < mass1.length; j++){
            dispX[j] = Math.sqrt(x_square[j] - x_medium[j] * x_medium[j]);
            dispY[j] = Math.sqrt(y_square[j] - y_medium[j] * y_medium[j]);
            cofCor[j] = (xy[j] - (x_medium[j] * y_medium[j]))/(dispX[j] * dispY[j]);
            resultCof += cofCor[j];
        }


        return resultCof/cofCor.length;
    }
}