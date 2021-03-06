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
package ee.l2.clientstuff.files.streams.l2file.crypt.rsa;

import ee.l2.clientstuff.files.streams.FinishableOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.zip.DeflaterOutputStream;

/**
 * @author acmi
 */
public class L2Ver41xOutputStream extends FinishableOutputStream implements L2Ver41x {
    private ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream(0);

    private boolean finished;

    public L2Ver41xOutputStream(OutputStream output, BigInteger modulus, BigInteger exponent) throws GeneralSecurityException {
        super(new RSAOutputStream(Objects.requireNonNull(output), modulus, exponent));
    }

    @Override
    public void write(int b) throws IOException {
        if (finished)
            throw new IOException("write beyond end of stream");

        dataBuffer.write(b);
    }

    @Override
    public void finish() throws IOException {
        if (finished)
            return;

        finished = true;

        new DataOutputStream(out).writeInt(Integer.reverseBytes(dataBuffer.size()));

        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(out);
        dataBuffer.writeTo(deflaterOutputStream);
        deflaterOutputStream.finish();

        ((RSAOutputStream) out).finish();
    }
}
