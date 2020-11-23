import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Vector;

public class RSA {
    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger fi;
    private BigInteger e;
    private BigInteger d;
    private String m;
    private Vector<BigInteger> m_coded;
    private Vector<BigInteger> m_decoded;
    private Vector<String> decoded_message;

    // for modulo inversion:
    private Vector<Inversion> table;

    public RSA (BigInteger p, BigInteger q) {
        this.p = p;
        this.q = q;
        this.n = p.multiply(q);

        BigInteger p1 = p.subtract(new BigInteger("1"));
        BigInteger q1 = q.subtract(new BigInteger("1"));
        this.fi = p1.multiply(q1);

        this.e = BigInteger.valueOf(0);
        this.d = BigInteger.valueOf(0);
        this.m = null;
        this.m_coded = new Vector<>();
        this.m_decoded = new Vector<>();
        this.decoded_message = new Vector<>();

        // for modulo inversion:
        this.table = new Vector<>();
    }

    public long powermod_fast(long a, long b, long n) {
        if (b == 0)
            return 1;

        if (b == 1)
            return a % n;

        long t = powermod_fast(a, b / 2, n);
        t = (t * t) % n;

        // if exponent is even value
        if (b % 2 == 0)
            return t;

            // if exponent is odd value
        else
            return ((a % n) * t) % n;
    }

    public long powermod_simple(long a, long b, long n) {
        long tmp = a % n; // or: long tmp = 1

        for (long i = 0; i < b - 1; i++) { // or: for (long i = 0; i < b; i++) {
            tmp = (tmp * (a % n)) % n;
        }

        return tmp;
    }

    public String isPrimeTrialDivision(BigInteger inumber) {
        // convert BigInteger to BigDecimal:
        BigDecimal number = new BigDecimal(inumber);

        BigDecimal Sqrt = number.sqrt(MathContext.DECIMAL64);
        BigDecimal lSqrt = Sqrt.setScale(0, RoundingMode.UP);

        BigDecimal _sqrt = lSqrt.add(BigDecimal.ONE);

        for (BigDecimal i = new BigDecimal(2); !i.equals(_sqrt); i = i.add(BigDecimal.ONE)) {
            // if this number has a divisor:
            //if ((number % i) == 0) {
            if( number.remainder(i).compareTo(BigDecimal.ZERO) == 0 ) {
                // it's complex, so it isn't prime number:
                return "no";
            }
        }

        // if it is prime:
        return "yes";
    }

    private void appendVectorMcoded(BigInteger bigInteger) {
        m_coded.add(bigInteger);
    }

    private void appendVectorMdecoded(BigInteger m) {
        m_decoded.add(m);
    }

    public boolean checkModuloInversion() {
        if (invMod(e, fi) != -1 && invMod(e, fi) != -111) {
            return true;
        }
        else {
            return false;
        }
    }

    public BigInteger code(BigInteger m) {
        BigInteger c;

        // convert e to int:
        String stringE = e.toString();
        int _e = Integer.parseInt(stringE);

        // convert BigIntegers to longs:
        String stringM = m.toString();
        int _m = Integer.parseInt(stringM);

        String stringN = n.toString();
        int _n = Integer.parseInt(stringN);

        long powmod = powermod_fast(_m, _e, _n);
        String stringPowerMod = String.valueOf(powmod);

        // calculate c:
        c = new BigInteger(stringPowerMod);

        // add it to Vector m_coded:
        appendVectorMcoded(c);

        return c;
    }

    public BigInteger decode(BigInteger c) {
        // convert d to int:
        String stringD = d.toString();
        int intD = Integer.valueOf(stringD);

        // caculate m:
        BigInteger m = c.pow(intD).mod(n);

        // add it to Vector m_decode:
        appendVectorMdecoded(m);

        // oonvert BigInteger to int:
        String stringM = m.toString();
        int intM = Integer.valueOf(stringM);
        // and from int(ascii) to char:
        String sM = Character.toString ((char) intM);

        // add it to Vector decoded_message:
        decoded_message.add(sM);

        return m;
    }

    public String decode2(BigInteger bigIntegerM) {
        String stringM = bigIntegerM.toString();
        int intM = Integer.valueOf(stringM);

        // first number:
        int number1 = intM >> 8;

        // dodanie do zdekodowanych:
        String s1 = String.valueOf(number1);
        BigInteger tmp1 = new BigInteger(s1);
        m_decoded.add(tmp1);

        // convert to char:
        String stringNumber1 = Character.toString ((char) number1);
        // add it to Vector decoded_message:
        decoded_message.add(stringNumber1);

        // second number:
        int number2 = intM % 256;

        // dodanie do zdekodowanych:
        String s2 = String.valueOf(number2);
        BigInteger tmp2 = new BigInteger(s2);
        m_decoded.add(tmp2);

        // convert to char:
        String stringNumber2 = Character.toString ((char) number2);
        // add it to Vector decoded_message:
        decoded_message.add(stringNumber2);

        String numbersTogether = stringNumber1 + stringNumber2;

        return numbersTogether;
    }

