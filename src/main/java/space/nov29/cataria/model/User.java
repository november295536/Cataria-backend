package space.nov29.cataria.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String password;
}
