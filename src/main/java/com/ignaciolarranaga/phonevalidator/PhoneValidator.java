package com.ignaciolarranaga.phonevalidator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

@Slf4j
public class PhoneValidator implements ConstraintValidator<Phone, Object> {

    private String defaultRegion;

    public void initialize(Phone constraintAnnotation) {
        this.defaultRegion = constraintAnnotation.defaultRegion();
    }

    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        if (object == null) {
            return true;
        }

        if (object instanceof Collection) {
            boolean valid = true;
            for (Object rawPhone : (Collection) object) {
                valid &= isValidElement(rawPhone, constraintValidatorContext);
            }
            return valid;
        } else {
            return isValidElement(object, constraintValidatorContext);
        }
    }

    private boolean isValidElement(Object rawPhone, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid;
        if (rawPhone == null) {
            isValid = false;
        } else if (rawPhone instanceof String) {
            try {
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                phoneNumberUtil.parse((String) rawPhone, defaultRegion);
                isValid = true;
            } catch (NumberParseException ex) {
                log.debug("There was an error parsing the number", ex);
                isValid = false;
            }
        } else {
            isValid = false;
        }

        if (!isValid) {
            HibernateConstraintValidatorContext hibernateConstraintValidatorContext =
                    constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);
            val v = hibernateConstraintValidatorContext.addExpressionVariable("phone", rawPhone);
            hibernateConstraintValidatorContext.buildConstraintViolationWithTemplate(
                    hibernateConstraintValidatorContext.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation();
        }

        return isValid;
    }
}
