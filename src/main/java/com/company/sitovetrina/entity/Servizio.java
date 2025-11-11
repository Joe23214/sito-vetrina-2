package com.company.sitovetrina.entity;

import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.Store;
import io.jmix.data.DdlGeneration;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@DdlGeneration(value = DdlGeneration.DbScriptGenerationMode.DISABLED)
@JmixEntity
@Store(name = "sitovetrinadb")
@Table(name = "servizio")
@Entity
public class Servizio {
    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_creazione")
    private Date dataCreazione;

    @Column(name = "descrizione")
    @Lob
    private String descrizione;

    @Column(name = "`sottoTitolo`", length = 150)
    private String sottoTitolo;

    @Column(name = "titolo", nullable = false, length = 150)
    private String titolo;

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getSottoTitolo() {
        return sottoTitolo;
    }

    public void setSottoTitolo(String sottoTitolo) {
        this.sottoTitolo = sottoTitolo;
    }


    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Date getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}