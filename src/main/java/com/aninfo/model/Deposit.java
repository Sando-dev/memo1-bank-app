package com.aninfo.model;

import javax.persistence.*;


@Entity
@DiscriminatorValue("DEPOSIT")
public class Deposit extends Transaction {
    public Deposit() {
        super(); // Llama al constructor sin argumentos de la clase base
    }

    public Deposit(Double amount) {
        super(amount);
    }
}
