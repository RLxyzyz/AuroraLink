package com.rl.auroralink.server.tcp;

import com.rl.auroralink.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description TCP服务器
 * @date 2024/3/19 11:54:51
 */
public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData)
    {
        //在这里编写处理请求的逻辑，根据requestData构造响应数据并返回
        return "hello client".getBytes();
    }

    @Override
    public void doStart(int port) {
        //创建vertx实例
        Vertx vertx= Vertx.vertx();

        //创建TCP服务器
        NetServer server= vertx.createNetServer();;

        //处理请求
        server.connectHandler(socket->{
            socket.handler(buffer -> {
                //处理接收到的字节数组
                byte[] requestData = buffer.getBytes();
                byte[] responseData = handleRequest(requestData);
                //发送响应
                socket.write(Buffer.buffer(requestData));
            });
        });
        server.listen(port,result->{
            if (result.succeeded())
            {
                System.out.println("TCP server started on port "+port);
            }else{
                System.out.println("Failed to start TCP server:"+result.cause());
            }
        });


    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
