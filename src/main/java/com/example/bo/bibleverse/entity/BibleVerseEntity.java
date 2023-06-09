package com.example.bo.bibleverse.entity;


import com.example.bo.bibleverse.dto.BibleVerseDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bible_korHRV")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(BibleVersePK.class)
public class BibleVerseEntity {
    @Id
    private int book;
    @Id
    private int chapter;
    @Id
    private int verse;
    private String bookName;
    @Column(columnDefinition = "TEXT")
    private String content;

    public BibleVerseDto toDto() {
        return BibleVerseDto.builder()
                .book(this.getBook())
                .bookName(this.getBookName())
                .chapter(this.getChapter())
                .verse(this.getVerse())
                .content(this.getContent())
                .build();
    }
}
