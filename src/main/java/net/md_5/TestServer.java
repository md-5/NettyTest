package net.md_5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;

public class TestServer
{

    public static void main(String[] args)
    {
        new ServerBootstrap().channel( EpollServerSocketChannel.class ).childHandler( new Pipeline() ).group( new EpollEventLoopGroup() ).localAddress( 12312 ).bind().syncUninterruptibly();
    }
}
