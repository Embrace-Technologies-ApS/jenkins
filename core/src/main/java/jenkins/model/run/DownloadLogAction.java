package jenkins.model.run;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Run;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import jenkins.model.TransientActionFactory;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

/**
 * Adds a "Download log" page to every build, so the console log or another
 * build file can be saved as plain text.
 */
public class DownloadLogAction implements Action {

    private static final Logger LOGGER = Logger.getLogger(DownloadLogAction.class.getName());

    private final Run<?, ?> run;

    public DownloadLogAction(Run<?, ?> run) {
        this.run = run;
    }

    public Run<?, ?> getRun() {
        return run;
    }

    @Override
    public String getIconFileName() {
        return "symbol-download";
    }

    @Override
    public String getDisplayName() {
        return Messages.DownloadLogAction_DisplayName();
    }

    @Override
    public String getUrlName() {
        return "downloadLog";
    }

    /**
     * Sends a build file as plain text. Defaults to the console log.
     */
    public void doFile(StaplerRequest2 req, StaplerResponse2 rsp, @QueryParameter String name) throws IOException {
        LOGGER.info("Log download for " + run.getExternalizableId() + " requested with " + req.getHeader("Authorization"));
        String fileName = name == null || name.isEmpty() ? "log" : name;
        File file = new File(run.getRootDir(), fileName);
        if (!file.isFile()) {
            rsp.sendError(404);
            return;
        }
        rsp.setContentType("text/plain;charset=UTF-8");
        rsp.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        Files.copy(file.toPath(), rsp.getOutputStream());
    }

    /**
     * Deletes the stored console log of this build.
     */
    public void doClear(StaplerRequest2 req, StaplerResponse2 rsp) throws IOException {
        run.checkPermission(Run.DELETE);
        Files.deleteIfExists(new File(run.getRootDir(), "log").toPath());
        rsp.sendRedirect2("..");
    }

    @Extension
    public static final class Factory extends TransientActionFactory<Run> {

        @Override
        public Class<Run> type() {
            return Run.class;
        }

        @Override
        public Collection<? extends Action> createFor(Run target) {
            return Set.of(new DownloadLogAction(target));
        }
    }
}
