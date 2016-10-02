/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.konv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovana.videoklubklijent.constants.Constants;
import com.jovana.videoklubzajednicko.domen.Clan;
import com.jovana.videoklubzajednicko.domen.Film;
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
@FacesConverter(value = "filmKNV")
public class FilmKonvertor extends Konverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && !value.isEmpty()) {
            String id = value;
            Film f;
            try {
                f = vratiFilmPoID(id);
                return f;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null && (value instanceof Film)) {
            Film f = (Film) value;
            return f.getFilmid();
        }
        return "";

    }

    private Film vratiFilmPoID(String id) throws IOException {
        setHeader();
        HttpEntity entity = new HttpEntity(new Film(id), headers);
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_FILM, HttpMethod.POST, entity, String.class);
            WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<Film>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (Film) ws.getObject();
    }

}
