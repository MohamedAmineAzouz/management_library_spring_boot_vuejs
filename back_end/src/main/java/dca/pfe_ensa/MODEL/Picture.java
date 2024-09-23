package dca.pfe_ensa.MODEL;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "picture")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "picture_id")
    private  int pictureId;
    private String path;

    @OneToOne
    @JoinColumn(name = "book_id", referencedColumnName = "book_id")
    @JsonIgnore
    private Books book;

}
