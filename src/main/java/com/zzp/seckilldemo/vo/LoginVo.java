package com.zzp.seckilldemo.vo;

import com.zzp.seckilldemo.common.validator.IsPhone;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author zzp
 * @Date 2023/3/22 19:14
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class LoginVo {

    @NotBlank
    @IsPhone
    private String phone;

    @NotBlank
    private String password;
}
