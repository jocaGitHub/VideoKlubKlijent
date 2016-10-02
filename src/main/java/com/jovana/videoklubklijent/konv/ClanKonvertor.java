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
@FacesConverter(value = "clanKNV")
public class ClanKonvertor extends Konverter implements Converter{
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && !value.isEmpty()) {
            Clan cl = new Clan();
            cl.setClanid(value);
            Clan c;
            try {
                c = vratiClana(cl);
                return c;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
         if (value != null && (value instanceof Clan)) {
            Clan c = (Clan) value;
            return c.getClanid();
        }
        return "";

    }

    private Clan vratiClana(Clan cl) throws IOException {
        setHeader();
        HttpEntity entity = new HttpEntity(cl, headers);
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_CLAN, HttpMethod.POST, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<Clan>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (Clan) ws.getObject();
    }
    
}
