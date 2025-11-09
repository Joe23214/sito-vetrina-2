package com.company.sitovetrina.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.Store;
import io.jmix.data.DdlGeneration;
import jakarta.persistence.*;

import java.util.Date;

@DdlGeneration(value = DdlGeneration.DbScriptGenerationMode.DISABLED)
@JmixEntity
@Store(name = "sitovetrinadb")
@Table(name = "configsitovetrina")
@Entity
public class Configsitovetrina {
    @JmixGeneratedValue
    @Column(name = "id", nullable = false)
    @Id
    private Integer id;

    @Column(name = "about_testo")
    @Lob
    private String aboutTesto;

    @Column(name = "about_testo2")
    @Lob
    private String aboutTesto2;

    @Column(name = "about_titolo", length = 150)
    private String aboutTitolo;

    @Column(name = "about_titolo2", length = 150)
    private String aboutTitolo2;

    @Column(name = "cap", length = 6)
    private String cap;

    @Column(name = "citta", length = 30)
    private String citta;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_ultima_modifica")
    private Date dataUltimaModifica;

    @Column(name = "descrizione_sito")
    @Lob
    private String descrizioneSito;

    @Column(name = "email_contatti", length = 150)
    private String emailContatti;

    @Column(name = "indirizzo")
    private String indirizzo;

    @Column(name = "`linkFacebook`", length = 200)
    private String linkFacebook;

    @Column(name = "`linkInsta`", length = 200)
    private String linkInsta;

    @Column(name = "`linkTikTok`", length = 200)
    private String linkTikTok;

    @Column(name = "nome_sito", length = 150)
    private String nomeSito;

    @Column(name = "piva", length = 12)
    private String piva;

    @Column(name = "`ragioneSociale`", length = 100)
    private String ragioneSociale;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "`testoFooter`", length = 250)
    private String testoFooter;

    public String getTestoFooter() {
        return testoFooter;
    }

    public void setTestoFooter(String testoFooter) {
        this.testoFooter = testoFooter;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRagioneSociale() {
        return ragioneSociale;
    }

    public void setRagioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
    }

    public String getPiva() {
        return piva;
    }

    public void setPiva(String piva) {
        this.piva = piva;
    }

    public String getNomeSito() {
        return nomeSito;
    }

    public void setNomeSito(String nomeSito) {
        this.nomeSito = nomeSito;
    }

    public String getLinkTikTok() {
        return linkTikTok;
    }

    public void setLinkTikTok(String linkTikTok) {
        this.linkTikTok = linkTikTok;
    }

    public String getLinkInsta() {
        return linkInsta;
    }

    public void setLinkInsta(String linkInsta) {
        this.linkInsta = linkInsta;
    }

    public String getLinkFacebook() {
        return linkFacebook;
    }

    public void setLinkFacebook(String linkFacebook) {
        this.linkFacebook = linkFacebook;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getEmailContatti() {
        return emailContatti;
    }

    public void setEmailContatti(String emailContatti) {
        this.emailContatti = emailContatti;
    }

    public String getDescrizioneSito() {
        return descrizioneSito;
    }

    public void setDescrizioneSito(String descrizioneSito) {
        this.descrizioneSito = descrizioneSito;
    }

    public Date getDataUltimaModifica() {
        return dataUltimaModifica;
    }

    public void setDataUltimaModifica(Date dataUltimaModifica) {
        this.dataUltimaModifica = dataUltimaModifica;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getAboutTitolo2() {
        return aboutTitolo2;
    }

    public void setAboutTitolo2(String aboutTitolo2) {
        this.aboutTitolo2 = aboutTitolo2;
    }

    public String getAboutTitolo() {
        return aboutTitolo;
    }

    public void setAboutTitolo(String aboutTitolo) {
        this.aboutTitolo = aboutTitolo;
    }

    public String getAboutTesto2() {
        return aboutTesto2;
    }

    public void setAboutTesto2(String aboutTesto2) {
        this.aboutTesto2 = aboutTesto2;
    }

    public String getAboutTesto() {
        return aboutTesto;
    }

    public void setAboutTesto(String aboutTesto) {
        this.aboutTesto = aboutTesto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}