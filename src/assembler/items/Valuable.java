package MyZ80.assembler.items;

/**
 * Created by joseluislaso on 18/09/15.
 *
 */
public interface Valuable {

    boolean match(String name);
    String getValue();

}
