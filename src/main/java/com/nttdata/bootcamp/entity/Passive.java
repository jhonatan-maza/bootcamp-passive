package com.nttdata.bootcamp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "passive")
public class Passive {

    @Id
    private String id;

    private String dni;
    private String accountNumber;
    private String typeCustomer;

    private Boolean saving = false;
    private Boolean currentAccount = false;
    private Boolean fixedTerm = false;

    private Number movementCommission; //Commission percentage
    private Number savingMovementsMonthly; //Movement limit

    private String status;
    private Double balance;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @CreatedDate
    private Date creationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @LastModifiedDate
    private Date modificationDate;

}
