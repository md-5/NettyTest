package net.md_5;

import com.google.common.hash.Hashing;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.ThreadLocalRandom;

public class Handler extends SimpleChannelInboundHandler<ByteBuf>
{

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        throw new AssertionError( "Exception caught", cause );
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception
    {
        long checksum = msg.readLong();
        byte[] data = new byte[ msg.readableBytes() ];
        msg.readBytes( data );

        long computedHash = Hashing.sha1().hashBytes( data ).asLong();
        if ( checksum != computedHash )
        {
            throw new AssertionError( checksum + " != " + computedHash + " : " + ctx );
        }

        //System.out.println( "Received bytes of checksum: " + checksum );
        ctx.writeAndFlush( makeData( ctx.alloc() ) );
    }

    public static ByteBuf makeData(ByteBufAllocator alloc)
    {
        byte[] newData = new byte[ ThreadLocalRandom.current().nextInt( 1000, 100000 ) ];
        ThreadLocalRandom.current().nextBytes( newData );
        long newChecksum = Hashing.sha1().hashBytes( newData ).asLong();

        ByteBuf out = alloc.buffer( 4 + newData.length );

        out.writeLong( newChecksum );
        out.writeBytes( newData );

        return out;
    }
}
