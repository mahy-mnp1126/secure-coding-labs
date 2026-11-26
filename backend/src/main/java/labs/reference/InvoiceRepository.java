package labs.reference;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data derives the ownership filter from the method name
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByIdAndOwnerId(Long id, String ownerId);
}
