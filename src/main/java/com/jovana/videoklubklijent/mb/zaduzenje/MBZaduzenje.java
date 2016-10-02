/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.mb.zaduzenje;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovana.videoklubklijent.constants.Constants;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import com.jovana.videoklubklijent.mb.film.MBFilm;
import com.jovana.videoklubklijent.mb.prijava.MBPrijavaRadnika;
import com.jovana.videoklubzajednicko.domen.Film;
import com.jovana.videoklubzajednicko.domen.Zaduzenje;
import com.jovana.videoklubzajednicko.domen.ZaduzenjePK;
import com.jovana.videoklubzajednicko.dto.WsDto;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;
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
public class MBZaduzenje implements Serializable{
    private List<Zaduzenje> listaZaduzenja;
    private Zaduzenje trenutnoZaduzenje;
    private boolean daLiIzmena;
    private HttpHeaders headers;

    public boolean isDaLiIzmena() {
        return daLiIzmena;
    }

    public void setDaLiIzmena(boolean daLiIzmena) {
        this.daLiIzmena = daLiIzmena;
    }

    @ManagedProperty(value = "#{mBPrijavaRadnika}")
    MBPrijavaRadnika mBPrijavaRadnika;
    
    @ManagedProperty(value = "#{mBFilm}")
    private MBFilm mBFilm;
    /**
     * Creates a new instance of MBClan
     */
    public MBZaduzenje() {
        System.out.println("MBZADUZENJE KONSTRUKTOR");
    }

    public MBPrijavaRadnika getmBPrijavaRadnika() {
        return mBPrijavaRadnika;
    }

    public void setmBPrijavaRadnika(MBPrijavaRadnika mBPrijavaRadnika) {
        this.mBPrijavaRadnika = mBPrijavaRadnika;
    }

    @PostConstruct
    public void init() {
        setHeader();
        try {
            listaZaduzenja = vratiListuZaduzenja();
        } catch (Exception ex) {
            System.out.println("greska init"+ex.getMessage());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greska", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        trenutnoZaduzenje = new Zaduzenje(new ZaduzenjePK());
        daLiIzmena = false;
    }

    public String sacuvajZaduzenje() {
        System.out.println("MBZADUZENJE: sacuvajZaduzenje() - clan: " + trenutnoZaduzenje);
        try {
            String kopijaID = trenutnoZaduzenje.getKopija().getKopijaPK().getKopijaid();
            String filmID = trenutnoZaduzenje.getKopija().getKopijaPK().getFilmid();
            String clanID = trenutnoZaduzenje.getClan().getClanid();
            trenutnoZaduzenje.getZaduzenjePK().setClanid(clanID);
            trenutnoZaduzenje.getZaduzenjePK().setFilmid(filmID);
            trenutnoZaduzenje.getZaduzenjePK().setKopijaid(kopijaID);
            trenutnoZaduzenje.setZaduzio(mBPrijavaRadnika.getPrijavljeniRadnik());
            trenutnoZaduzenje.setStatuszaduzenja("zauzeto");
            Zaduzenje zaduzenjeDB = sacuvajNovoZaduzenje(trenutnoZaduzenje);
//            listaZaduzenja.add(trenutnoZaduzenje);
            //da osvezi kopije
//            mBFilm.uzmiNovuListuFilmova();
            trenutnoZaduzenje = null;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sacuvaj zaduzenje", "Zaduzenje je sacuvano");
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return "pretragaZaduzenja.xhtml";
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greska", "Sistem ne moze da sacuva zaduzenje:"+ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        return null;
    }

    public List<Zaduzenje> vratiNovuListuZaduzenja() {
        try {
            listaZaduzenja = vratiListuZauzetihZaduzenja();
            return listaZaduzenja;
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greska", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return null;
        }

    }
    public String izmeniZaduzenje(){
        try {
            trenutnoZaduzenje.setRazduzio(mBPrijavaRadnika.getPrijavljeniRadnik());
            trenutnoZaduzenje.setStatuszaduzenja("vraceno");
            System.out.println("ZAD"+trenutnoZaduzenje);
            sacuvajNovoZaduzenje(trenutnoZaduzenje);
//            mBFilm.uzmiNovuListuFilmova();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Izmena zaduzenja", "Zaduzenje je izmenjeno");
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return null;
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greska", "Sistem ne moze da sacuva zaduzenje:"+ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        return null;
    }
    
    public String kreirajZaduzenje() {
        System.out.println("MBCLAN: kreirajClana()");
        trenutnoZaduzenje = new Zaduzenje(new ZaduzenjePK());
        mBFilm.setTrenutniFilm(new Film());
        uzmiNovuListuFilmova();
        return "unosZaduzenja.xhtml";
    }
    
    public String prikaziPretragu(){
        trenutnoZaduzenje = new Zaduzenje(new ZaduzenjePK());
         try {
            listaZaduzenja = vratiListuZauzetihZaduzenja();
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greska", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        return "pretragaZaduzenja.xhtml";
    }

    /**
     * Creates a new instance of MBZaduzenja
     */
    public List<Zaduzenje> getListaZaduzenja() {
        return listaZaduzenja;
    }

    public void setListaZaduzenja(List<Zaduzenje> listaZaduzenja) {
        this.listaZaduzenja = listaZaduzenja;
    }

    public Zaduzenje getTrenutnoZaduzenje() {
        return trenutnoZaduzenje;
    }

    public void setTrenutnoZaduzenje(Zaduzenje trenutnoZaduzenje) {
        this.trenutnoZaduzenje = trenutnoZaduzenje;
    }

    public MBFilm getmBFilm() {
        return mBFilm;
    }

    public void setmBFilm(MBFilm mBFilm) {
        this.mBFilm = mBFilm;
    }

    private void uzmiNovuListuFilmova() {
        mBFilm.uzmiNovuListuFilmova();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    private List<Zaduzenje> vratiListuZaduzenja() throws IOException {
        HttpEntity entity = new HttpEntity(getHeaders());
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_ZADUZENJE_LIST, HttpMethod.GET, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<List<Zaduzenje>>>(){});
        System.out.println("dobio u vratiListuZaduzenja"+ws.getObject().toString());    
        if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (List<Zaduzenje>) ws.getObject();
    }

     public void setHeader(){
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> listaMediaType = new LinkedList<>();
        listaMediaType.add(MediaType.APPLICATION_JSON);
        listaMediaType.add(MediaType.APPLICATION_XML);
        headers.setAccept(listaMediaType);
    }

    private Zaduzenje sacuvajNovoZaduzenje(Zaduzenje trenutnoZaduzenje) throws IOException {
        HttpEntity entity = new HttpEntity(trenutnoZaduzenje, getHeaders());
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.SAVE_ZADUZENJE, HttpMethod.POST, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<Zaduzenje>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
        return (Zaduzenje) ws.getObject();
    }

    private List<Zaduzenje> vratiListuZauzetihZaduzenja() throws IOException {
        List<Zaduzenje> lista = vratiListuZaduzenja();
        List<Zaduzenje>listaVracenih = new ArrayList<>();
        for (Zaduzenje z : lista) {
            if(z.getStatuszaduzenja().equals("vraceno")){
                listaVracenih.add(z);
            }
        }
        for (Zaduzenje zz : listaVracenih) {
            lista.remove(zz);
        }
        return lista;
    }
}
