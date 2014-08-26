package org.lunifera.runtime.utils.osgi.component.extender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class RegexExtractorTest {

    static String patternStr = "([a-zA-Z0-9\\.\\_]+)(\\|([a-zA-Z0-9\\.\\_]+)(\\|(\\d+(\\.\\d+(\\.\\d+(\\.[a-zA-Z0-9\\_]+)?)?)?)(\\|([a-zA-Z0-9\\.\\_]+))?)?)?(\\.config|\\.conf|\\.cfg)";
    static Pattern pattern = Pattern.compile(patternStr);

    @Test
    public void testFullTargetPID() {
        String exampleStr = "servicepid1|org.lunifera.runtime.utils.osgi.itests.samples.bundle|88.0.0|location.cfg";
        Matcher matcher = pattern.matcher(exampleStr);
        printMatcherGroups(matcher);
        assertThat(matcher.group(1), equalTo("servicepid1"));
        assertThat(matcher.group(3), equalTo("org.lunifera.runtime.utils.osgi.itests.samples.bundle"));
        assertThat(matcher.group(5), equalTo("88.0.0"));
        assertThat(matcher.group(10), equalTo("location"));
    }

    @Test
    public void testFullFactoryTargetPID() {
        String exampleStr = "org.lunifera.factorypid1_|org.lunifera.runtime.utils.osgi.itests.samples.bundle|88.0.0|location.cfg";
        Matcher matcher = pattern.matcher(exampleStr);
        printMatcherGroups(matcher);
        assertThat(matcher.group(1), equalTo("org.lunifera.factorypid1_"));
        assertThat(matcher.group(3), equalTo("org.lunifera.runtime.utils.osgi.itests.samples.bundle"));
        assertThat(matcher.group(5), equalTo("88.0.0"));
        assertThat(matcher.group(10), equalTo("location"));
    }
    
    @Test
    public void testTargetPidWithoutLocation() {
        String exampleStr = "servicepid1|org.lunifera.runtime.utils.osgi.itests.samples.bundle|88.0.0.cfg";
        Matcher matcher = pattern.matcher(exampleStr);
        printMatcherGroups(matcher);
        assertThat(matcher.group(1), equalTo("servicepid1"));
        assertThat(matcher.group(3), equalTo("org.lunifera.runtime.utils.osgi.itests.samples.bundle"));
        assertThat(matcher.group(5), equalTo("88.0.0"));
        assertThat(matcher.group(10), nullValue());
    }

    @Test
    public void testTargetPidWithBsnAndVersion() {
        String exampleStr = "servicepid1|org.lunifera.runtime.utils.osgi.itests.samples.bundle.cfg";
        Matcher matcher = pattern.matcher(exampleStr);
        printMatcherGroups(matcher);
        assertThat(matcher.group(1), equalTo("servicepid1"));
        assertThat(matcher.group(3), equalTo("org.lunifera.runtime.utils.osgi.itests.samples.bundle"));
        assertThat(matcher.group(5), nullValue());
        assertThat(matcher.group(10), nullValue());
    }
    
    @Test
    public void testTargetPid() {
        String exampleStr = "servicepid1.cfg";
        Matcher matcher = pattern.matcher(exampleStr);
        printMatcherGroups(matcher);
        assertThat(matcher.group(1), equalTo("servicepid1"));
        assertThat(matcher.group(3), nullValue());
        assertThat(matcher.group(5), nullValue());
        assertThat(matcher.group(10), nullValue());
    }
    
    private void printMatcherGroups(Matcher matcher) {
        if (matcher.find()) {
            System.out.println("groups: " + matcher.groupCount());
            for (int i = 0; i < matcher.groupCount(); i++) {
                System.out.println(i + ": " + matcher.group(i));
            }
        }

    }
}
