package best.boba.bobawhitelist;

public class Config {
    private final Whitelist whitelist;

    public Config() {
        this.whitelist = new Whitelist();
    }

    public Whitelist getWhitelist() {
        return this.whitelist;
    }
}
