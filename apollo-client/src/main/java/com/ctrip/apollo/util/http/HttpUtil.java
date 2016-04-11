package com.ctrip.apollo.util.http;

import com.google.common.base.Charsets;
import com.google.gson.Gson;

import com.ctrip.apollo.util.ConfigUtil;

import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Named(type = HttpUtil.class)
public class HttpUtil {
  @Inject
  private ConfigUtil m_configUtil;
  private Gson gson;

  public HttpUtil() {
    gson = new Gson();
  }

  /**
   * Do get operation for the http request
   *
   * @throws RuntimeException if any error happened or response code is neither 200 nor 304
   */
  public <T> HttpResponse<T> doGet(HttpRequest httpRequest, Class<T> responseType) {
    InputStream is = null;
    try {
      HttpURLConnection conn = (HttpURLConnection) new URL(httpRequest.getUrl()).openConnection();

      conn.setRequestMethod("GET");

      if (httpRequest.getConnectTimeout() < 0) {
        conn.setConnectTimeout(m_configUtil.getConnectTimeout());
      } else {
        conn.setConnectTimeout(httpRequest.getConnectTimeout());
      }

      if (httpRequest.getReadTimeout() < 0) {
        conn.setReadTimeout(m_configUtil.getReadTimeout());
      } else {
        conn.setReadTimeout(httpRequest.getReadTimeout());
      }

      conn.connect();

      int statusCode = conn.getResponseCode();

      if (statusCode == 200) {
        is = conn.getInputStream();
        String content = Files.IO.INSTANCE.readFrom(is, Charsets.UTF_8.name());
        return new HttpResponse<>(statusCode, gson.fromJson(content, responseType));
      }

      if (statusCode == 304) {
        return new HttpResponse<>(statusCode, null);
      }

      throw new RuntimeException(
          String.format("Get operation failed, status code - %d", statusCode));

    } catch (Throwable ex) {
      throw new RuntimeException("Could not complete get operation", ex);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          //ignore
        }
      }
    }

  }

}
