package com.zzp.seckilldemo.common.validator;

import com.zzp.seckilldemo.common.utils.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author zzp
 * @Date 2023/3/24 16:26
 * @Description: 手机号格式校验器
 * @Version 1.0
 */
public class IsPhoneValidator implements ConstraintValidator<IsPhone, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(s)) {
            return false;
        } else
            return ValidatorUtil.isPhone(s);
    }
}
