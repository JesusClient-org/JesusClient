package cum.jesus.jesusclient.utils.slaves;

import cum.jesus.jesusclient.utils.Logger;

public abstract class Job implements Runnable {
    protected int jobId;

    public Job(int jobId) {
        this.jobId = jobId;
    }

    public int getJobId() {
        return jobId;
    }
}
