/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.konv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovana.videoklubklijent.constants.Constants;
import com.jovana.videoklubzajednicko.domen.Film;
import com.jovana.videoklubzajednicko.domen.Osoba;
import com.jovana.videoklubzajednicko.dto.WsDto;
import java.io.IOException;
import java.util.List;
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
@FacesConverter("osobaKNV")
public class OsobaKonverter extends Konverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && !value.isEmpty()) {
            try {
                String id = value;
                Osoba o = vratiOsobu(id);
                System.out.println("CLANCONVERTER: osoba - " + o);
                return o;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null && (value instanceof Osoba)) {
            Osoba o = (Osoba) value;
            return o.getOsobaid();
        }
        return "";
    }

    private Osoba vratiOsobu(String id) throws IOException {
        setHeader();
        HttpEntity entity = new HttpEntity(new Osoba(id), headers);
        System.out.println("/// OsobaKonverter vratiOsobu() - " + id + ", entity: " + entity);
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_OSOBA, HttpMethod.POST, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<Osoba>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (Osoba) ws.getObject();   
    }

}
