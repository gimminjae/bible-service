package com.example.bo.bibleverse.enums;

import lombok.Getter;

@Getter
public enum BibleInfoEnum {
    Genesis(1, "창세기"),
    Exodus(2, "출애굽기"),
    Leviticus(3, "레위기"),
    Numbers(4, "민수기"),
    Deuteronomy(5, "신명기"),
    Joshua(6, "여호수아"),
    Judges(7, "사사기"),
    Ruth(8, "룻기"),
    Samuel1(9, "사무엘상"),
    Samuel2(10, "사무엘하"),
    Kings1(11, "열왕기상"),
    Kings2(12, "열왕기하"),
    Chronicles1(13, "역대상"),
    Chronicles2(14, "역대하"),
    Ezra(15, "에스라"),
    Nehemiah(16, "느헤미야"),
    Esther(17, "에스더"),
    Job(18, "욥기"),
    Psalms(19, "시편"),
    Proverbs(20, "잠언"),
    Ecclesiastes(21, "전도서"),
    SongOfSongs(22, "아가"),
    Isaiah(23, "이사야"),
    Jeremiah(24, "예레미야"),
    Lamentations(25, "예레미야애가"),
    Ezekiel(26, "에스겔"),
    Daniel(27, "다니엘"),
    Hosea(28, "호세아"),
    Joel(29, "요엘"),
    Amos(30, "아모스"),
    Obadiah(31, "오바댜"),
    Jonah(32, "요나"),
    Micah(33, "미가"),
    Nahum(34, "나훔"),
    Habakkuk(35, "하박국"),
    Zephaniah(36, "스바냐"),
    Haggai(37, "학개"),
    Zechariah(38, "스가랴"),
    Malachi(39, "말라기"),
    Matthew(40, "마태복음"),
    Mark(41, "마가복음"),
    Luke(42, "누가복음"),
    John(43, "요한복음"),
    Acts(44, "사도행전"),
    Romans(45, "로마서"),
    Corinthians1(46, "고린도전서"),
    Corinthians2(47, "고린도후서"),
    Galatians(48, "갈라디아서"),
    Ephesians(49, "에베소서"),
    Philippians(50, "빌립보서"),
    Colossians(51, "골로새서"),
    Thessalonians1(52, "데살로니가전서"),
    Thessalonians2(53, "데살로니가후서"),
    Timothy1(54, "디모데전서"),
    Timothy2(55, "디모데후서"),
    Titus(56, "디도서"),
    Philemon(57, "빌레몬서"),
    Hebrews(58, "히브리서"),
    James(59, "야고보서"),
    Peter1(60, "베드로전서"),
    Peter2(61, "베드로후서"),
    John1(62, "요한1서"),
    John2(63, "요한2서"),
    John3(64, "요한3서"),
    Jude(65, "유다서"),
    Revelation(66, "요한계시록");
    BibleInfoEnum(int bookSeq, String bookName) {
        this.bookSeq = bookSeq;
        this.bookName = bookName;
    }
    private final String bookName;
    private final int bookSeq;
}
