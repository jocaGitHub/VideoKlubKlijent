/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.mb.clan;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovana.videoklubklijent.constants.Constants;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import com.jovana.videoklubklijent.mb.film.MBFilm;
import com.jovana.videoklubklijent.mb.prijava.MBPrijavaRadnika;
import com.jovana.videoklubzajednicko.domen.Clan;
import com.jovana.videoklubzajednicko.domen.Film;
import com.jovana.videoklubzajednicko.domen.Mesto;
import com.jovana.videoklubzajednicko.domen.Zaduzenje;
import com.jovana.videoklubzajednicko.domen.ZaduzenjePK;
import com.jovana.videoklubzajednicko.dto.WsDto;
import java.io.IOException;
import java.util.ArrayList;
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
@SessionScoped
public class MBClan implements Serializable {

    private List<Clan> listaClanova;
    private List<Mesto> listaMesta;
    private Clan trenutniClan;
    private Zaduzenje zaduzenje;
    private boolean vidljivostZaduzenja;
    private boolean vidljivost2;
    private HttpHeaders headers;

    @ManagedProperty(value = "#{mBPrijavaRadnika}")
    MBPrijavaRadnika mBPrijavaRadnika;

    @ManagedProperty(value = "#{mBFilm}")
    private MBFilm mBFilm;

