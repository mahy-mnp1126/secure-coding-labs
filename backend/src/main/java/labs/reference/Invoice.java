package labs.reference;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    private BigDecimal amount;
    private String description;

    protected Invoice() {
    }

    public Invoice(Long id, String ownerId, BigDecimal amount, String description) {
        this.id = id;
        this.ownerId = ownerId;
        this.amount = amount;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
}
