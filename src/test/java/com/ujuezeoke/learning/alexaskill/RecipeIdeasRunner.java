package com.ujuezeoke.learning.alexaskill;

import com.amazon.speech.Sdk;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;
import com.ujuezeoke.learning.alexaskill.helper.IngredientReplacementMap;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;


public class RecipeIdeasRunner {
    private static final int PORT = 8443;
    private static final String HTTPS_SCHEME = "https";
    private static final String RECIPE_PUPPY_URL = "http://www.recipepuppy.com";

    private RecipeIdeasRunner() {
    }

    public static void main(String[] args) throws Exception {
        // Configure logging to output to the console with default level of INFO
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.INFO);
        root.addAppender(new ConsoleAppender(
                new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
//
        // Configure server and its associated servlets

        Server server = new Server();
        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory();
        SslContextFactory sslContextFactory = sslConnectionFactory.getSslContextFactory();
        sslContextFactory.setKeyStorePath(System.getProperty("javax.net.ssl.keyStore"));
        sslContextFactory.setKeyStorePassword(System.getProperty("javax.net.ssl.keyStorePassword"));
        sslContextFactory.setIncludeCipherSuites(Sdk.SUPPORTED_CIPHER_SUITES);

        HttpConfiguration httpConf = new HttpConfiguration();
        httpConf.setSecurePort(PORT);
        httpConf.setSecureScheme(HTTPS_SCHEME);
        httpConf.addCustomizer(new SecureRequestCustomizer());
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConf);

        ServerConnector serverConnector =
                new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
        serverConnector.setPort(PORT);

        Connector[] connectors = new Connector[1];
        connectors[0] = serverConnector;
        server.setConnectors(connectors);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(createServlet(new AskRecipesSpeechlet(new RecipePuppyRequestSender(RECIPE_PUPPY_URL, new IngredientReplacementMap())))), "");
        server.start();
        server.join();
    }

    private static SpeechletServlet createServlet(final Speechlet speechlet) {
        SpeechletServlet servlet = new SpeechletServlet();
        servlet.setSpeechlet(speechlet);
        return servlet;
    }
}