    /**
     * Creates a new instance of MBClan
     */
    public MBClan() {
        System.out.println("MBCLAN KONSTRUKTOR");
        listaMesta = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        setHeader();
        try {
            listaClanova =  vratiListuClanova();
        } catch (Exception ex) {
            System.out.println("init u mbclan nije nasao clanove"+ex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Greska", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        try {
            listaMesta = vratiListuMesta();
        } catch (Exception ex) {
            System.out.println("init u mb clan, nije nasao mesta"+ex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Greska", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        trenutniClan = new Clan();
        vidljivostZaduzenja = false;
        vidljivost2 = false;
    }

    public String izmeniClana() {
        return "unosClana.xhtml";
    }

    public List<Clan> getListaClanova() {
        return listaClanova;
    }

    public void setListaClanova(List<Clan> listaClanova) {
        this.listaClanova = listaClanova;
    }

    public Clan getTrenutniClan() {
        return trenutniClan;
    }

    public void setTrenutniClan(Clan trenutniClan) {
        this.trenutniClan = trenutniClan;
    }

    public void vidljivostIzmene(boolean b) {
        vidljivost2 = b;
    }

    public String sacuvajNovogClana() {
        System.out.println("sacuvajNovogClana(): " + trenutniClan);
        try {
            trenutniClan = sacuvajClana(trenutniClan);
            listaClanova.add(trenutniClan);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Unos clana", "Clan je sacuvan");
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return "prikazClana_1.xhtml";
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Greska", "Sistem ne moze da sacuva clana: "+ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return null;
        }

    }

    public List<Clan> vratiNovuListuClanova() {
        try {
            listaClanova =  vratiListuClanova();
            return listaClanova;
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Greska", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return null;
        }
    }

    public String kreirajClana() {
        System.out.println("kreirajClana()");
        trenutniClan = new Clan("-1");
        mBFilm.setTrenutniFilm(new Film());
        return "unosClana.xhtml";
    }


    public String kreirajZaduzenje() {
        System.out.println("MBCLAN: kreirajZaduzenje()");
        zaduzenje = new Zaduzenje(new ZaduzenjePK());
        vidljivostZaduzenja = true;
        return null;
    }

    public String prikaziPretragu() {
        trenutniClan = null;
        vratiNovuListuClanova();
        return "pretragaClanova.xhtml";
    }

    public String sacuvajZaduzenje() {
        System.out.println("MBCLAN: sacuvajZaduzenje() - clan: " + zaduzenje);
        try {
            String kopijaID = zaduzenje.getKopija().getKopijaPK().getKopijaid();
            String filmID = zaduzenje.getKopija().getKopijaPK().getFilmid();
            zaduzenje.setClan(trenutniClan);
            zaduzenje.getZaduzenjePK().setClanid(trenutniClan.getClanid());
            zaduzenje.getZaduzenjePK().setFilmid(filmID);
            zaduzenje.getZaduzenjePK().setKopijaid(kopijaID);
            zaduzenje.setZaduzio(mBPrijavaRadnika.getPrijavljeniRadnik());
            zaduzenje.setStatuszaduzenja("zauzeto");
            System.out.println("novi saut zaduzenje"+zaduzenje);
            Zaduzenje zaduzenjeDB = sacuvajZaduzenjeZaClana(zaduzenje);
//            trenutniClan.getZaduzenjeList().add(zaduzenjeDB);
//            vratiNovuListuClanova();
//            mBFilm.uzmiNovuListuFilmova();
            zaduzenje = null;
            vidljivostZaduzenja = false;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sacuvaj zaduzenje", "Zaduzenje je sacuvano");
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greska", "Sistem ne moze da sacuva zaduzenje:"+ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        
        return null;
    }

    public String izmeniZaduzenje() {
        try {
            zaduzenje.setRazduzio(mBPrijavaRadnika.getPrijavljeniRadnik());
            zaduzenje.setStatuszaduzenja("vraceno");
            sacuvajZaduzenjeZaClana(zaduzenje);
            vidljivost2 = false;
            trenutniClan = vratiClana(trenutniClan);
//            mBFilm.uzmiNovuListuFilmova();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Izmena zaduzenja", "Zaduzenje je izmenjeno");
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return null;
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greska", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        return null;

    }

    public String otkaziZaduzenje() {
        vidljivostZaduzenja = false;
        zaduzenje = null;
        return null;
    }

    public List<Mesto> getListaMesta() {
        return listaMesta;
    }

    public void setListaMesta(List<Mesto> listaMesta) {
        this.listaMesta = listaMesta;
    }

    public String prikaziClana() {
        try {
            trenutniClan = vratiClana(trenutniClan);
            return "prikazClana_1.xhtml";
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Greska", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return null;
        }
    }

    public Zaduzenje getZaduzenje() {
        return zaduzenje;
    }

    public void setZaduzenje(Zaduzenje zaduzenje) {
        this.zaduzenje = zaduzenje;
    }

    public boolean isVidljivostZaduzenja() {
        return vidljivostZaduzenja;
    }

    public void setVidljivostZaduzenja(boolean vidljivostZaduzenja) {
        this.vidljivostZaduzenja = vidljivostZaduzenja;
    }

    public MBPrijavaRadnika getmBPrijavaRadnika() {
        System.out.println("MBPRIJAVARADNIKA KONSTRUKTOR");
        return mBPrijavaRadnika;
    }

    public void setmBPrijavaRadnika(MBPrijavaRadnika mBPrijavaRadnika) {
        this.mBPrijavaRadnika = mBPrijavaRadnika;
    }

    public MBFilm getmBFilm() {
        return mBFilm;
    }

    public void setmBFilm(MBFilm mBFilm) {
        this.mBFilm = mBFilm;
    }

    public boolean isVidljivost2() {
        return vidljivost2;
    }

    public void setVidljivost2(boolean vidljivost2) {
        this.vidljivost2 = vidljivost2;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public Clan vratiClana(Clan clan) throws IOException{
            HttpEntity entity = new HttpEntity(trenutniClan, getHeaders());
            ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_CLAN, HttpMethod.POST, entity, String.class);
            WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<Clan>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (Clan) ws.getObject();
    }
    
    public List<Clan> vratiListuClanova() throws IOException{
            HttpEntity entity = new HttpEntity(getHeaders());
            ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_CLAN_LIST, HttpMethod.GET, entity, String.class);
            WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<List<Clan>>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (List<Clan>) ws.getObject();
    }

    private List<Mesto> vratiListuMesta() throws IOException {
            HttpEntity entity = new HttpEntity(getHeaders());
            ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_MESTO_LIST, HttpMethod.GET, entity, String.class);
            WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<List<Mesto>>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (List<Mesto>) ws.getObject();
    }
    
    private void setHeader() {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            List<MediaType> listaMediaType = new LinkedList<>();
            listaMediaType.add(MediaType.APPLICATION_JSON);
            listaMediaType.add(MediaType.APPLICATION_XML);
            headers.setAccept(listaMediaType);
    }

    private Clan sacuvajClana(Clan trenutniClan) throws IOException {
            HttpEntity entity = new HttpEntity(trenutniClan, getHeaders());
            ResponseEntity<String> response = new RestTemplate().exchange(Constants.SAVE_CLAN, HttpMethod.POST, entity, String.class);
            WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<Clan>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (Clan) ws.getObject();
    }

    private Zaduzenje sacuvajZaduzenjeZaClana(Zaduzenje zaduzenje) throws IOException {
            HttpEntity entity = new HttpEntity(zaduzenje, getHeaders());
            ResponseEntity<String> response = new RestTemplate().exchange(Constants.SAVE_ZADUZENJE, HttpMethod.POST, entity, String.class);
            WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<Zaduzenje>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (Zaduzenje) ws.getObject();
    }
}
