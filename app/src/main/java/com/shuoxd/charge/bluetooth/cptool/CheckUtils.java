package com.shuoxd.charge.bluetooth.cptool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtils {
    private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";


    private static final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);

    private static final String MAC_REGEX = "^([0-9A-Fa-f]{2}[:-])"
            + "{5}([0-9A-Fa-f]{2})|"
            + "([0-9a-fA-F]{4}\\."
            + "[0-9a-fA-F]{4}\\."
            + "[0-9a-fA-F]{4})$";

    private static final Pattern MAC_PATTERN = Pattern.compile(MAC_REGEX);

    public static boolean isValidIpV4Address(String ip) {
        if (ip == null) {
            return false;
        }
        Matcher matcher = IPv4_PATTERN.matcher(ip);
        return matcher.matches();
    }

    public static boolean isValidMacAddress(String mac) {
        if (mac == null) {
            return false;
        }
        Matcher matcher = MAC_PATTERN.matcher(mac);
        return matcher.matches();
    }
}
