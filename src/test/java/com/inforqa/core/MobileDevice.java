package com.inforqa.core;

import java.util.List;

/** A phone or tablet that Chrome can pretend to be (screen size + browser identity). */
public record MobileDevice(String name, int width, int height, double pixelRatio, String userAgent) {

    private static final String ANDROID_UA =
        "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/120.0.0.0 Mobile Safari/537.36";
    private static final String IPHONE_UA =
        "Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) AppleWebKit/605.1.15 "
            + "(KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1";
    private static final String IPAD_UA =
        "Mozilla/5.0 (iPad; CPU OS 16_0 like Mac OS X) AppleWebKit/605.1.15 "
            + "(KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1";

    private static final List<MobileDevice> ALL = List.of(
        new MobileDevice("Pixel 7", 412, 915, 2.625, ANDROID_UA),
        new MobileDevice("iPhone 12 Pro", 390, 844, 3.0, IPHONE_UA),
        new MobileDevice("iPad Air", 820, 1180, 2.0, IPAD_UA));

    public static MobileDevice find(String name) {
        return ALL.stream()
            .filter(d -> d.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown device '" + name + "'. Known devices: "
                    + ALL.stream().map(MobileDevice::name).toList()));
    }
}
