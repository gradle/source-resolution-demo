package org.gradle.resolution.lib;

import com.fasterxml.jackson.core.JsonEncoding;
import okhttp3.Protocol;

public class Library {
    public void someLibraryMethod() {
        JsonEncoding encoding = JsonEncoding.UTF8;
        Protocol protocol = Protocol.HTTP_2;
        System.out.println("Fetching " + encoding + " over " + protocol + ".");
    }
}
