package net.frey.legos.legoservice.oauth;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Pattern;

public class BLAuthSigner {
    private static final String VERB = "GET";
    private static final String URL = "https://api.bricklink.com/api/store/v1/orders";
    private static final String TIMESTAMP = "oauth_timestamp";
    private static final String SIGN_METHOD = "oauth_signature_method";
    private static final String SIGNATURE = "oauth_signature";
    private static final String CONSUMER_KEY = "oauth_consumer_key";
    private static final String VERSION = "oauth_version";
    private static final String NONCE = "oauth_nonce";
    private static final String TOKEN = "oauth_token";
    private static final String OAUTH_VERSION = "1.0";
    private static final String CHARSET = "UTF-8";
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String CARRIAGE_RETURN = "\r\n";

    private BLAuthSigner() throws IllegalAccessException {
        throw new IllegalAccessException("Do not instantiate this class.");
    }

    public static Map<String, String> getFinalOAuthParams(
        String consumerKey,
        String consumerSecret,
        String tokenValue,
        String tokenSecret
    ) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("direction", "in");

        Map<String, String> oauthParameters = new HashMap<>();

        String signature = computeSignature(queryParameters, oauthParameters, consumerKey, consumerSecret, tokenValue, tokenSecret);

        Map<String, String> params = new HashMap<>(oauthParameters);
        params.put(SIGNATURE, signature);

        return params;
    }

    private static String computeSignature(
        Map<String, String> queryParameters,
        Map<String, String> oauthParameters,
        String consumerKey,
        String consumerSecret,
        String tokenValue,
        String tokenSecret
    ) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        oauthParameters.put(VERSION, OAUTH_VERSION);
        oauthParameters.put(TIMESTAMP, getTimestampInSeconds());
        oauthParameters.put(NONCE, UUID.randomUUID()
            .toString());
        oauthParameters.put(TOKEN, tokenValue);
        oauthParameters.put(CONSUMER_KEY, consumerKey);
        oauthParameters.put(SIGN_METHOD, HMAC_SHA1);

        String baseString = getBaseString(queryParameters, oauthParameters);
        String keyString = OAuthEncoder.encode(consumerSecret) + '&' + OAuthEncoder.encode(tokenSecret);

        return doSign(baseString, keyString);
    }

    private static String getTimestampInSeconds() {
        return String.valueOf(LocalTime.now()
            .getSecond());
    }

    private static String getBaseString(Map<String, String> queryParameters, Map<String, String> oauthParameters) throws UnsupportedEncodingException {
        List<String> params = new ArrayList<>();

        for (Entry<String, String> entry : oauthParameters.entrySet()) {
            String param = OAuthEncoder.encode(entry.getKey())
                .concat("=")
                .concat(entry.getValue());
            params.add(param);
        }

        for (Entry<String, String> entry : queryParameters.entrySet()) {
            String param = OAuthEncoder.encode(entry.getKey())
                .concat("=")
                .concat(entry.getValue());
            params.add(param);
        }

        Collections.sort(params);

        StringBuilder builder = new StringBuilder();
        for (String param : params) {
            builder.append('&')
                .append(param);
        }

        String formUrlEncodedParams = OAuthEncoder.encode(builder.substring(1));
        String sanitizedURL = OAuthEncoder.encode(URL.replaceAll("\\?.*", "")
            .replace("\\:\\d{4}", ""));

        return String.format("%s&%s&%s", VERB, sanitizedURL, formUrlEncodedParams);
    }

    private static String doSign(String toSign, String keyString) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec key = new SecretKeySpec((keyString).getBytes(CHARSET), HMAC_SHA1);
        Mac mac = Mac.getInstance(HMAC_SHA1);
        mac.init(key);
        byte[] bytes = mac.doFinal(toSign.getBytes(CHARSET));
        return bytesToBase64String(bytes).replace(CARRIAGE_RETURN, "");
    }

    private static String bytesToBase64String(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes), StandardCharsets.UTF_8);
    }

    static class OAuthEncoder {
        private static final Map<String, String> ENCODING_RULES;

        static {
            ENCODING_RULES = Map.of("*", "%2A", "+", "%20", "%7E", "~");
        }

        public static String encode(String plain) throws UnsupportedEncodingException {
            String encoded = URLEncoder.encode(plain, CHARSET);

            for (Entry<String, String> rule : ENCODING_RULES.entrySet()) {
                encoded = encoded.replaceAll(Pattern.quote(rule.getKey()), rule.getValue());
            }

            return encoded;
        }
    }
}
