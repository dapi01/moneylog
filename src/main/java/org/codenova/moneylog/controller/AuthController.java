package org.codenova.moneylog.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenova.moneylog.entity.User;
import org.codenova.moneylog.entity.Verifications;
import org.codenova.moneylog.repository.UserRepository;
import org.codenova.moneylog.repository.VerificationRepository;
import org.codenova.moneylog.request.FindPasswordRequest;
import org.codenova.moneylog.request.LoginRequest;
import org.codenova.moneylog.service.KakaoApiService;
import org.codenova.moneylog.service.MailService;
import org.codenova.moneylog.service.NaverApiService;
import org.codenova.moneylog.vo.KakaoTokenResponse;
import org.codenova.moneylog.vo.NaverProfileResponse;
import org.codenova.moneylog.vo.NaverTokenResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {
    private NaverApiService naverApiService;
    private KakaoApiService kakaoApiService;
    private MailService mailService;
    private VerificationRepository verificationRepository;
    private UserRepository userRepository;



    @GetMapping("/login")
    public String loginHandle(Model model) {
        // log.info("loginHandle...executed");

        model.addAttribute("kakaoClientId", "dbf4c3fce0a6d83a32988b67843ff363");
        model.addAttribute("kakaoRedirectUri", "http://192.168.10.65:8080/auth/kakao/callback");


        model.addAttribute("naverClientId", "F1A0nSVWMrUkKyDhkGgd");
        model.addAttribute("naverRedirectUri", "http://192.168.10.65:8080/auth/naver/callback");


        return "auth/login";
    }


    @PostMapping("/login")
    public String loginPostHandle(
            @ModelAttribute LoginRequest loginRequest,
            HttpSession session,
            Model model) {
        User user =
                userRepository.findByEmail(loginRequest.getEmail());
        if(user != null && user.getPassword().equals(loginRequest.getPassword())) {
            session.setAttribute("user", user);
            return "redirect:/index";
        }else {
            return "redirect:/auth/login";
        }
    }


    @GetMapping("/signup")
    public String signupGetHandle(Model model) {

        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signupPostHandle(@ModelAttribute User user) {
        User found = userRepository.findByEmail(user.getEmail());
        if(found == null) {
            user.setProvider("LOCAL");
            user.setVerified("F");
            userRepository.save(user);
            mailService.sendWelcomMessage(user);
        }
        return "redirect:/index";
    }



    @GetMapping("/find-password")
    public String forgotPasswordHandle(Model model){


        return "auth/find-password";
    }



    @PostMapping("/find-password")
    public String findPasswordPostHandle(@ModelAttribute @Valid FindPasswordRequest req,
                                         BindingResult result, Model model){
        if(result.hasErrors()) {
            model.addAttribute("error", "이메일 형식이 아닙니다.");
            return "auth/find-password-error";
        }
        User found = userRepository.findByEmail(req.getEmail());
        if(found == null){
            model.addAttribute("error", "해당 이메일로 임시번호를 전송할 수 없습니다.");
            return "auth/find-password-error";
        }

        String temporalPassword = UUID.randomUUID().toString().substring(0,8);
        userRepository.updatePasswordByEmail(req.getEmail(), temporalPassword);
        mailService.sendTemporalPasswordMessage(req.getEmail(), temporalPassword);

        return "auth/find-password";
    }



    @GetMapping("/naver/callback")
    public String naverCallbackHandle(@RequestParam("code") String code,
                                      @RequestParam("state") String state,
                                      HttpSession session) throws JsonProcessingException {
        // log.info("code = {}, state = {}", code, state);

        NaverTokenResponse tokenResponse =
                naverApiService.exchangeToken(code, state);

        // log.info("accessToken = {}", tokenResponse.getAccessToken());


        NaverProfileResponse profileResponse
                = naverApiService.exchangeProfile(tokenResponse.getAccessToken());
        // log.info("profileResponse id = {}", profileResponse.getId());
        log.info("profileResponse nickname = {}", profileResponse.getNickname());
        log.info("profileResponse profileImage = {}", profileResponse.getProfileImage());
        // =========================================================================================

        User found = userRepository.findByProviderAndProviderId("NAVER", profileResponse.getId());
        if (found == null) {
            User user = User.builder()
                    .nickname(profileResponse.getNickname())
                    .provider("NAVER")
                    .providerId(profileResponse.getId())
                    .verified("T")
                    .picture(profileResponse.getProfileImage()).build();

            userRepository.save(user);
            session.setAttribute("user", user);
        } else {
            session.setAttribute("user", found);
        }


        return "redirect:/index";
    }



    @GetMapping("/kakao/callback")
    public String kakaoCallbackHandle(@RequestParam("code") String code,
                                      HttpSession session
    ) throws JsonProcessingException {
        // log.info("code = {}", code);
        KakaoTokenResponse response = kakaoApiService.exchangeToken(code);
        log.info("response.idToken = {}", response.getIdToken());

        DecodedJWT decodedJWT = JWT.decode(response.getIdToken());
        String sub = decodedJWT.getClaim("sub").asString();
        String nickname = decodedJWT.getClaim("nickname").asString();
        String picture = decodedJWT.getClaim("picture").asString();

        User found = userRepository.findByProviderAndProviderId("KAKAO", sub);
        log.info("found = {}", found);
        if(found != null) {
            session.setAttribute("user", found);
        } else {
            User user = User.builder().provider("KAKAO")
                    .providerId(sub).nickname(nickname).picture(picture).verified("T").build();
            userRepository.save(user);
            session.setAttribute("user", user);
        }

        return "redirect:/index";
    }


@GetMapping("send-token")
public String sendTokenHandle(@SessionAttribute("user") User user, Model model){
        String token = UUID.randomUUID().toString().replace("-","");
        Verifications one = Verifications.builder().token(token).expiresAt(LocalDateTime.now()
                .plusDays(1)).userEmail(user.getEmail()).build();

        verificationRepository.create(one);
        mailService.sendVerificationMessage(user, one);

        return "auth/send-token";
}

    @GetMapping("/email-verify")
    public String emailVerifyHandle(@RequestParam("token")String token, Model model){
        Verifications found = verificationRepository.selectVerification(token);
        if(found == null){
            model.addAttribute("error", "유효하지 않은 인증토큰 입니다.");
            return "auth/email-verify-error";
        }
        if(LocalDateTime.now().isAfter(found.getExpiresAt())){
            model.addAttribute("error","유효기간이 만료된 인증토큰 입니다.");
        }
       // found.getExpiresAt(); : 토큰 유효 만료 시점
       // LocalDateTime.now();  : 인증 시점

        String userEmail =found.getUserEmail();
        userRepository.userVerified(userEmail);

        return "auth/email-verify";
    }

/*
    @GetMapping("/email-check")
    public String verificationHandle(@RequestParam("email")String email, Model model){
        User found = userRepository.findByEmail(email);
        if(found == null){
            model.addAttribute("error", "해당 이메일로 토큰을 전송할 수 없습니다.");
            return "auth/email-check";
        }
        String token = UUID.randomUUID().toString();

        Verifications verifications = new Verifications();
        verifications.setToken(token);
        verifications.setUserEmail(found.getEmail());

        verificationRepository.create(verifications);
        mailService.sendEmailVerify(found.getEmail(), verifications.getToken());
        return "redirect:/auth/login";
    }


    @GetMapping("/email-verify/{email}/{token}")
    public String emailVerifyCheck(@PathVariable("email")String email, @PathVariable("token")String token){

        User found = userRepository.findByEmail(email);
        Verifications verifications = verificationRepository.selectVerification(token);
        if(found.getEmail().equals(verifications.getUserEmail())){
            userRepository.userVerified(email);
        }
        return "auth/email-verify-check";
    }
*/


}
