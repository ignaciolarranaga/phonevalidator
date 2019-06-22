package com.ignaciolarranaga.phonevalidator;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
        checkResult(null, true, Collections.<String>emptyList());
    }

    @Test
    public void invalidNumberShouldFail() {
        checkResult("305", false, singletonList("305"));
    }

    @Test
    public void invalidNumberBecauseOfCountryCodeShouldFail() {
        checkResult("305 509-6995",false, singletonList("305 509-6995"));
    }

    @Test
    public void collectionOfInvalidNumberShouldFail() {
        checkResult(Arrays.asList("305", "305 509-6995"), false, Arrays.asList("305", "305 509-6995"));
    }

    @Test
    public void countryCodeShouldBeApplied() {
        Phone phone = mock(Phone.class);
        when(phone.defaultRegion()).thenReturn("US");
        instance.initialize(phone);
        checkResult("305 509-6995",true, Collections.<String>emptyList());
    }

    @Test
    public void countryCodeShouldBeAppliedInCollections() {
        Phone phone = mock(Phone.class);
        when(phone.defaultRegion()).thenReturn("US");
        instance.initialize(phone);
        checkResult(singletonList("305 509-6995"), true, Collections.<String>emptyList());
    }

    @Test
    public void countryCodeShouldBeApplied2() {
        Phone phone = mock(Phone.class);
        when(phone.defaultRegion()).thenReturn("UY");
        instance.initialize(phone);
        checkResult("099 123 123",true, Collections.<String>emptyList());
    }

    @Test
    public void validNumberSholdPass() {
        checkResult("+598 99 123 123", true, Collections.<String>emptyList());
    }

    @Test
    public void collectionOfValidNumberSholdPass() {
        checkResult(Arrays.asList("+1 305 509-6995", "+598 99 123 123"), true, Collections.<String>emptyList());
    }

    @Test
    public void collectionOfValidAndInvalidNumbersSholdFail() {
        checkResult(Arrays.asList("+1 305 509-6995", "99 123 123"), false, singletonList("99 123 123"));
    }

    private void checkResult(Object phone, boolean isValid, Collection<String> invalidPhones) {
        assertThat(instance.isValid(phone, constraintValidatorContext), is(isValid));

        if (!isValid) {
            verify(constraintValidatorContext, times(invalidPhones.size()))
                    .unwrap(HibernateConstraintValidatorContext.class);
            for (String invalidPhone : invalidPhones) {
                verify(hibernateConstraintValidatorContext).addExpressionVariable("phone", invalidPhone);
            }
            verify(hibernateConstraintValidatorContext, times(invalidPhones.size()))
                    .buildConstraintViolationWithTemplate(SAMPLE_MESSAGE_TEMPLATE);
            verify(constraintViolationBuilder, times(invalidPhones.size())).addConstraintViolation();
        }
    }

}
