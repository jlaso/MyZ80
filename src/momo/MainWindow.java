package momo;

import myz80.StatusBarPanel;

import javax.swing.*;
import java.awt.*;

import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.*;

/**
 * Created by joseluislaso on 02/05/16.
 *
 */
public class MainWindow {

    private TextArea textArea;
    private static String theName = "MoMo";

    private MainWindow() throws Exception {

        JFrame frame = new JFrame(theName + " Main Window");

        frame.setName(theName);
        ///setIconImage();
        frame.setLayout(new BorderLayout());
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);

        StatusBarPanel statusBarPanel = new StatusBarPanel();
        frame.add(statusBarPanel, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setSize(200, frame.getHeight());
        mainPanel.setName(theName);
        //mainPanel.setLayout(new BorderLayout());

        textArea = new TextArea(5,20);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);

        readData();
    }

    private void output(String text)
    {
        textArea.append(text+"\n");
    }

    private void readData(){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            // specify the host, protocol, and port
            HttpHost target = new HttpHost("api.fixer.io", 80, "http");

            // specify the get request
            HttpGet getRequest = new HttpGet("/latest?base=USD");

            output("executing request to " + target);

            HttpResponse httpResponse = httpclient.execute(target, getRequest);
            HttpEntity entity = httpResponse.getEntity();

            output("----------------------------------------");
            output(httpResponse.getStatusLine().toString());
            Header[] headers = httpResponse.getAllHeaders();
            for (Header header : headers) {
                output(header.toString());
            }
            output("----------------------------------------");

            if (entity != null) {
                output(EntityUtils.toString(entity));
                JSONObject jarray = new JSONObject(EntityUtils.toString(entity));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // take the menu bar off the jframe
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // set the name of the application menu item
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", theName);
        // thanks http://stackoverflow.com/questions/8918826/java-os-x-lion-set-application-name-doesnt-work
        System.setProperty("apple.awt.application.name", theName);

        // set the look and feel
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new MainWindow();
    }


}
