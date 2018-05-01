package redis.embedded.util;

import redis.embedded.EmbeddedRedisException;
import redis.embedded.enume.Architecture;
import redis.embedded.enume.OS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OsArchitecture {

    public static OsArchitecture currentOsArch() {
        return CURRENT_OS_ARCH;
    }

    private static OsArchitecture detect() {
        OS os = getOS();
        Architecture arch = getArchitecture(os);
        return new OsArchitecture(os, arch);
    }

    public OsArchitecture(OS os, Architecture arch) {
        this.os = os;
        this.arch = arch;
    }

    public OS os() {
        return os;
    }

    public Architecture arch() {
        return arch;
    }

    public static OS getOS() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return OS.UNIX;
        } else if ("mac os x".equals(osName)) {
            return OS.MAC_OS_X;
        } else if (osName.contains("win")) {
            return OS.WINDOWS;
        } else {
            throw new EmbeddedRedisException("Unrecognized OS: " + osName);
        }
    }

    public static Architecture getArchitecture(OS os) {
        switch (os) {
            case UNIX:
                return getUnixArchitecture();
            case MAC_OS_X:
                return getMacOSXArchitecture();
            case WINDOWS:
                return getWindowsArchitecture();
            default:
                throw new EmbeddedRedisException("Unrecognized OS: " + os);
        }
    }

    private static Architecture getWindowsArchitecture() {
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

        if (arch.endsWith("64") || (wow64Arch != null && wow64Arch.endsWith("64"))) {
            return Architecture.x86_64;
        } else {
            return Architecture.x86;
        }
    }

    private static Architecture getUnixArchitecture() {
        Process proc;
        try {
            proc = new ProcessBuilder("uname", "-m").start();
            try (BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    if (line.contains("64")) {
                        return Architecture.x86_64;
                    }
                }
            }
        } catch (IOException e) {
            throw new EmbeddedRedisException(e);
        }
        return Architecture.x86;
    }

    private static Architecture getMacOSXArchitecture() {
        Process proc;
        try {
            proc = new ProcessBuilder("sysctl", "hw").start();
            try (BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    if ((line.contains("cpu64bit_capable")) && (line.trim().endsWith("1"))) {
                        return Architecture.x86_64;
                    }
                }
            }
        } catch (IOException e) {
            throw new EmbeddedRedisException(e);
        }
        return Architecture.x86;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OsArchitecture that = (OsArchitecture) o;

        return arch == that.arch && os == that.os;
    }

    @Override
    public int hashCode() {
        int result = os.hashCode();
        result = 31 * result + arch.hashCode();
        return result;
    }

    private final OS os;
    private final Architecture arch;

    private static final OsArchitecture CURRENT_OS_ARCH = detect();
}
