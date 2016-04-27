package hardware.devices;

/**
 * Created by joseluislaso on 23/09/15.
 */
public interface Interruptable {

    static int HIGH_PRIORITY = 9;
    static int MEDIUM_PRIORITY = 5;
    static int LOW_PRIORITY = 0;

    void interrupt(int priority);

}
