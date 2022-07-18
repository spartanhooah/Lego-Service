package net.frey.legos.legoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.frey.legos.legoservice.enums.Type;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lego_set")
public class LegoSet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String name;

    @Column(name = "set_number", nullable = false)
    private String number;

    @Column(name = "type")
    private Type type;

    @Column(name = "year")
    private int year;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        LegoSet legoSet = (LegoSet) o;
        return id != null && Objects.equals(id, legoSet.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}