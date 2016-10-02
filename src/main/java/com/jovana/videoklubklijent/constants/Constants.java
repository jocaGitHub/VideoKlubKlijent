/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jovana.videoklubklijent.constants;

/**
 *
 * @author jmoldovan
 */
public class Constants {
    final static String ADDRES="http://localhost:8085";
    
    public final static String GET_CLAN= ADDRES + "/clan";
    public final static String GET_CLAN_LIST= GET_CLAN + "/all";
    public final static String SAVE_CLAN= GET_CLAN + "/save";
    public final static String GET_MESTO_LIST= GET_CLAN + "/mesta";
    public final static String GET_MESTO= GET_CLAN + "/mesto";
    
    public final static String GET_ZADUZENJE= ADDRES + "/zaduzenje";
    public final static String GET_ZADUZENJE_LIST= GET_ZADUZENJE + "/all";
    public final static String GET_ZADUZENJA_ZA_CLANA= GET_ZADUZENJE_LIST + "/clan";
    public final static String SAVE_ZADUZENJE= GET_ZADUZENJE + "/save";
    
    public final static String GET_FILM= ADDRES + "/film";
    public final static String GET_FILM_LIST= GET_FILM + "/all";
    public final static String GET_FILM_LIST_PRETRAGA= GET_FILM_LIST + "/search";
    public final static String SAVE_FILM= GET_FILM + "/save";
    public final static String REPLACE_FILM= GET_FILM + "/replace";
    public final static String GET_ULOGA_LIST_FILMA= GET_FILM + "/uloge";
    public final static String GET_KOPIJA= GET_FILM + "/kopija";
    public final static String GET_KOPIJA_LIST_FILMA= GET_KOPIJA + "/all";
    public final static String GET_OSOBA= GET_FILM + "/osoba";
    public final static String GET_OSOBA_LIST= GET_OSOBA + "/all";
    
    public final static String GET_RADNIK= ADDRES + "/radnik";
    
    
}
