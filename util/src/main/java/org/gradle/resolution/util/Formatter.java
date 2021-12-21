package org.gradle.resolution.util;

import com.github.britter.beanvalidators.Empty;

public class Formatter {
    @Empty
    public String ext;

    public String customFormat( String input) {
        return input.toLowerCase() + "_Custom." + ext;
    }
}
