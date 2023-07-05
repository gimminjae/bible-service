package com.example.bo.member.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.bo.base.jwt.provider.JwtProvider;
import com.example.bo.member.refresh.entity.RefreshToken;
import com.example.bo.member.refresh.repository.RefreshTokenRedisRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bo.base.util.ObjectUtil;
import com.example.bo.member.dto.MemberDto;
import com.example.bo.member.entity.Member;
import com.example.bo.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final static int ACCESS_TOKEN_MAXAGE = 60 * 30;
    private final static String LOGIN_FAIL_MSG = "아이디 혹은 비밀번호를 확인하세요.";
    private final static String NO_ACCESS_AUTH_MSG = "권한이 없습니다.";
    private final static String NICKNAME_DUPLICATION_MSG = "중복된 닉네임입니다.";
    private final static String USERNAME_DUPLICATION_MSG = "중복된 아이디입니다.";
    private final static String EMAIL_DUPLICATION_MSG = "중복된 이메일입니다.";
    private final static String TWO_NEW_PASSWORD_NOT_CORRECT_MSG = "두 개의 새 비밀번호가 일치하지 않습니다.";
    private final static String INVALID_REQUEST_MSG = "유효하지 않은 요청입니다.";
    private final static String EXPIRE_RELOGIN_MSG = "기간 만료 : 재로그인해주세요.";
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public void signUp(MemberDto memberDto) {
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        Member member = Member.from(memberDto);
        try {
            memberRepository.save(member);
        } catch(DataIntegrityViolationException e) {
            throw new IllegalArgumentException(NICKNAME_DUPLICATION_MSG);
        }
    }

    @Override
    public MemberDto getByUsername(String username) {
        return ObjectUtil.isNullExceptionElseReturnObJect(memberRepository.findByUsername(username)).toDto();
    }

    @Override
    public void modifyEmail(MemberDto memberDto, String email) {
        Member member = ObjectUtil.isNullExceptionElseReturnObJect(memberRepository.findById(memberDto.getMemId()));
        member.modifyEmail(email);
        try {
            memberRepository.save(member);
        } catch(DataIntegrityViolationException e) {
            throw new IllegalArgumentException(EMAIL_DUPLICATION_MSG);
        }
    }

    @Override
    public void changePassword(MemberDto memberDto, String oldPassword, String newPassword1, String newPassword2) {
        if(!newPassword1.equals(newPassword2)) {
            throw new IllegalArgumentException(TWO_NEW_PASSWORD_NOT_CORRECT_MSG);
        }
        Member member = ObjectUtil.isNullExceptionElseReturnObJect(memberRepository.findById(memberDto.getMemId()));
        if(passwordEncoder.matches(oldPassword, memberDto.getPassword())) {
            member.changePassword(passwordEncoder.encode(newPassword1));
        } else {
            throw new AccessDeniedException(NO_ACCESS_AUTH_MSG);
        }
        memberRepository.save(member);
    }

    @Override
    public List<MemberDto> getAllMember() {
        return memberRepository.findAll().stream().map(Member::toDto).toList();
    }

    @Override
    public void deleteMember(MemberDto memberDto) {
        memberRepository.deleteById(memberDto.getMemId());
    }

    @Override
    public void modifyNickname(MemberDto memberDto, String nickname) {
        Member member = ObjectUtil.isNullExceptionElseReturnObJect(memberRepository.findById(memberDto.getMemId()));
        member.modifyNickname(nickname);
        try {
            memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(NICKNAME_DUPLICATION_MSG);
        }
    }

    @Override
    public void empowerDEPTSUBADMIN(MemberDto memberDto) {
        Member member = ObjectUtil.isNullExceptionElseReturnObJect(memberRepository.findById(memberDto.getMemId()));
        member.empowerDEPTSUBADMIN();
        memberRepository.save(member);
    }

    @Override
    public Map<String, String> login(MemberDto memberDto) {
        Member member = ObjectUtil.isNullExceptionElseReturnObJect(memberRepository.findByUsername(memberDto.getUsername()), LOGIN_FAIL_MSG);
        if(!passwordEncoder.matches(memberDto.getPassword(), member.getPassword())) {
            throw new AccessDeniedException(LOGIN_FAIL_MSG);
        }
        return generateTokens(member);
    }

    private Map<String, String> generateTokens(Member member) {
        return Map.of("accessToken", genAccessToken(member), "refreshToken", genRefreshToken(member));
    }


    @Override
    public MemberDto getByMemId(String memId) {
        return ObjectUtil.isNullExceptionElseReturnObJect(memberRepository.findById(memId)).toDto();
    }

    @Override
    public void logout(String memId) {
        deleteRefreshToken(memId);
    }

    @Override
    public String regenAccessToken(String refreshToken) {
        return regenAccessTokenWithRefreshToken(refreshToken);
    }

    private String genAccessToken(Member member) {
        return jwtProvider.generateAccessToken(member.getAccessTokenClaims(), ACCESS_TOKEN_MAXAGE);
    }

    private String genRefreshToken(Member member) {
        StringBuilder sb = new StringBuilder();
        sb.append(member.getCreateDateTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
        sb.append(member.getMemId());
        sb.append(UUID.randomUUID());

        try {
            RefreshToken refreshToken = ObjectUtil.isNullExceptionElseReturnObJect(refreshTokenRedisRepository.findById(member.getMemId()));
            refreshToken.update(LocalDateTime.now().plusMonths(1), sb.toString());
            refreshTokenRedisRepository.save(refreshToken);
        } catch (NullPointerException e) {
            return refreshTokenRedisRepository.save(RefreshToken.from(member.getMemId(), sb.toString())).getRefreshToken();
        }
        return sb.toString();
    }

    private String regenAccessTokenWithRefreshToken(String refreshTokenString) {
        RefreshToken refreshToken;
        try {
            refreshToken = refreshTokenRedisRepository.findByRefreshToken(refreshTokenString);
        } catch (NullPointerException e) {
            throw new NullPointerException(INVALID_REQUEST_MSG);
        }
        if(LocalDateTime.now().isAfter(refreshToken.getExpiredDateTime())) {
            throw new AccessDeniedException(EXPIRE_RELOGIN_MSG);
        }
        return genAccessToken(ObjectUtil.isNullExceptionElseReturnObJect(memberRepository.findById(refreshToken.getId())));
    }

    private void deleteRefreshToken(String memId) {
        RefreshToken refreshToken = ObjectUtil.isNullExceptionElseReturnObJect(refreshTokenRedisRepository.findById(memId));
        refreshTokenRedisRepository.delete(refreshToken);
    }

}
