package dca.pfe_ensa.MODEL;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;


@Entity
@Table(name = "borrower")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Borrower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrower_id")
    private int borrowerId;

    @Temporal(TemporalType.DATE)
    private Date borrowerDate;

    @Temporal(TemporalType.DATE)
    private Date returnedDate;

    private boolean confirmReturn;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "book_id")
    private Books book;
}
