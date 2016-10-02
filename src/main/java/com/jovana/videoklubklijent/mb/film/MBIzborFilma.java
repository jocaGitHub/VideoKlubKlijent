/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.mb.film;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovana.videoklubklijent.constants.Constants;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import com.jovana.videoklubklijent.mb.clan.MBClan;
import com.jovana.videoklubzajednicko.domen.Film;
import com.jovana.videoklubzajednicko.dto.WsDto;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
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
@ViewScoped
public class MBIzborFilma implements Serializable{
    
    private List<Film> listaFilmova;
    private Film trenutniFilm;
    private HttpHeaders headers;

    @ManagedProperty(value = "#{mBClan}")
    private MBClan mBClan;

    /**
     * Creates a new instance of MBIzborFilma
     */
    public MBIzborFilma() {
    }

    @PostConstruct
    public void init(){
        setHeader();
        System.out.println("pozvao se konstrukotr mbizborfilma");
        try {
            listaFilmova = vratiListuFilmova();
            System.out.println("init u mbizborfilma"+vratiListuFilmova().get(0).getFilmid()+vratiListuFilmova().get(0).getKopijaList());
        } catch (Exception ex) {
             FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greska", "Nije uspeo da vrati listu filmova");
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
    }
    
    public void sacuvajZaduzenje(){
        trenutniFilm = null;
        mBClan.sacuvajZaduzenje();
        uzmiNovuListu();
    }
    
    public void izmeniZaduzenje(){
        trenutniFilm = null;
        mBClan.izmeniZaduzenje();
        uzmiNovuListu();
    }
    
    public boolean imaDostupnihKopija(){
        if(trenutniFilm != null && trenutniFilm.getKopijaList() != null){
            return trenutniFilm.getKopijaList().size() > 0;
        }
        return false;
    }
    
    public List<Film> getListaFilmova() {
        return listaFilmova;
    }

    public void setListaFilmova(List<Film> listaFilmova) {
        this.listaFilmova = listaFilmova;
    }
    
    
    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public Film getTrenutniFilm() {
        return trenutniFilm;
    }

    public void setTrenutniFilm(Film trenutniFilm) {
        this.trenutniFilm = trenutniFilm;
    }

    public MBClan getmBClan() {
        return mBClan;
    }

    public void setmBClan(MBClan mBClan) {
        this.mBClan = mBClan;
    }

    private void uzmiNovuListu() {
        try {
            listaFilmova = vratiListuFilmova();
        } catch (Exception ex) {
             FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greska", "Nije uspeo da vrati listu filmova");
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
    }
    
    private void setHeader() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> listaMediaType = new LinkedList<>();
        listaMediaType.add(MediaType.APPLICATION_JSON);
        listaMediaType.add(MediaType.APPLICATION_XML);
        headers.setAccept(listaMediaType);
    }

    private List<Film> vratiListuFilmova() throws IOException {
        HttpEntity entity = new HttpEntity(getHeaders());
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_FILM_LIST_PRETRAGA, HttpMethod.GET, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<List<Film>>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (List<Film>) ws.getObject();
    }
        
}
    

