/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.mb.prijava;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovana.videoklubklijent.constants.Constants;
import com.jovana.videoklubzajednicko.domen.Radnici;
import com.jovana.videoklubzajednicko.dto.WsDto;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
public class MBPrijavaRadnika implements Serializable{

    private Radnici prijavljeniRadnik;
    private Radnici trenutniRadnik;
    private HttpHeaders headers;

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }
    /**
     * Creates a new instance of MBPrijavaRadnika
     */
    public MBPrijavaRadnika() {
        System.out.println("MBPRIJAVARADNIKA KONSTRUKTOR");
        trenutniRadnik = new Radnici();
        setHeader();
    }

    public String prijaviRadnika() {
        System.out.println("prijavaaaa: " + trenutniRadnik.getKorisnickoime() + trenutniRadnik.getKorisnickasifra());
        if (trenutniRadnik != null) {
            try {
                prijavljeniRadnik = prijaviRadnikaNaSistem(trenutniRadnik.getKorisnickoime(), trenutniRadnik.getKorisnickasifra());       
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Prijava radnika", "Uspesno ste se prijavili na sistem");
                RequestContext.getCurrentInstance().showMessageInDialog(message);
                return "index.xhtml";

            } catch (Exception ex) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Greska", ex.getMessage());
                RequestContext.getCurrentInstance().showMessageInDialog(message);
                return null;
                
            }
        }else{
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Greska", "prijavljeni radnik je null");
            return null;
        }
        
    }

    public String odjavi() {
        prijavljeniRadnik = new Radnici();
        trenutniRadnik = new Radnici();
        return "prijavaRadnika.xhtml";
    }

    public Radnici getPrijavljeniRadnik() {
        return prijavljeniRadnik;
    }

    public void setPrijavljeniRadnik(Radnici prijavljeniRadnik) {
        this.prijavljeniRadnik = prijavljeniRadnik;
    }

    public Radnici getTrenutniRadnik() {
        return trenutniRadnik;
    }

    public void setTrenutniRadnik(Radnici trenutniRadnik) {
        this.trenutniRadnik = trenutniRadnik;
    }

    private Radnici prijaviRadnikaNaSistem(String korisnickoime, String korisnickasifra) throws IOException {
        Radnici radnik = new Radnici();
        radnik.setKorisnickasifra(korisnickasifra);
        radnik.setKorisnickoime(korisnickoime);
        HttpEntity entity = new HttpEntity(radnik, getHeaders());
        System.out.println("radnik"+radnik.getKorisnickoime());
        ResponseEntity<String> response = new RestTemplate().exchange(Constants.GET_RADNIK, HttpMethod.POST, entity, String.class);
        WsDto ws = new ObjectMapper().readValue(response.getBody(), new TypeReference<WsDto<Radnici>>(){});
        System.out.println(radnik.getKorisnickoime() + "vratio");
        if(ws.getStatus()==-1){
                throw new IOException(ws.getErrorMessage());
            }
            return (Radnici) ws.getObject();
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
