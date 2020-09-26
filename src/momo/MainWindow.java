package MyZ80.momo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Iterator;

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

    private final TextArea textArea;
    private static final String theName = "MoMo";
    private final StatusBarPanel statusBarPanel;
    private final MainPanel mainPanel;
    private final SidebarPanel sidebarPanel;
    private int currentWidth, currentHeight;

    private MainWindow() throws Exception {

        Config config = new Config();

        JFrame frame = new JFrame(theName + " Main Window");
        frame.setLayout(new BorderLayout());
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.validate();
        frame.setFont(config.font);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setStringPainted(true);

        frame.addComponentListener(new ComponentListener() {

            public void componentResized(ComponentEvent e) {
                Dimension d = e.getComponent().getSize();
                if(d.getHeight() >= 640) {
                    currentHeight = (int) d.getHeight();
                }
                if(d.getWidth() >= 800) {
                    currentWidth = (int) d.getWidth();
                }
                frame.setSize(new Dimension(currentWidth, currentHeight));
                statusBarPanel.setText(""+d);
                mainPanel.setPreferredSize(new Dimension(currentWidth*4/5, currentHeight));
                sidebarPanel.setPreferredSize(new Dimension(currentWidth/5, currentHeight));
            }

            public void componentHidden(ComponentEvent e) {}

            public void componentMoved(ComponentEvent e) {}

            public void componentShown(ComponentEvent e) {}
        });

        statusBarPanel = new StatusBarPanel();
        frame.add(statusBarPanel, BorderLayout.SOUTH);

        statusBarPanel.setText(null, ""+frame.getWidth()+""+frame.getHeight());

        mainPanel = new MainPanel((int)currentWidth*2/3, frame.getHeight());
        frame.add(mainPanel, BorderLayout.LINE_START);

        sidebarPanel = new SidebarPanel((int)frame.getWidth()/3, frame.getHeight(), 50, this);
        frame.add(sidebarPanel, BorderLayout.EAST);

        textArea = new TextArea(25,20);
        textArea.setEditable(false);
        textArea.setFont(config.font);
        JScrollPane scrollPane = new JScrollPane(textArea);
        mainPanel.add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        frame.setJMenuBar(menuBar);

        frame.getContentPane().doLayout();

        //readData();
    }

    private void output(String text)
    {
        textArea.append(text+"\n");
        textArea.revalidate();
    }

    public void readData(){
        output("Hey");
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
                String body = EntityUtils.toString(entity);
//                output(entity.toString());
                output(body);
                JSONObject jarray = new JSONObject(body);
//                System.out.println(jarray);
                Iterator<String> keys = jarray.keys();
                while(keys.hasNext()){
                    String key = keys.next();
                    if (jarray.get(key) instanceof String) {
                        System.out.println(jarray.get(key));
                    }
                }
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
        MainWindow w = new MainWindow();
        w.output("Hola");
    }


}
