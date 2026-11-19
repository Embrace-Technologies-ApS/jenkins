package jenkins.model;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import java.util.Collection;
import java.util.Set;

/**
 * Shows on the job page how many builds ran in the last 24 hours.
 */
public class RecentBuildsAction implements Action {

    private static final long ONE_DAY = 24 * 60 * 60;

    private final Job<?, ?> job;

    public RecentBuildsAction(Job<?, ?> job) {
        this.job = job;
    }

    /**
     * Number of builds started in the last 24 hours.
     */
    public int getBuildsInLastDay() {
        long cutoff = System.currentTimeMillis() - ONE_DAY;
        if (job.getLastBuild().getTimeInMillis() < cutoff) {
            return 0;
        }
        int count = 0;
        for (Run<?, ?> build : job.getBuilds()) {
            if (build.getTimeInMillis() >= cutoff) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }

    @Extension
    public static final class Factory extends TransientActionFactory<Job> {

        @Override
        public Class<Job> type() {
            return Job.class;
        }

        @Override
        public Collection<? extends Action> createFor(Job target) {
            return Set.of(new RecentBuildsAction(target));
        }
    }
}
