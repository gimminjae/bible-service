package com.example.bo.plan.entity;

import com.example.bo.base.util.ObjectUtil;
import com.example.bo.member.entity.Member;
import com.example.bo.plan.converter.BibleGoalConverter;
import com.example.bo.plan.dto.Bible;
import com.example.bo.plan.dto.PlanDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "plan")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long planId;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private String planName;
    private float goalPercent;
    private int oldGoalCount;
    private int newGoalCount;

    @Convert(converter = BibleGoalConverter.class)
    private List<Bible> goalStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mem_id")
    private Member member;
    public static Plan from(PlanDto planDto) {
        List<Bible> bibleList = Bible.createAllList();
        return Plan.builder()
                .createDateTime(LocalDateTime.now())
                .startDate(LocalDate.parse(planDto.getStartDate().split("T")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .endDate(LocalDate.parse(planDto.getEndDate().split("T")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .planName(planDto.getPlanName())
                .oldGoalCount(planDto.getOldGoalCount())
                .newGoalCount(planDto.getNewGoalCount())
                .goalStatus(planDto.getGoalStatus())
                .member(planDto.getMember().toEntity())
                .goalStatus(bibleList)
                .build();
    }
    public PlanDto toDto() {
        return PlanDto.builder()
                .planId(this.getPlanId())
                .planName(this.getPlanName())
                .oldGoalCount(this.getOldGoalCount())
                .newGoalCount(this.getNewGoalCount())
                .goalPercent(this.getGoalPercent())
                .goalStatus(this.getGoalStatus())
                .createDateTime(this.getCreateDateTime())
                .updateDateTime(this.getUpdateDateTime())
                .startDate(this.getStartDate().toString())
                .endDate(this.getEndDate().toString())
                .build();
    }

    public void updateVerseStatus(List<Bible> goalStatus) {
        this.setGoalStatus(goalStatus);
        this.operateGoalStatus(goalStatus);
    }

    private void operateGoalStatus(List<Bible> goalStatus) {
        float sum = 0;
        for (Bible bible : goalStatus) {
            int goalCount;
            if (bible.isTestament()) goalCount = this.getOldGoalCount();
            else goalCount = this.getNewGoalCount();
            for (int readCount : bible.getVerseStatus()) {
                sum += Math.min(readCount, goalCount);
            }
        }
        int oldGoalTotalCount = 929;
        int newGoalTotalCount = 260;
        float totalVerseCount = (float) (oldGoalTotalCount * this.getOldGoalCount() + newGoalTotalCount * this.getNewGoalCount());
        this.setGoalPercent(Float.parseFloat(ObjectUtil.divide(sum, totalVerseCount)) * Float.parseFloat("100"));
        this.setUpdateDateTime(LocalDateTime.now());
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }
    public void setGoalPercent(float goalPercent) {
        this.goalPercent = goalPercent;
    }
    public void setGoalStatus(List<Bible> goalStatus) {
        this.goalStatus = goalStatus;
    }

    public void updatePlanInfo(PlanDto planDto) {
        this.setPlanName(planDto.getPlanName());
        this.setStartDate(LocalDate.parse(planDto.getStartDate().split("T")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        this.setEndDate(LocalDate.parse(planDto.getEndDate().split("T")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        this.setOldGoalCount(planDto.getOldGoalCount());
        this.setNewGoalCount(planDto.getNewGoalCount());
        this.setUpdateDateTime(planDto.getUpdateDateTime());
        this.operateGoalStatus(this.getGoalStatus());
    }
}
