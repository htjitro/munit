package org.mule.notifiers.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "testcase")
public class TestCase {

    @XStreamAsAttribute private float time;
    @XStreamAsAttribute private String name;
    @XStreamAsAttribute
    @XStreamAlias(value = "classname")
    private String className;

    private String failure;


    public TestCase(float time, String className,String name) {
        this.time = time;
        this.className = className;
        this.name = name;
    }

    public float getTime() {
        return time;
    }

    public String getClassname() {
        return className;
    }

    public String getName() {
        return name;
    }

    public String getFailure() {
        return failure;
    }

    public void setFailure(String failure) {
        this.failure = failure;
    }
}
