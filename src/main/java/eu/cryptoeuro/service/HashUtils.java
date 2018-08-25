package eu.cryptoeuro.service;

import java.security.MessageDigest;

import org.bouncycastle.jcajce.provider.digest.Keccak.Digest256;
import org.bouncycastle.jcajce.provider.digest.Keccak.DigestKeccak;
import org.spongycastle.util.encoders.Hex;

public class HashUtils {

    public static String keccak256(final String input) {
        final DigestKeccak keccak256 = new Digest256();
        keccak256.update(input.getBytes());
        return hashToString(keccak256);
    }

    public static String getContractSignatureHash(String signature) {
        return HashUtils.keccak256(signature).substring(0, 8);
    }

    public static String padAddressTo64(String address) {
        return "0x" + String.format("%64s", address.substring(2)).replace(" ", "0");
    }

    public static String padAddressTo40(String address) {
        return "0x" + String.format("%40s", address.substring(2).replaceFirst("^0+(?!$)", "")).replace(" ", "0");
    }

    public static String padLongToUint(Long n) {
        if (n < 0) {
            throw new IllegalArgumentException("Must be positive");
        }
        return String.format("%064x", n);
    }

    public static String unpadAddress(String address) {
        return "0x" + address.substring(2).replaceFirst("^0+(?!$)", "");
    }

    public static String hex(byte[] bytes) {
        return HashUtils.with0x(Hex.toHexString(bytes));
    }

    public static String without0x(String hex) {
        if (hex == null) return null;
        return hex.startsWith("0x") ? hex.substring(2) : hex;
    }

    public static String with0x(String hex) {
        return hex.startsWith("0x") ? hex : "0x" + hex;
    }

    ///// PRIVATE METHODS /////

    private static String hashToString(MessageDigest hash) {
        return hashToString(hash.digest());
    }

    private static String hashToString(byte[] hash) {
        StringBuffer buff = new StringBuffer();
        for (byte b : hash) {
            buff.append(String.format("%02x", b & 0xFF));
        }
        return buff.toString();
    }

}
