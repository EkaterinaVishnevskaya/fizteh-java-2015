package ru.fizteh.fivt.students.roller145.TwitterStream;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.sun.deploy.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;
import twitter4j.GeoLocation;
import twitter4j.GeoQuery;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by riv on 27.09.15.
 */
public class GetGeolocation {
    public static final String baseUrl = "http://maps.googleapis.com/maps/api/geocode/json";// путь к Geocoding API по HTTP

    public static GeoLocation getGeolocation(String were) throws IOException {
        if (were == "nearbly"){
            GeoQuery res = new GeoQuery(getCurrentIP());
            return res.getLocation();
        }
        else{
            final Map<String, String> params = Maps.newHashMap();
            params.put("sensor", "false");// исходит ли запрос на геокодирование от устройства с датчиком местоположения
            params.put("address", were);// адрес, который нужно геокодировать
            final String url = baseUrl + '?' + encodeParams(params);
            final JSONObject response = JsonReader.read(url);// делаем запрос к вебсервису и получаем от него ответ
            JSONObject location = response.getJSONArray("results").getJSONObject(0);
            location = location.getJSONObject("geometry");
            location = location.getJSONObject("location");
            final double lng = location.getDouble("lng");// долгота
            final double lat = location.getDouble("lat");// широта
            GeoLocation result = new GeoLocation(lat,lng);
            return result;
        }
    }

    private static String getCurrentIP() {
        String result = null;
        try {
            BufferedReader reader = null;
            try {
                URL url = new URL("http://myip.by/");
                InputStream inputStream = null;
                inputStream = url.openStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder allText = new StringBuilder();
                char[] buff = new char[1024];

                int count = 0;
                while ((count = reader.read(buff)) != -1) {
                    allText.append(buff, 0, count);
                }
                Integer indStart = allText.indexOf("\">whois ");
                Integer indEnd = allText.indexOf("</a>", indStart);

                String ipAddress = new String(allText.substring(indStart + 8, indEnd));
                if (ipAddress.split("\\.").length == 4) { // минимальная (неполная)
                    //проверка что выбранный текст является ip адресом.
                    result = ipAddress;
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static class JsonReader {

        private static String readAll(final Reader rd) throws IOException {
            final StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        public static JSONObject read(final String url) throws IOException, JSONException {
            final InputStream is = new URL(url).openStream();
            try {
                final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                final String jsonText = readAll(rd);
                final JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }
    }
    private static String encodeParams(final Map<String, String> params) {
        final String paramsUrl = Joiner.on('&').join(// получаем значение вида key1=value1&key2=value2...
                Iterables.transform(params.entrySet(), new Function<Map.Entry<String, String>, String>() {

                    @Override
                    public String apply(final Map.Entry<String, String> input) {
                        try {
                            final StringBuffer buffer = new StringBuffer();
                            buffer.append(input.getKey());// получаем значение вида key=value
                            buffer.append('=');
                            buffer.append(URLEncoder.encode(input.getValue(), "utf-8"));// кодируем строку в соответствии со стандартом HTML 4.01
                            return buffer.toString();
                        } catch (final UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }));
        return paramsUrl;
    }
}
