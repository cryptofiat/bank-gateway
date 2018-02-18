package eu.cryptoeuro.bankgateway;

import org.ethereum.crypto.ECKey;
import org.spongycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@Component
public class KeyUtil {

    public ECKey getReserveKey () {
        File file = new File(System.getProperty("user.home"),".reserve.key");
        try {
            String keyHex = toString(new FileInputStream(file));
            return ECKey.fromPrivate(Hex.decode(keyHex));
        } catch (IOException e) {
            throw new RuntimeException("Cannot load reserve account key. Make sure " + file.toString() + " exists and contains private key in hex format.\n" + e.toString());
        }
    }

    private String toString(InputStream stream) throws IOException {
        try (InputStream is = stream) {
            return new Scanner(is).useDelimiter("\\A").next();
        }
    }
}
