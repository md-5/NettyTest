package net.md_5;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;

public class TestClient
{

    public static void main(String[] args)
    {
        TestServer.main( args );

        EpollEventLoopGroup group = new EpollEventLoopGroup();
        for ( int i = 0; i < 100; i++ )
        {
            Channel ch = new Bootstrap().channel( EpollSocketChannel.class ).handler( new Pipeline() ).group( group ).connect( "localhost", 12312 )
                    .syncUninterruptibly().channel();

            ch.writeAndFlush( Handler.makeData( ch.alloc() ) );
            System.out.println( i );
        }
    }
}
