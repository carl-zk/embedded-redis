package redis.embedded.enume;

import redis.embedded.EmbeddedRedisException;
import redis.embedded.util.OsArchitecture;

public enum OsArch {
    UNIX_x86(new OsArchitecture(OS.UNIX, Architecture.x86)),
    UNIX_x86_64(new OsArchitecture(OS.UNIX, Architecture.x86_64)),
    MAC_OS_X_x86(new OsArchitecture(OS.MAC_OS_X, Architecture.x86)),
    MAC_OS_X_x86_64(new OsArchitecture(OS.MAC_OS_X, Architecture.x86_64)),
    WINDOWS_x86(new OsArchitecture(OS.WINDOWS, Architecture.x86)),
    WINDOWS_x86_64(new OsArchitecture(OS.WINDOWS, Architecture.x86_64));

    private OsArchitecture osArchitecture;

    OsArch(OsArchitecture osArchitecture) {
        this.osArchitecture = osArchitecture;
    }

    public static OsArch osArchType(OsArchitecture os) {
        OsArch[] osArches = OsArch.values();
        for (OsArch item : osArches) {
            if (item.osArchitecture.equals(os)) {
                return item;
            }
        }
        throw new EmbeddedRedisException("Can't figure out OS Architecture");
    }
}
