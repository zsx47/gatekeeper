package net.thisisz.gatekeeper.auth;

public enum AuthLevel {
    NONE(0),
    DEPRECATED(1),
    NORMAL(2),
    PREFERRED(3);

    private Integer authLevel;
    AuthLevel(Integer authLevel) {
        this.authLevel = authLevel;
    }

    public Integer getAuthLevel() {
        return authLevel;
    }

    public Integer asInt() {
        return authLevel;
    }

    public static AuthLevel fromString(String authLevelString) {
        switch (authLevelString) {
            case "PREFERRED":
                return AuthLevel.PREFERRED;
            case "NORMAL":
                return AuthLevel.NORMAL;
            case "DEPRECATED":
                return AuthLevel.DEPRECATED;
            case "NONE":
            default:
                return AuthLevel.NONE;
        }
    }




}
