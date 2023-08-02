package com.example.mutsamarket.controller;

import com.example.mutsamarket.dto.ResponseDto;
import com.example.mutsamarket.dto.user.UserChangePasswordDto;
import com.example.mutsamarket.dto.user.UserDeleteDto;
import com.example.mutsamarket.dto.user.UserRegisterDto;
import com.example.mutsamarket.dto.user.UserRequestDto;
import com.example.mutsamarket.entity.CustomUserDetails;
import com.example.mutsamarket.service.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final UserUtils userUtils;


    public UserController(UserDetailsManager manager, PasswordEncoder passwordEncoder, UserUtils userUtils) {
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
        this.userUtils = userUtils;
    }

    //회원가입
    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerUser(
            @RequestBody UserRegisterDto dto
            ){
        //비밀번호, 비밀번호 확인 비교
        if (dto.getPassword().equals(dto.getPasswordCheck())){
            log.info("password match");
            //회원가입
            manager.createUser(CustomUserDetails.builder()
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .phone(dto.getPhone())
                    .email(dto.getEmail())
                    .address(dto.getAddress())
                    .build());

            ResponseDto response = new ResponseDto();
            response.setMessage("회원가입이 완료되었습니다. ");
            return ResponseEntity.ok(response);
        }

        log.warn("password does not match..");
        ResponseDto response = new ResponseDto();
        response.setMessage("비밀번호와 비밀번호 확인이 틀립니다.");
        return ResponseEntity.badRequest().body(response);

    }


    //회원정보 수정
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateUser(
            @RequestBody UserRequestDto userRequestDto
    ){

        //비밀번호 검증
        if (!userUtils.checkPassword(userRequestDto.getPassword())) {
            log.warn("password does not match.");
            ResponseDto response = new ResponseDto();
            response.setMessage("현재 비밀번호가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        //사용자 정보 업데이트
        manager.updateUser(
                CustomUserDetails.builder()
                        .username(userRequestDto.getUsername())
                        .password(passwordEncoder.encode(userRequestDto.getPassword()))
                        .phone(userRequestDto.getPhone())
                        .email(userRequestDto.getEmail())
                        .address(userRequestDto.getAddress())
                .build());

        ResponseDto response = new ResponseDto();
        response.setMessage("회원정보가 변경되었습니다. ");
        return ResponseEntity.ok(response);

    }

    @PutMapping("/changePassword")
    public ResponseEntity<ResponseDto> changePassword(
            @RequestBody UserChangePasswordDto dto
            ){

        //비밀번호 변경
        manager.changePassword(dto.getOldPassword(), dto.getNewPassword());

        ResponseDto response = new ResponseDto();
        response.setMessage("비밀번호가 변경되었습니다. ");
        return ResponseEntity.ok(response);
    }

    //회원탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteUser(
            @RequestBody UserDeleteDto dto
            ){

        //비밀번호 검증
        if (!userUtils.checkPassword(dto.getPassword())) {
            log.warn("password does not match.");
            ResponseDto response = new ResponseDto();
            response.setMessage("현재 비밀번호가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        //회원 탈퇴
        manager.deleteUser(userUtils.getCurrentUser().getUsername());
        ResponseDto response = new ResponseDto();
        response.setMessage("회원탈퇴가 완료되었습니다. ");
        return ResponseEntity.ok(response);
    }

}
