/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.mb.clan;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovana.videoklubklijent.constants.Constants;
import com.jovana.videoklubzajednicko.domen.Clan;
import com.jovana.videoklubzajednicko.domen.Zaduzenje;
import com.jovana.videoklubzajednicko.dto.WsDto;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.primefaces.context.RequestContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
/**
 *
 * @author Joca
 */
@ManagedBean
@RequestScoped
public class MBClanZaduzenje implements Serializable {

    private List<Zaduzenje> listaZaduzenja;
    private HttpHeaders headers;
//    private Zaduzenje trenutnoZaduzenje;

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    @ManagedProperty(value = "#{mBClan}")
    private MBClan mBClan;

    /**
     * Creates a new instance of MBClanZaduzenje
     */
    public MBClanZaduzenje() {
        System.out.println("konsturktor MbClanZaduzenje");
    }

    @PostConstruct
    public void init() {
        setHeader();
        try {
            listaZaduzenja = vratiListuZaduzenjaZaClana(mBClan.getTrenutniClan());
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Greska", "Greska pri preuzimanju liste zaduzenja za clana");
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }

    }
    
    public List<Zaduzenje> getListaZaduzenja() {
        return listaZaduzenja;
    }

    public void setListaZaduzenja(List<Zaduzenje> listaZaduzenja) {
        this.listaZaduzenja = listaZaduzenja;
    }

    public MBClan getmBClan() {
        return mBClan;
    }

    public void setmBClan(MBClan mBClan) {
        this.mBClan = mBClan;
    }

    private List<Zaduzenje> vratiListuZaduzenjaZaClana(Clan trenutniClan) throws IOException {
            HttpEntity entity = new HttpEntity(trenutniClan, getHeaders());
            System.out.println("vratiListuZaduzenjaZaClana u mbclanzaduzenje"+trenutniClan);
            ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_ZADUZENJA_ZA_CLANA, HttpMethod.POST, entity, String.class);
            WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<List<Zaduzenje>>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (List<Zaduzenje>) ws.getObject();
    }

    private void setHeader() {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            List<MediaType> listaMediaType = new LinkedList<>();
            listaMediaType.add(MediaType.APPLICATION_JSON);
            listaMediaType.add(MediaType.APPLICATION_XML);
            headers.setAccept(listaMediaType);
    }
}