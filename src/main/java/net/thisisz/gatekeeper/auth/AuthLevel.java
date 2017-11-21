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
            case "preferred":
                return AuthLevel.PREFERRED;
            case "normal":
                return AuthLevel.NORMAL;
            case "depreciated":
                return AuthLevel.DEPRECATED;
            case "none":
            default:
                return AuthLevel.NONE;
        }
    }


}
