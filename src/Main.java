import java.math.BigInteger;
import java.util.Scanner;

public class Main {
    private static String m; // message:

    private static String enterMessage() {
        System.out.println("Enter m(essage): ");
        Scanner scanner = new Scanner(System.in);
        m = scanner.nextLine();

        return m;
    }

    public static void main(String args[]) {
        System.out.println("Enter p: ");
        BigInteger p;
        Scanner scanner = new Scanner(System.in);
        p = scanner.nextBigInteger();

        System.out.println("Enter q: ");
        BigInteger q;
        q = scanner.nextBigInteger();

        RSA rsa1 = new RSA(p, q);
        String isPPrime = null;
        String isQPrime = null;

        isPPrime = rsa1.isPrimeTrialDivision(p);
        isQPrime = rsa1.isPrimeTrialDivision(q);

        // if both numbers are prime:
        if (isPPrime.equals("yes") && isQPrime.equals("yes")) {
            BigInteger n = rsa1.getN();

            // if 256^2 > n > 256, code with 1 letter:
            if ((n.compareTo(new BigInteger("65536"))) == -1) {
                if ((n.compareTo(new BigInteger("256")) == 1)) {
                    // ====================================================================   <--
                    // SKOPIUJ TO NIŻEJ:
                    System.out.println("Enter e: ");
                    BigInteger e;
                    e = scanner.nextBigInteger();

                    // czy to ma być fi - 1 ???? ???? ???? ??? ??? ?? ?? /???????????????????????????           <<<-----------------
                    if ((e.compareTo(rsa1.getFi())) == -1) {
                        if ((e.compareTo(new BigInteger("1")) == 1)) {
                            System.out.println("e is OK!");
                            // set the value of e with the input:
                            rsa1.setE(e);
                        }
                        else
                        {
                            System.out.println("WRONG e - not from period (1, fi) !!!");
                            System.exit(0);
                        }
                    }

                    //if (checkModuloInversion(rsa1) == true) {
                    if (rsa1.checkModuloInversion() == true) {
                        int intD = rsa1.invMod(rsa1.getE(), rsa1.getFi());
                        // do some conversions of types:
                        long longD = Long.valueOf(intD);
                        String stringD = String.valueOf(longD);
                        BigInteger bigIntegerD = new BigInteger(stringD);
                        // set rsa1's D with the value of d:
                        rsa1.setD(bigIntegerD);

                        // enter and set m:
                        rsa1.setM(enterMessage());

                        // coding and decoding:
                        for (int i = 0; i < rsa1.getM().length(); i++) {
                            // catch the sign:
                            char tmp = rsa1.getM().charAt(i);
                            int ascii_value = tmp;
                            String ascii = Integer.toString(ascii_value);
                            // convert to BigInteger:
                            BigInteger bigInteger = new BigInteger(ascii);

                            // code the number:
                            BigInteger c = rsa1.code(bigInteger);
                            //System.out.println("coded c: " + c);

                            // decode the number:
                            BigInteger d = rsa1.decode(c);
                            //System.out.println("decoded d: " + d);
                        }

                        // printing:
                        rsa1.printAll();
                    }
                    else {
                        System.out.println("The smallest common divisor");
                    }
                }
            }

            // if 256^3 > n > 256^2, code with 2 letters:
            if ((n.compareTo(new BigInteger("16777216"))) == -1) {
                if ((n.compareTo(new BigInteger("65536")) == 1)) {
                    if ((n.compareTo(new BigInteger("256")) == 1)) {
                        System.out.println("Enter e: ");
                        BigInteger e;
                        e = scanner.nextBigInteger();

                        if ((e.compareTo(rsa1.getFi())) == -1) {
                            if ((e.compareTo(new BigInteger("1")) == 1)) {
                                System.out.println("e is OK!");
                                // set the value of e with the input:
                                rsa1.setE(e);
                            }
                            else {
                                System.out.println("WRONG e - not from period (1, fi) !!!");
                                System.exit(0);
                            }
                        }

                        if (rsa1.checkModuloInversion() == true) {
                            int intD = rsa1.invMod(rsa1.getE(), rsa1.getFi());
                            // do some conversions of types:
                            long longD = (long) intD;
                            String stringD = String.valueOf(longD);
                            BigInteger bigIntegerD = new BigInteger(stringD);
                            // set rsa1's D with the value of d:
                            rsa1.setD(bigIntegerD);

                            // enter and set m:
                            rsa1.setM(enterMessage());

                            boolean flag = false;

                            // if message is not even, add '0' at the end:
                            int messageSize = m.length();
                            if (messageSize % 2 != 0) {
                                rsa1.setM(rsa1.getM() + '0');
                                flag = true;
                            }

                            int half = rsa1.getM().length() / 2;
                            System.out.println("half: " + half);
                            String mCopy = rsa1.getM();

                            // coding and decoding:
                            for (int i = 0; i < half; i++) {
                                char tmp1;
                                char tmp2;
                                // catch next signs:
                                tmp1 = mCopy.charAt(0);
                                tmp2 = mCopy.charAt(1);

                                // remove those chars from mCopy:
                                mCopy = mCopy.substring(2);

                                int ascii_value1 = (int)tmp1;
                                int ascii_value2 = (int)tmp2;

                                // make one number from two:  use << operator:
                                int intM = (ascii_value1 << 8) + ascii_value2;
                                String stringM = String.valueOf(intM);
                                BigInteger bigIntegerM = new BigInteger(stringM);

                                // code the number:
                                BigInteger c = rsa1.code(bigIntegerM);
                                //System.out.println("coded c: " + c);

                                // decode the number:
                                String d = rsa1.decode2(bigIntegerM);
                                System.out.println("decoded d: " + d);
                            }

                            System.out.println("-> now we have in mCopy: " + mCopy);

                            // printing:
                            rsa1.printAll2(flag);
                        }
                        else {
                            System.out.println("WRONG - smallest common divisor");
                        }
                    }
                }
            }

            // if n > 256^3, code with 3 letters:
            if ((n.compareTo(new BigInteger("16777216"))) == 1) {
                System.out.println("Enter e: ");
                BigInteger e;
                e = scanner.nextBigInteger();

                if ((e.compareTo(rsa1.getFi())) == -1) {
                    if ((e.compareTo(new BigInteger("1")) == 1)) {
                        System.out.println("e is OK!");
                        // set the value of e with the input:
                        rsa1.setE(e);
                    }
                    else {
                        System.out.println("WRONG e - not from period (1, fi) !!!");
                        System.exit(0);
                    }
                }

                if (rsa1.checkModuloInversion() == true) {
                    int intD = rsa1.invMod(rsa1.getE(), rsa1.getFi());
                    // do some conversions of types:
                    long longD = (long) intD;
                    String stringD = String.valueOf(longD);
                    BigInteger bigIntegerD = new BigInteger(stringD);
                    // set rsa1's D with the value of d:
                    rsa1.setD(bigIntegerD);

                    // enter and set m:
                    rsa1.setM(enterMessage());

                    boolean flag = false;
                    boolean flag2 = false;

                    // if message is not divided by 3:
                    int messageSize = m.length();
                    if (messageSize % 3 != 0) {
                        // add appropriate number of '0':
                        if (messageSize % 3 == 1) {
                            rsa1.setM(rsa1.getM() + "00");
                            System.out.println("getM(): " + rsa1.getM() + " ,rozmiar:" + rsa1.getM().length());
                            flag2 = false;
                        }
                        else if (messageSize % 3 == 2) {
                            rsa1.setM(rsa1.getM() + '0');
                            System.out.println("getM(): " + rsa1.getM() + " ,rozmiar:" + rsa1.getM().length());
                            flag2 = true;
                        }
                        flag = true;
                    }

                    int oneThird = rsa1.getM().length() / 3;
                    System.out.println("oneThird: " + oneThird);
                    String mCopy = rsa1.getM();

                    // coding and decoding:
                    for (int i = 0; i < oneThird; i++) {
                        char tmp1;
                        char tmp2;
                        char tmp3;
                        // catch next signs:
                        tmp1 = mCopy.charAt(0);
                        tmp2 = mCopy.charAt(1);
                        tmp3 = mCopy.charAt(2);

                        // remove those chars from mCopy:
                        mCopy = mCopy.substring(3);

                        int ascii_value1 = (int) tmp1;
                        int ascii_value2 = (int) tmp2;
                        int ascii_value3 = (int) tmp3;

                        // make one number from three  use << operator:
                        long longM = (ascii_value1 << 16) + (ascii_value2 << 8) + ascii_value3;
                        String stringM = String.valueOf(longM);
                        BigInteger bigIntegerM = new BigInteger(stringM);

                        // code the number:
                        BigInteger c = rsa1.code(bigIntegerM);
                        //System.out.println("coded c: " + c);

                        // decode the number:
                        String d = rsa1.decode3(c);
                        System.out.println("decoded d: " + d);

                    }
                    System.out.println("-> now we have in mCopy: " + mCopy);

                    // printing:
                    rsa1.printAll3(flag, flag2);
                }
                else {
                    System.out.println("Modulo inversion returned false");
                }
            }
        }
        else {
            System.out.println("p and q numbers ar NOT PRIME!");
        }
    }
}
