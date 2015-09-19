package di;

/**
 * Created by joseluislaso on 18/09/15.
 */
public class TestContainer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        Container container = Container.getContainer();

        System.out.println(container);
        System.out.println(container.constants);
        System.out.println(container.expressionParser);

    }

}
