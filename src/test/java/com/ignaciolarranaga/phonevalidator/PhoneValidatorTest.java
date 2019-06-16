package com.ignaciolarranaga.phonevalidator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PhoneValidatorTest {

    private static final String SAMPLE_MESSAGE_TEMPLATE = "com.ignaciolarranaga.phonevalidator.Phone.message";

    private PhoneValidator instance;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;
    @Mock
    private HibernateConstraintValidatorContext hibernateConstraintValidatorContext;
    @Mock
    private ConstraintViolationBuilder constraintViolationBuilder;

    @Before
    public void beforeEach() {
        this.instance = new PhoneValidator();

        when(constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class))
                .thenReturn(hibernateConstraintValidatorContext);
        when(hibernateConstraintValidatorContext.getDefaultConstraintMessageTemplate())
                .thenReturn(SAMPLE_MESSAGE_TEMPLATE);
        when(hibernateConstraintValidatorContext.addExpressionVariable(any(String.class), any()))
                .thenReturn(hibernateConstraintValidatorContext);
        when(hibernateConstraintValidatorContext
                .buildConstraintViolationWithTemplate(SAMPLE_MESSAGE_TEMPLATE))
                .thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addConstraintViolation()).thenReturn(hibernateConstraintValidatorContext);
    }

    @Test
    public void nullShouldPass() {
        checkResult(null, true);
    }

    @Test
    public void invalidNumberShouldFail() {
        checkResult("305", false);
    }

    @Test
    public void invalidNumberBecauseOfCountryCodeShouldFail() {
        checkResult("305 509-6995",false);
    }

    @Test
    public void countryCodeShouldBeApplied() {
        Phone phone = mock(Phone.class);
        when(phone.defaultRegion()).thenReturn("US");
        instance.initialize(phone);
        checkResult("305 509-6995",true);
    }

    @Test
    public void countryCodeShouldBeApplied2() {
        Phone phone = mock(Phone.class);
        when(phone.defaultRegion()).thenReturn("UY");
        instance.initialize(phone);
        checkResult("099 123 123",true);
    }

    @Test
    public void validNumberSholdPass() {
        checkResult("+598 99 123 123", true);
    }

    private void checkResult(String phone, boolean isValid) {
        assertThat(instance.isValid(phone, constraintValidatorContext), is(isValid));

//        if (!isValid) {
//            verify(constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class));
//            verify(hibernateConstraintValidatorContext.addExpressionVariable("phone", phone));
//            verify(hibernateConstraintValidatorContext.buildConstraintViolationWithTemplate(SAMPLE_MESSAGE_TEMPLATE));
//            verify(constraintViolationBuilder.addConstraintViolation());
//        }
    }

}
