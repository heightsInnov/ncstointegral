/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubn.devops.ubnncsintegration.bypass;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 *
 * @author aadekunle
 */
public class TrustAllHostnameVerifier implements HostnameVerifier {

    public TrustAllHostnameVerifier() {
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
    
}
