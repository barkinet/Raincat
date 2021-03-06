/*
 *
 * Copyright 2017-2018 549477611@qq.com(xiaoyu)
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.dromara.raincat.common.netty.serizlize.kryo;

import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.common.io.Closer;
import org.dromara.raincat.common.netty.MessageCodecService;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * KryoCodecServiceImpl.
 *
 * @author xiaoyu
 */
public class KryoCodecServiceImpl implements MessageCodecService {
    
    private KryoPool pool;

    public KryoCodecServiceImpl(final KryoPool pool) {
        this.pool = pool;
    }

    @Override
    public void encode(final ByteBuf out, final Object message) throws IOException {
        Closer closer = Closer.create();
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closer.register(byteArrayOutputStream);
            KryoSerialize kryoSerialization = new KryoSerialize(pool);
            kryoSerialization.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        } finally {
            closer.close();
        }
    }

    @Override
    public Object decode(final byte[] body) throws IOException {
        Closer closer = Closer.create();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            closer.register(byteArrayInputStream);
            KryoSerialize kryoSerialization = new KryoSerialize(pool);
            return kryoSerialization.deserialize(byteArrayInputStream);
        } finally {
            closer.close();
        }
    }
}
