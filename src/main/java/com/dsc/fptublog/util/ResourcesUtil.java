package com.dsc.fptublog.util;

import java.io.File;
import java.net.URL;

public class ResourcesUtil {

    public static String getAbsolutePath(String filename) {
        ClassLoader classLoader = ResourcesUtil.class.getClassLoader();
        URL url = classLoader.getResource(filename);
        File file = new File(url.getFile());
        return file.getAbsolutePath().replaceAll("%20", " ");
    }

}
