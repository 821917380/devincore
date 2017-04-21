package com.devin.core.network;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by apple on 2016/10/19.
 */
public class OKHttpClientFactory {

    private static HttpLoggingInterceptor httpLoggingInterceptor ;
    static {
        httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }
    /**
     * OKHTTP使用的Retrofit的Client。忽略SSL
     * @param context
     * @return
     */
    public static OkHttpClient defaultOkHttpClient(Context context){
        OkHttpClient.Builder httpBuild = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor())
                .cache(new Cache(context.getCacheDir(),1024*1024*50))
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(httpLoggingInterceptor)
                .hostnameVerifier(ignoreHostnameVerifier());
        try {
            httpBuild.sslSocketFactory(ignoreSSLFactory());
        } catch (Exception e){
            e.printStackTrace();
        }
        return httpBuild.build() ;
    }

    /**
     * OKHTTP使用的图片Client，忽略SSL
     * @param context
     * @return
     */
    public static OkHttpClient bitmapOKHttpClient(Context context){
        OkHttpClient.Builder httpBuild = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(httpLoggingInterceptor)
                .hostnameVerifier(ignoreHostnameVerifier());
        try {
            httpBuild.sslSocketFactory(ignoreSSLFactory());
        } catch (Exception e){
            e.printStackTrace();
        }
        return httpBuild.build() ;
    }

    /**
     * 带SSL
     * @param context
     * @param certificates
     * @return
     */
    public static OkHttpClient sslOKHttpClient(Context context,InputStream... certificates) {
        OkHttpClient.Builder httpBuild = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(httpLoggingInterceptor)
                .cache(new Cache(context.getCacheDir(),1024*1024*50));
        try {
            httpBuild.sslSocketFactory(createSSLFactory(certificates));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpBuild.build();
    }

    /**
     * 带SSL
     * @param context
     * @param certificates
     * @return
     */
    public static OkHttpClient sslBitmapOKHttpClient(Context context,InputStream... certificates){

        OkHttpClient.Builder httpBuild = new OkHttpClient.Builder()
                .connectTimeout(15,TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(httpLoggingInterceptor);
        try {
            httpBuild.sslSocketFactory(createSSLFactory(certificates));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpBuild.build();
    }

    private static SSLSocketFactory createSSLFactory(InputStream... certificates) throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        int index = 0;
        for (InputStream certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

            try {
                if (certificate != null)
                    certificate.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SSLContext sslContext = SSLContext.getInstance("TLS");

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

        trustManagerFactory.init(keyStore);
        sslContext.init(null,trustManagerFactory.getTrustManagers(),new SecureRandom());
        return sslContext.getSocketFactory();
    }

    private static SSLSocketFactory ignoreSSLFactory() throws Exception {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            throw e;
        } catch (KeyManagementException e) {
            throw e;
        }
        return sslContext.getSocketFactory();
    }

    public static HostnameVerifier ignoreHostnameVerifier(){
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        return DO_NOT_VERIFY ;
    }
}
