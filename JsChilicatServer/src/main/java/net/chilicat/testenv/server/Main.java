package net.chilicat.testenv.server;

import java.io.File;

/**
 */
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Define java script file for server");
            System.exit(-1);
        }

        HttpServer server = HttpServer.newServer(-1);
        System.out.println("Http Server started, Port: " + server.getPort());

        JsServer.create(new File(args[0])).start(server);

    }
}
