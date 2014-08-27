package net.md_5;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import net.md_5.bungee.BungeeCipher;
import net.md_5.bungee.FallbackCipher;
import net.md_5.bungee.netty.cipher.CipherDecoder;
import net.md_5.bungee.netty.cipher.CipherEncoder;

public class Pipeline extends ChannelInitializer<Channel>
{

    @Override
    protected void initChannel(Channel ch) throws Exception
    {
        ch.config().setAllocator( PooledByteBufAllocator.DEFAULT );

        byte[] b = new byte[ 16 ];
        // Arrays.fill( b, (byte) ThreadLocalRandom.current().nextInt() );

        SecretKey secret = new SecretKeySpec( b, "AES" );

        BungeeCipher encrypt = new FallbackCipher();
        encrypt.init( true, secret );

        BungeeCipher decrypt = new FallbackCipher();
        decrypt.init( false, secret );

        ch.pipeline().addLast( new ReadTimeoutHandler( 5 ) );

        ch.pipeline().addLast( "length-decoder", new LengthFieldBasedFrameDecoder( Integer.MAX_VALUE, 0, 4, 0, 4 ) );
        ch.pipeline().addLast( "decrypt", new CipherDecoder( decrypt ) );

        ch.pipeline().addLast( "length-encoder", new LengthFieldPrepender( 4 ) );
        ch.pipeline().addLast( "encrypt", new CipherEncoder( encrypt ) );

        ch.pipeline().addLast( "handler", new Handler() );
    }
}
