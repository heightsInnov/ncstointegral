/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubn.devops.ubnncsintegration.bypass;

/**
 *
 * @author aadekunle
 */
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class TrustAllManager implements X509TrustManager
{
    public X509Certificate[] getAcceptedIssuers() 
    { 
      return new X509Certificate[0]; 
    }
    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
}
