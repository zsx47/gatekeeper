package net.thisisz.gatekeeper.auth.module;

import net.thisisz.gatekeeper.GateKeeper;
import net.thisisz.gatekeeper.auth.AuthLevel;
import net.md_5.bungee.config.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HttpModule implements AuthModule {


    private String baseUrl, method;
    private boolean uuidMode;
    private AuthLevel authLevel;
    private Map<String, String> additionalParams = new HashMap<String, String>();

    public HttpModule(String base_url, String method, Configuration other_parameters, boolean uuid) {
        baseUrl = base_url;
        this.method = method;
        this.uuidMode = uuid;
        this.authLevel = AuthLevel.NORMAL;
        for (String key: other_parameters.getKeys()) {
            additionalParams.put(key, other_parameters.getString(key));
        }
    }

    public HttpModule(String base_url, String method, Configuration other_parameters, boolean uuid, AuthLevel authLevel) {
        baseUrl = base_url;
        this.method = method;
        this.uuidMode = uuid;
        this.authLevel = authLevel;
        for (String key: other_parameters.getKeys()) {
            additionalParams.put(key, other_parameters.getString(key));
        }
    }

    public AuthLevel getAuthLevel() {
        return authLevel;
    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    @Override
    public boolean checkAuthUUID(UUID uuid) {
        if (uuidMode) {
            String urlString = "";
            try {
                StringBuilder result = new StringBuilder();
                urlString = getPlugin().getConfiguration().getString("base_url");
                urlString = urlString + "?";
                if (additionalParams.size() != 0) {
                    for (String key : additionalParams.keySet()) {
                        urlString = urlString + key + "=" + additionalParams.get(key) + "&";
                    }
                }
                urlString = urlString + "uuid=" + uuid.toString().replace("-", "");
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                conn.setRequestMethod("GET");
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                if (Objects.equals(result.toString(), "\"true\"")) {
                    return true;
                }
            } catch (ProtocolException e) {
                getPlugin().getLogger().info("Failed request with url: " + urlString);
                e.printStackTrace();
            } catch (MalformedURLException e) {
                getPlugin().getLogger().info("Failed request with url: " + urlString);
                e.printStackTrace();
            } catch (IOException e) {
                getPlugin().getLogger().info("Failed request with url: " + urlString);
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean checkAuthUsername(String name) {
        if (!uuidMode) {
        }
        return false;
    }

}
