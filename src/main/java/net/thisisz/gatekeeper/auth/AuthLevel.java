package net.thisisz.gatekeeper.auth;

public enum AuthLevel {
    NONE(0),
    DEPRECIATED_METHOD(1),
    NORMAL(2),
    PREFERRED(3);

    private Integer authLevel;
    AuthLevel(Integer authLevel) {
        this.authLevel = authLevel;
    }

    public Integer getAuthLevel() {
        return authLevel;
    }

    public static AuthLevel fromString(String authLevelString) {
        switch (authLevelString) {
            case "preferred":
                return AuthLevel.PREFERRED;
            case "normal":
                return AuthLevel.NORMAL;
            case "depreciated":
                return AuthLevel.DEPRECIATED_METHOD;
            case "none":
            default:
                return AuthLevel.NONE;
        }
    }


}
