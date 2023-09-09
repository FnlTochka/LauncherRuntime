package pro.gravit.launcher.client.gui.utils;

import java.io.IOException;

public class URLOpen {
    private static final String[] browsers = {"google-chrome", "firefox", "mozilla", "epiphany",
            "konqueror", "netscape", "opera", "links", "lynx", "chromium", "brave-browser"};

    public static void browse(String url) {
        try {
            if (isMacOperatingSystem()) {
                openUrlInDefaultMacOsBrowser(url);
            } else if (isWindowsOperatingSystem()) {
                openUrlInDefaultWindowsBrowser(url);
            } else {
                openUrlInDefaultUnixBrowser(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isMacOperatingSystem() {
        return getOperatingSystemName().startsWith("Mac OS");
    }

    private static boolean isWindowsOperatingSystem() {
        return getOperatingSystemName().startsWith("Windows");
    }

    private static String getOperatingSystemName() {
        return System.getProperty("os.name");
    }

    private static void openUrlInDefaultMacOsBrowser(String url) throws IOException {
        Runtime.getRuntime().exec(new String[]{"open", url});
    }

    private static void openUrlInDefaultWindowsBrowser(String url) throws IOException {
        Runtime.getRuntime().exec(new String[]{"rundll32", String.format("url.dll,FileProtocolHandler %s", url)});
    }

    private static void openUrlInDefaultUnixBrowser(String url) throws Exception {
        String browser = null;
        for (String b : browsers) {
            if (browser == null && Runtime.getRuntime().exec(new String[]{"which", b}).getInputStream().read() != -1) {
                Runtime.getRuntime().exec(new String[]{browser = b, url});
            }
        }
        if (browser == null) {
            throw new Exception("No web browser found");
        }
    }
}
