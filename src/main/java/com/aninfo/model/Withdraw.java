package com.aninfo.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("WITHDRAW")
public class Withdraw extends Transaction {
    public Withdraw() {
        super(); // Llama al constructor sin argumentos de la clase base
    }

    public Withdraw(Double amount) {
        super(amount);
    }
}
