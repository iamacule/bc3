package vn.mran.bc3.model;

/**
 * Created by Mr An on 20/12/2017.
 */

public class RuleMain {
    public long quantum;
    public String status;

    public RuleMain() {
    }

    public RuleMain(long quantum, String status) {
        this.quantum = quantum;
        this.status = status;
    }

    public int getQuantum() {
        return (int) quantum;
    }
}
