package com.example.bankcards.entity;

import com.example.bankcards.utility.converter.CardStatusConverter;
import com.example.bankcards.utility.converter.YearMonthConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "encrypted_card_number", nullable = false, updatable = false, unique = true)
    private String encryptedCardNumber;

    @Column(name = "masked_card_number", nullable = false, updatable = false, length = 19)
    private String maskedCardNumber;

    @Column(name = "expiry_date", nullable = false)
    @Convert(converter = YearMonthConverter.class)
    @FutureOrPresent(message = "{card.expiry.date.invalid}")
    private YearMonth expiryDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Builder.Default
    @Convert(converter = CardStatusConverter.class)
    @Column(name = "card_status", nullable = false)
    private CardStatus cardStatus = CardStatus.ACTIVE;

    @Builder.Default
    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    @PrePersist
    private void validateAndUpdateStatus() {
        if (YearMonth.now().isAfter(expiryDate)) {
            this.cardStatus = CardStatus.EXPIRED;
        }

        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Balance cannot be negative");
        }
    }

    public String getFormattedExpiry() {
        return String.format("%02d/%02d",
            expiryDate.getMonthValue(),
            expiryDate.getYear() % 100
        );
    }

    public boolean isExpired() {
        return expiryDate.isBefore(YearMonth.now());
    }

    public void block() {
        if (this.cardStatus != CardStatus.EXPIRED) {
            this.cardStatus = CardStatus.BLOCKED;
        }
    }

    public void activate() {
        if (this.cardStatus != CardStatus.EXPIRED && !isExpired()) {
            this.cardStatus = CardStatus.ACTIVE;
        }
    }

}
