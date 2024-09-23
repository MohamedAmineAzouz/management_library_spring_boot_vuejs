package dca.pfe_ensa.MODEL;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;
    private String title;
    private String author;
    private String year;
    @Column(name = "number_pages")
    private int numberPages;
    private String type;
    private int quantity;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL)
    private Picture picture;

}
