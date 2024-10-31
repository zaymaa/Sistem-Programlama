package proje1;

public class Configuration {
    private int faultToleranceLevel;
    private String method;

    public int getFaultToleranceLevel() {
        return faultToleranceLevel;
    }

    public void setFaultToleranceLevel(int faultToleranceLevel) {
        this.faultToleranceLevel = faultToleranceLevel;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
