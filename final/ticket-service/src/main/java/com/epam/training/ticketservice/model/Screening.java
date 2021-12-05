package com.epam.training.ticketservice.model;



import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import java.io.Serializable;


@Getter
@NoArgsConstructor
@Entity
public class Screening implements Serializable {

    @EmbeddedId
    private ScreeningId id;

    public Screening(ScreeningId screeningId) {
        this.id = screeningId;
    }
}