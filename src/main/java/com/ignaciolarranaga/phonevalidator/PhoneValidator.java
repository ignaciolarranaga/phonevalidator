package com.ignaciolarranaga.phonevalidator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private String defaultRegion;

    public void initialize(Phone constraintAnnotation) {
        this.defaultRegion = constraintAnnotation.defaultRegion();
    }

    public boolean isValid(String phone, ConstraintValidatorContext constraintContext) {
        if (phone == null) {
            return true;
        }

        try {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            phoneNumberUtil.parse(phone, defaultRegion);
        } catch (NumberParseException ex) {
            log.debug("There was an error parsing the number", ex);
            return false;
        }

        return true;
    }
}
