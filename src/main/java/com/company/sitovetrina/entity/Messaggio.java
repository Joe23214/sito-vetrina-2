package com.company.sitovetrina.entity;

import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.Store;
import io.jmix.data.DdlGeneration;
import jakarta.persistence.*;

import java.util.Date;

@DdlGeneration(value = DdlGeneration.DbScriptGenerationMode.DISABLED)
@JmixEntity
@Store(name = "sitovetrinadb")
@Table(name = "messaggio")
@Entity
public class Messaggio {
    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_invio")
    private Date dataInvio;

    @Column(name = "email_mittente", length = 150)
    private String emailMittente;

    @Column(name = "nome_mittente", length = 150)
    private String nomeMittente;

    @Column(name = "testo", nullable = false)
    @Lob
    private String testo;

    @Column(name = "titolo", nullable = false, length = 150)
    private String titolo;

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getNomeMittente() {
        return nomeMittente;
    }

    public void setNomeMittente(String nomeMittente) {
        this.nomeMittente = nomeMittente;
    }

    public String getEmailMittente() {
        return emailMittente;
    }

    public void setEmailMittente(String emailMittente) {
        this.emailMittente = emailMittente;
    }

    public Date getDataInvio() {
        return dataInvio;
    }

    public void setDataInvio(Date dataInvio) {
        this.dataInvio = dataInvio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}