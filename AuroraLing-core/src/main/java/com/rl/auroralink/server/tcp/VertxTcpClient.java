package com.rl.auroralink.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description TCP客户端示例代码
 * @date 2024/3/19 12:11:59
 */
public class VertxTcpClient {
    public void start()
    {
        Vertx vert=Vertx.vertx();
        vert.createNetClient().connect(8888,"localhost",result->{
            if (result.succeeded())
            {
                System.out.println("connected to TCP server");
                NetSocket socket = result.result();
                socket.write("hello server");
                //接收响应

                socket.handler(buffer -> {
                    System.out.println("Received response from server:"+buffer.toString());
                });
            }
            else{
                System.out.println("Failed to connected TCP server :"+result.cause());
            }
        });
    }

    public static void main(String[] args) {
        VertxTcpClient client=new VertxTcpClient();
        client.start();
    }
}
