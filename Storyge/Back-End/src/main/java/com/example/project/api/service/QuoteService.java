package com.example.project.api.service;

import com.example.project.dto.common.QuoteDto;
import com.example.project.db.entity.Quote;

public interface QuoteService {

    //명언 한 개 반환
    QuoteDto selectOneQuote();

    void setQuoteId(Long quoteId);

    default QuoteDto toDto(Quote quote) {
        return QuoteDto.builder()
                .quoteContent(quote.getQuoteContent())
                .quoteSource((quote.getQuoteSource()))
                .build();
    }


}
