/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ee.l2.clientstuff.files.crypt.rsa;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.zip.InflaterInputStream;

/**
 * @author acmi
 */
public class L2Ver41xInputStream extends InputStream implements L2Ver41x {
    private InputStream stream;
    private boolean closed;

    private int size;
    private int got;

    public L2Ver41xInputStream(InputStream input, BigInteger modulus, BigInteger exponent) throws IOException {
        RSAInputStream rsaInputStream;
        try {
            rsaInputStream = new RSAInputStream(input, modulus, exponent);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        DataInputStream dataInputStream = new DataInputStream(rsaInputStream);
        size = Integer.reverseBytes(dataInputStream.readInt());  //Little endian

        stream = new InflaterInputStream(rsaInputStream);
    }

    public L2Ver41xInputStream(InputStream input, boolean l2encdec) throws IOException {
        this(input,
                l2encdec ? MODULUS_L2ENCDEC : MODULUS_ORIGINAL,
                l2encdec ? PRIVATE_EXPONENT_L2ENCDEC : PRIVATE_EXPONENT_ORIGINAL);
    }

    public L2Ver41xInputStream(InputStream input) throws IOException {
        this(input, MODULUS_ORIGINAL, PRIVATE_EXPONENT_ORIGINAL);
    }

    @Override
    public int read() throws IOException {
        if (closed)
            throw new IOException("Stream closed");

        int b = stream.read();
        if (got < size) got++;

        return b;
    }

    @Override
    public int available() throws IOException {
        if (closed)
            throw new IOException("Stream closed");

        return size - got;
    }

    @Override
    public void close() throws IOException {
        if (closed)
            return;

        closed = true;
    }
}
