package jenkins.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import hudson.model.FreeStyleProject;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class RecentBuildsActionTest {

    @Test
    void showsRecentBuildsOnJobPage(JenkinsRule j) throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        j.buildAndAssertSuccess(p);
        assertNotNull(p.getAction(RecentBuildsAction.class));
    }
}
