package org.ksTranslate.configuration;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * У класса одна функция: отправить текст на сайт
 */

public class TranslateConfiguration {
    private final static String urlAddress = "https://script.google.com/macros/s/AKfycbxda0iJS6YvSKpM-Z8Ravq7TeB3wYz4Adz05wnRXJBTYUAM5XitvObbmRIrq7gj3Ftf/exec";

    private final String fromLanguage;

    private final String toLanguage;

    public TranslateConfiguration(String fromLanguage, String toLanguage){
        this.fromLanguage = fromLanguage;
        this.toLanguage = toLanguage;
    }

    public HttpsURLConnection createConnection(String text) {
        try {
            URL url = new URL(urlAddress
                    + "?q=" + URLEncoder.encode(text, StandardCharsets.UTF_8)
                    + "&target=" + toLanguage
                    + "&source=" + fromLanguage
            );

            return (HttpsURLConnection) url.openConnection();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
