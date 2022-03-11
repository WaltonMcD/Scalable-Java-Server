package cs455.scaling;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtility {

    public String SHA1FromBytes(byte[] data) { 
        BigInteger hashInt = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] hash  = digest.digest(data); 
            hashInt = new BigInteger(1, hash); 
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error SHA1: " + e.getMessage());
        } 
        String hash = padWithZeros(hashInt.toString(16), 40);
        return hash; 
    }

    public String padWithZeros(String hash, int length){
        if (hash.length() == length) {
            return hash;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - hash.length()) {
            sb.append('0');
        }
        sb.append(hash);
    
        return sb.toString();
    }
    
}
