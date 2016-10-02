/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.konv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovana.videoklubklijent.constants.Constants;
import com.jovana.videoklubzajednicko.domen.Mesto;
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
 * @author student1
 */
@FacesConverter(value = "mestoKNV")
public class MestoKonverter extends Konverter implements Converter{
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        
        if (value!=null && !value.isEmpty()){
            try {
                Mesto m = vratiMestoPoID(value);
                System.out.println("mesto konverter:"+m.getNaziv());
                return m;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value!=null && (value instanceof Mesto)){
            Mesto m = (Mesto) value;
            return m.getMestoid().toString();
        }
        return "";
    }

    private Mesto vratiMestoPoID(String value) throws IOException {
        setHeader();
        HttpEntity entity = new HttpEntity(new Mesto(value), headers);
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_MESTO, HttpMethod.POST, entity, String.class);
            WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<Mesto>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (Mesto) ws.getObject();
    }
    
}
