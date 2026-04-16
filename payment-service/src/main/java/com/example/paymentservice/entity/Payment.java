import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🚀 SİHİRLİ DOKUNUŞ BURASI: unique = true
    // Artık veritabanı aynı sipariş ID'sini ikinci kez kaydetmeye ASLA izin vermez.
    @Column(unique = true, nullable = false)
    private Long orderId;

    private Double amount;
    private String status;

    // Getter, Setter vs...
}