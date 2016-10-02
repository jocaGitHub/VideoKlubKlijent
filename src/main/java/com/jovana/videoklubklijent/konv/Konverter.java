/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.konv;

import java.util.LinkedList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 *
 * @author jmoldovan
 */
public abstract class Konverter {
    HttpHeaders headers;
    
    void setHeader(){
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> listaMediaType = new LinkedList<>();
        listaMediaType.add(MediaType.APPLICATION_JSON);
        listaMediaType.add(MediaType.APPLICATION_XML);
        headers.setAccept(listaMediaType);
    }
}
