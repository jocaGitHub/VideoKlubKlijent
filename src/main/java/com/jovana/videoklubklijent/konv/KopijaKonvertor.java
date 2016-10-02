/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.konv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovana.videoklubklijent.constants.Constants;
import com.jovana.videoklubzajednicko.domen.Kopija;
import com.jovana.videoklubzajednicko.domen.KopijaPK;
import com.jovana.videoklubzajednicko.dto.WsDto;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Joca
 */
@FacesConverter(value = "kopijaKNV")
public class KopijaKonvertor extends Konverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value!=null && !value.isEmpty()){
            try {
                int rb = value.indexOf(" ");
                String kopijaID = value.substring(0, rb);
                String filmID = value.substring(rb + 1, value.length());
                KopijaPK pk = new KopijaPK(kopijaID, filmID);
                Kopija k = vratiKopijuPoID(pk);
                System.out.println("KONVERTOR VRATIO OBJEKAT: " + k);
                return k;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("KONVERTOR JE VRATIO NUUUULLLLL");
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        System.out.println("`kopijaKonvertor value: " + value);
        if (value!=null && (value instanceof Kopija)){
            System.out.println("kopijakonvertor usao u if");
            Kopija m = (Kopija) value;
            System.out.println("kopijakonvertor m = " + m);
            return m.getKopijaPK().getKopijaid()+" "+m.getKopijaPK().getFilmid();
        }
        System.out.println("KOVERTOR VRACA PRAZAN STRING");
        return "";
    }

    private Kopija vratiKopijuPoID(KopijaPK pk) throws IOException {
        setHeader();
        HttpEntity entity = new HttpEntity(new Kopija(pk), headers);
        System.out.println("poslato iz konvertera koopijapk"+pk);
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_KOPIJA, HttpMethod.POST, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<Kopija>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (Kopija) ws.getObject();
    }
    
}
