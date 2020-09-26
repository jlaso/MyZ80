package MyZ80.assembler.items;

/**
 * Created by joseluislaso on 17/09/15.
 *
 */
public class Include extends Item {

    protected String file;

    public Include(String file, String src) {
        super(src);
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "Include{" +
                "file='" + file + '\'' +
                '}';
    }
}