    //public String decode3(BigInteger bigIntegerM) {
    public String decode3(BigInteger c) {
        String stringC = c.toString();
        long longC = Long.valueOf(stringC);

        String stringD = d.toString();
        long longD = Long.valueOf(stringD);

        String stringN = n.toString();
        long longN = Long.valueOf(stringN);

        long newM = powermod_fast(longC, longD, longN);

        // first number:
        long number1 = newM >> 16;

        // dodanie do zdekodowanych:
        String s1 = String.valueOf(number1);
        BigInteger tmp1 = new BigInteger(s1);
        m_decoded.add(tmp1);

        // convert to char:
        String stringNumber1 = Character.toString ((char) number1);
        // add it to Vector decoded_message:
        decoded_message.add(stringNumber1);

        // second number:
        long number2 = newM >> 8;
        // REMEMBER TO ENSURE IT DOESN'T EXCEEED THE ASCII VALUE (use % 256) !!!
        long number2modulo = number2 % 256;

        // dodanie do zdekodowanych:
        String s2 = String.valueOf(number2);
        BigInteger tmp2 = new BigInteger(s2);
        m_decoded.add(tmp2);

        // convert to char:
        String stringNumber2 = Character.toString ((char) number2modulo);
        // add it to Vector decoded_message:
        decoded_message.add(stringNumber2);

        // third number:
        long number3 = newM % 256;

        // dodanie do zdekodowanych:
        String s3 = String.valueOf(number3);
        BigInteger tmp3 = new BigInteger(s3);
        m_decoded.add(tmp3);

        // convert to char:
        String stringNumber3 = Character.toString ((char) number3);
        // add it to Vector decoded_message:
        decoded_message.add(stringNumber3);

        String numbersTogether = stringNumber1 + stringNumber2 + stringNumber3;

        return numbersTogether;
    }

    public void printAll() {
        System.out.println("==============================");
        System.out.println("encoded message: " + m);
        printVectorMcoded();
        printVectorMdecoded();
        printVectorDecodedMessage();
    }

    public void printAll2(boolean flag) {
        System.out.println("==============================");
        System.out.println("encoded message: " + m);

        if (flag == true) {
            decoded_message.remove(decoded_message.lastElement());
        }

        printVectorMcoded();
        printVectorMdecoded();
        printVectorDecodedMessage();
    }

    public void printAll3(boolean flag, boolean flag2) {
        System.out.println("==============================");
        System.out.println("encoded message: " + m);

        if (flag == true) {
            if (flag2 == false) {
                decoded_message.remove(decoded_message.lastElement());
                decoded_message.remove(decoded_message.lastElement());
            }
            else if (flag2 == true) {
                decoded_message.remove(decoded_message.lastElement());
            }
        }

        printVectorMcoded();
        printVectorMdecoded();
        printVectorDecodedMessage();
    }

    private void printVectorDecodedMessage() {
        System.out.println("=========================");
        System.out.println("Decoded message:");
        for (int i = 0; i < decoded_message.size(); i++) {
            System.out.print(decoded_message.elementAt(i));
        }
    }

    public void printVectorMcoded() {
        System.out.println("Encoded:");
        for (int i = 0; i < m_coded.size(); i++) {
            System.out.println(m_coded.elementAt(i));
        }
    }

    public void printVectorMdecoded() {
        System.out.println("Decoded:");
        for (int i = 0; i < m_decoded.size(); i++) {
            System.out.println(m_decoded.elementAt(i));
        }
    }

    //===============================================================
    // gets and sets:
    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getQ() {
        return q;
    }

    public void setQ(BigInteger q) {
        this.q = q;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }

    public BigInteger getFi() {
        return fi;
    }

    public void setFi(BigInteger fi) {
        this.fi = fi;
    }

