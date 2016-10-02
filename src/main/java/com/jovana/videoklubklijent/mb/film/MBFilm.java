/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.mb.film;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovana.videoklubklijent.constants.Constants;
import com.jovana.videoklubzajednicko.domen.Film;
import com.jovana.videoklubzajednicko.domen.Kopija;
import com.jovana.videoklubzajednicko.domen.KopijaPK;
import com.jovana.videoklubzajednicko.domen.Osoba;
import com.jovana.videoklubzajednicko.domen.Uloga;
import com.jovana.videoklubzajednicko.domen.UlogaPK;
import com.jovana.videoklubzajednicko.dto.WsDto;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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
public class MBFilm implements Serializable{

    private Film trenutniFilm;
    private boolean vidljivostPanela;
    private List<String> listaZanrova;
    private Kopija trenutnaKopija;
    private Uloga trenutnaUloga;
    private List<String> listaUloga;
    private List<Osoba> listaOsoba;
    private String poruka;
    private List<Film> listaFilmova;
    private List<Kopija> listaDostupnih;
    private boolean novi;
    private HttpHeaders headers;

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }
    
    public boolean isNovi() {
        return novi;
    }

    public List<Kopija> getListaDostupnih() {
        return listaDostupnih;
    }

    public void setListaDostupnih(List<Kopija> listaDostupnih) {
        this.listaDostupnih = listaDostupnih;
    }

    public void setNovi(boolean novi) {
        this.novi = novi;
    }
    /**
     * Creates a new instance of MBFilm
     */
    public MBFilm() {
        System.out.println("MBFILM KONSTRUKTOR");
        listaZanrova = new ArrayList<>();
        listaZanrova.add("drama");
        listaZanrova.add("komedija");
        listaZanrova.add("horor");
        listaZanrova.add("akcija");

        listaUloga = new ArrayList<>();
        listaUloga.add("glumi");
        listaUloga.add("reditelj");
        listaUloga.add("producent");
        listaUloga.add("scenarista");

        trenutnaUloga = new Uloga(new UlogaPK());
        vidljivostPanela = false;
        novi = true;
        poruka = "Vec postoji ta uloga";
    }

    @PostConstruct
    public void init() {
        setHeader();
        try {
            listaOsoba = vratiListuOsoba();
            System.out.println("MBFILM init listaOsoba = " + listaOsoba.toString());
        } catch (Exception ex) {
            System.out.println("MBFILM init() ex = "+ ex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "MBFilm", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        try {
            listaFilmova = vratiListuFilmova();
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "MBFilm", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }

    }

    public String prikaziPretragu() {
        trenutniFilm = null;
        try {
            listaFilmova = vratiListuFilmova();
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "MBFilm", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        return "pretragaFilma.xhtml";
    }

    public void uzmiNovuListuFilmova() {
        try {
            listaFilmova = vratiListuFilmova();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void prikaziPanele(boolean b) {
        System.out.println("prikaziPanele()");
        vidljivostPanela = b;
    }

    public String kreirajFilm() {
        System.out.println("kreirajFilm()");
        trenutniFilm = new Film();
        trenutniFilm.setKopijaList(new ArrayList<Kopija>());
        trenutniFilm.setUlogaList(new ArrayList<Uloga>());
        vidljivostPanela = false;
        novi = true;
        return "unosFilma_1.xhtml";
    }

    public String sacuvajFilm() {
        try {
            trenutniFilm = sacuvajNoviFilm(trenutniFilm, novi);
            if (novi == true) {
                listaFilmova.add(trenutniFilm);
            }
            novi = false;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Unos filma", "Sistem je uspesno sacuvao film");
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            trenutniFilm = null;
            return "pretragaFilma.xhtml";
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greska", "Sistem ne moze da sacuva film:"+ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        return null;
    }

    public String kreirajKopiju() {
        System.out.println("kreirajKopiju()");
        String id = (trenutniFilm.getKopijaList().size() + 10001) + "";
        Kopija kopija = new Kopija(new KopijaPK(id, trenutniFilm.getFilmid()));
        kopija.setRbr(id);
        trenutniFilm.getKopijaList().add(kopija);
        System.out.println("lista kopiija na filmu: " + trenutniFilm.getKopijaList().size());
        return null;
    }

    public String prikaziFilm() {
        try {
            trenutniFilm.setKopijaList(vratiListuKopijaZaFilm(trenutniFilm));
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Uzimanje kopija", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        try {
            trenutniFilm.setUlogaList(vratiListuUlogaZaFilm(trenutniFilm));
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Uzimanje Uloga", ex.getMessage());
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }
        vidljivostPanela = true;
        novi = false;
        return "prikazFilma.xhtml";
    }

    public void sacuvajUlogu() {
        System.out.println("sacuvajUlogu()");
        System.out.println("/// trenutna uloga:" + trenutnaUloga);
        Uloga u = trenutnaUloga;
        boolean postoji = false;
        for (Uloga ul : trenutniFilm.getUlogaList()) {
            if (ul.getNazivuloge().equals(u.getNazivuloge()) && ul.getOsoba().equals(u.getOsoba())) {
                postoji = true;
                break;
            }
        }
        if (postoji) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Upozorenje", poruka));
        } else {
            System.out.println("/// u: " + u);
            System.out.println("/// u.getosoba: " + u.getOsoba());
            u.getUlogaPK().setOsobaid(u.getOsoba().getOsobaid());
            String id = (trenutniFilm.getUlogaList().size() + 10001) + "";
            u.getUlogaPK().setUlogaid(id);
            trenutniFilm.getUlogaList().add(trenutnaUloga);
            System.out.println("velicina liste uloga u filmu: " + trenutniFilm.getUlogaList().size());
            trenutnaUloga = new Uloga(new UlogaPK());
        }
    }

    public void obrisiUlogu() {
        trenutniFilm.getUlogaList().remove(trenutnaUloga);
    }

    public void obrisiKopiju() {
        trenutniFilm.getKopijaList().remove(trenutnaKopija);
    }

    public Film getTrenutniFilm() {
        return trenutniFilm;
    }

    public void setTrenutniFilm(Film trenutniFilm) {
        this.trenutniFilm = trenutniFilm;
    }

    public List<String> getListaZanrova() {
        return listaZanrova;
    }

    public void setListaZanrova(List<String> listaZanrova) {
        this.listaZanrova = listaZanrova;
    }

    public Kopija getTrenutnaKopija() {
        return trenutnaKopija;
    }

    public void setTrenutnaKopija(Kopija trenutnaKopija) {
        this.trenutnaKopija = trenutnaKopija;
    }

    public List<String> getListaUloga() {
        return listaUloga;
    }

    public void setListaUloga(List<String> listaUloga) {
        this.listaUloga = listaUloga;
    }

    public Uloga getTrenutnaUloga() {
        return trenutnaUloga;
    }

    public void setTrenutnaUloga(Uloga trenutnUloga) {
        this.trenutnaUloga = trenutnUloga;
    }

    public List<Osoba> getListaOsoba() {
        return listaOsoba;
    }

    public void setListaOsoba(List<Osoba> listaOsoba) {
        this.listaOsoba = listaOsoba;
    }

    public boolean isVidljivostPanela() {
        return vidljivostPanela;
    }

    public void setVidljivostPanela(boolean vidljivostPanela) {
        this.vidljivostPanela = vidljivostPanela;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    public List<Film> getListaFilmova() {
        return listaFilmova;
    }

    public void setListaFilmova(List<Film> listaFilmova) {
        this.listaFilmova = listaFilmova;
    }

    public void setHeader(){
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> listaMediaType = new LinkedList<>();
        listaMediaType.add(MediaType.APPLICATION_JSON);
        listaMediaType.add(MediaType.APPLICATION_XML);
        headers.setAccept(listaMediaType);
    }
    
    private List<Osoba> vratiListuOsoba() throws IOException {
        HttpEntity entity = new HttpEntity(getHeaders());
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_OSOBA_LIST, HttpMethod.GET, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<List<Osoba>>>(){});
            if(ws.getStatus()==-1){
                System.out.println("MBFILM vratiListuOsoba status-1 ="+ws.getErrorMessage());
                throw new IOException(ws.getErrorMessage());
            }
            return (List<Osoba>) ws.getObject();   
    }

    private List<Film> vratiListuFilmova() throws IOException {
        HttpEntity entity = new HttpEntity(getHeaders());
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_FILM_LIST, HttpMethod.GET, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<List<Film>>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (List<Film>) ws.getObject();
    }

    private Film sacuvajNoviFilm(Film trenutniFilm, boolean novi) throws IOException {
        HttpEntity entity = new HttpEntity(trenutniFilm, getHeaders());
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.SAVE_FILM, HttpMethod.POST, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<Film>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            } else {
                return (Film) ws.getObject();
            }
    }

    private List<Kopija> vratiListuKopijaZaFilm(Film trenutniFilm) throws IOException {
        HttpEntity entity = new HttpEntity(trenutniFilm, getHeaders());
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_KOPIJA_LIST_FILMA, HttpMethod.POST, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<List<Kopija>>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (List<Kopija>) ws.getObject();
    }

    private List<Uloga> vratiListuUlogaZaFilm(Film trenutniFilm) throws IOException {
        HttpEntity entity = new HttpEntity(trenutniFilm, getHeaders());
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_ULOGA_LIST_FILMA, HttpMethod.POST, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<List<Uloga>>>(){});
            if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (List<Uloga>) ws.getObject();
    }

}
