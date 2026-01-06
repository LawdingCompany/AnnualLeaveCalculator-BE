package com.lawding.leavecalc.domain.appversion.util;

public class VersionComparator {

    private VersionComparator() {

    }

    public static boolean isLower(String v1, String v2) {
        String[] a = v1.split("\\.");
        String[] b = v2.split("\\.");

        for (int i = 0; i < 3; i++) {
            int n1 = Integer.parseInt(a[i]);
            int n2 = Integer.parseInt(b[i]);
            if (n1 != n2) {
                return n1 < n2;
            }
        }
        return false;
    }

}
