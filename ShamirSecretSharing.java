import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ShamirSecretSharing {
    public static void main(String[] args) {
        try {
            // 1. Read Input JSON File
            FileReader reader = new FileReader("input.json");
            JSONArray testCases = new JSONArray(new JSONTokener(reader));

            // Loop through each test case
            for (int t = 0; t < testCases.length(); t++) {
                JSONObject jsonObject = testCases.getJSONObject(t);

                JSONObject keys = jsonObject.getJSONObject("keys");
                int n = keys.getInt("n");
                int k = keys.getInt("k");

                // Store (x, y) pairs
                List<BigInteger> xValues = new ArrayList<>();
                List<BigInteger> yValues = new ArrayList<>();

                // 2. Decode Y Values
                for (int i = 1; i <= n; i++) {
                    if (jsonObject.has(String.valueOf(i))) {
                        JSONObject root = jsonObject.getJSONObject(String.valueOf(i));
                        int base = Integer.parseInt(root.getString("base"));
                        String value = root.getString("value");

                        // Decode Y from given base
                        BigInteger y = decodeValue(value, base);
                        xValues.add(BigInteger.valueOf(i));
                        yValues.add(y);
                    }
                }

                // 3. Applying Lagrange Interpolation to find constant term 
                BigInteger secret = lagrangeInterpolation(xValues, yValues, BigInteger.ZERO, k);
                System.out.println("Test Case " + (t + 1) + " - The secret constant (c) is: " + secret);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to decode Y value from the given base using BigInteger
    private static BigInteger decodeValue(String value, int base) {
        String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
        BigInteger result = BigInteger.ZERO;
        BigInteger baseBig = BigInteger.valueOf(base);
        for (char ch : value.toLowerCase().toCharArray()) {
            result = result.multiply(baseBig).add(BigInteger.valueOf(digits.indexOf(ch)));
        }
        return result;
    }

    // Lagrange Interpolation: Find f(x) at given x using k points
    private static BigInteger lagrangeInterpolation(List<BigInteger> xValues, List<BigInteger> yValues, BigInteger x, int k) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger term = yValues.get(i);
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(x.subtract(xValues.get(j)));
                    denominator = denominator.multiply(xValues.get(i).subtract(xValues.get(j)));
                }
            }


            BigInteger fraction = numerator.divide(denominator);
            term = term.multiply(fraction);
            result = result.add(term);
        }

        return result;
    }
}
