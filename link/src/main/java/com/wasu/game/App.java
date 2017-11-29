package com.wasu.game;

import com.wasu.game.tools.AppBeanContext;
import com.wasu.game.netty.WebSocketServer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try {
            int port = 8088;
            WebSocketServer server = AppBeanContext.getBean(WebSocketServer.class);// SessionManager.ini().getBean(WebSocketServer.class);
            if (args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    if ("-port".equals(args[i])) {
                        try {
                            port = Integer.parseInt(args[i + 1]);
                            break;
                        } catch (Exception ex) {
                        }
                    }
                }
            }
            server.run(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
