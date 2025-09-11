package com.example.bankcards.utility.converter;

import com.example.bankcards.entity.CardStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CardStatusConverter implements AttributeConverter<CardStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(CardStatus cardStatus) {
        return cardStatus.getCode();
    }

    @Override
    public CardStatus convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }

        return CardStatus.fromCode(code);
    }

}