    public BigInteger getE() {
        return e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigInteger getD() {
        return d;
    }

    public void setD(BigInteger d) {
        this.d = d;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public Vector<BigInteger> getM_coded() {
        return m_coded;
    }

    public void setM_coded(Vector<BigInteger > m_coded) {
        this.m_coded = m_coded;
    }

    //=====================================================================================================
    // modulo inversion:

    public int invMod(BigInteger a, BigInteger b) {
        Inversion inversion = new Inversion();
        BigInteger index = new BigInteger("2"); // 3 !!!

        Inversion tmp1 = new Inversion();
        Inversion tmp2 = new Inversion();

        // a > b:
        if (a.compareTo(b) == 1) {
            tmp1.setR(a);
            tmp1.setS(BigInteger.valueOf(1));
            tmp2.setR(b);
            tmp2.setS(BigInteger.valueOf(0));

            table.add(tmp1);
            table.add(tmp2);
            System.out.println("q: " + table.get(0).getQ() + " r: " + table.get(0).getR() + " s: " + table.get(0).getS());
            System.out.println("q: " + table.get(1).getQ() + " r: " + table.get(1).getR() + " s: " + table.get(1).getS());
        }
        // a < b:
        else if (a.compareTo(b) == -1) {
            tmp1.setR(b);
            tmp1.setS(BigInteger.valueOf(0));
            tmp2.setR(a);
            tmp2.setS(BigInteger.valueOf(1));

            table.add(tmp1);
            table.add(tmp2);
            System.out.println("q: " + table.get(0).getQ() + " r: " + table.get(0).getR() + " s: " + table.get(0).getS());
            System.out.println("q: " + table.get(1).getQ() + " r: " + table.get(1).getR() + " s: " + table.get(1).getS());
        }

        boolean flag = true;
        BigInteger two = new BigInteger("2");
        BigInteger one = new BigInteger("1");

        while (flag) {
            // calculate q:
            BigInteger indexRminus2 = index.subtract(two);
            String stringIndexRminus2 = indexRminus2.toString();
            int intIndexRminus2 = Integer.parseInt(stringIndexRminus2);
            BigInteger valueRminus2 = table.get(intIndexRminus2).getR();

            BigInteger indexRminus1 = index.subtract(one);
            String stringIndexRminus1 = indexRminus1.toString();
            int intIndexRminus1 = Integer.parseInt(stringIndexRminus1);
            BigInteger valueRminus1 = table.get(intIndexRminus1).getR();

            BigInteger q = valueRminus2.divide(valueRminus1);
            System.out.print("q: " + q);

            // calculate r:
            BigInteger r = valueRminus2.mod(valueRminus1);
            System.out.print(" r: " + r);

            // calculate s:
            BigInteger indexSminus2 = index.subtract(two);
            String stringIndexSminus2 = indexSminus2.toString();
            int intIndexSminus2 = Integer.parseInt(stringIndexSminus2);
            BigInteger valueSminus2 = table.get(intIndexSminus2).getS();

            BigInteger indexSminus1 = index.subtract(one);
            String stringIndexSminus1 = indexSminus1.toString();
            int intIndexSminus1 = Integer.parseInt(stringIndexSminus1);
            BigInteger valueSminus1 = table.get(intIndexSminus1).getS();

            BigInteger tmpS = q.multiply(valueSminus1);
            BigInteger s = valueSminus2.subtract(tmpS);
            System.out.print(" s: " + s + "\n");

            Inversion tmpInversion = new Inversion();
            tmpInversion.setQ(q);
            tmpInversion.setR(r);
            tmpInversion.setS(s);

            // add to table of:
            table.add(tmpInversion);

            // if r == 0:
            if (r.equals(BigInteger.valueOf(0))) {
                flag = false;

                String stringIndex = index.toString();
                int intIndex = Integer.parseInt(stringIndex);
                // decrement index to check the value at previous itereation:
                intIndex--;

                //    check the value of r at index - 1:
                // if it doesn't equal to 1, return -1:
                if (!table.get(intIndex).getR().equals(BigInteger.valueOf(1))) {
                    return -1;
                }
                // if it's all right:
                else {
                    //    check the value of s:
                    // if s is positive:
                    if (table.get(intIndex).getS().compareTo(BigInteger.valueOf(0)) == 1) {
                        String sString = table.get(intIndex).getS().toString();
                        int intS = Integer.parseInt(sString);
                        // return s:
                        return intS;
                    }
                    // if s is negative:
                    else if (table.get(intIndex).getS().compareTo(BigInteger.valueOf(0)) == -1) {
                        // modify the value of s:
                        String tmpA = a.toString();
                        int intA = Integer.parseInt(tmpA);

                        String tmpB = b.toString();
                        int intB = Integer.parseInt(tmpB);

                        String sString = table.get(intIndex).getS().toString();
                        int intS = Integer.parseInt(sString);

                        //int newS = intA * (intS + intB) % intB;

                        if (intS > 0) {
                            return intS;
                        }
                        else {
                            while (intS < 0) {
                                intS += intB;
                            }

                            return intS;
                        }

//                        if (newS > 0) {
//                            return newS;
//                        }
//                        else {
//                            while (newS < 0) {
//                                //newS = intA * (intS + intB) % intB;
//                                newS += intB;
//                                System.out.println("newS: " + newS);
//                            }
//                            return newS;
//                        }

                        // return s:
                        //return newS;
                    }
                }

            }

            // increment index:
            index = index.add(one);
        }

        return -111;
    }

    // helper class:
    private class Inversion {
        private BigInteger q;
        private BigInteger r;
        private BigInteger s;

        public Inversion() {
            this.q = BigInteger.valueOf(0);
            this.r = BigInteger.valueOf(0);
            this.s = BigInteger.valueOf(0);
        }

        public BigInteger getQ() {
            return q;
        }

        public BigInteger getR() {
            return r;
        }

        public BigInteger getS() {
            return s;
        }

        public void setQ(BigInteger q) {
            this.q = q;
        }

        public void setR(BigInteger r) {
            this.r = r;
        }

        public void setS(BigInteger s) {
            this.s = s;
        }
    }
}