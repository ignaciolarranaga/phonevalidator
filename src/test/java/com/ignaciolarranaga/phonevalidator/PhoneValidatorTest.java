package com.ignaciolarranaga.phonevalidator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class PhoneValidatorTest {

    private PhoneValidator instance;

    @Before
    public void beforeEach() {
        this.instance = new PhoneValidator();
    }

    @Test
    public void nullShouldPass() {
        assertThat(instance.isValid(null, null), is(true));
    }

    @Test
    public void invalidNumberShouldFail() {
        assertThat(instance.isValid("305", null), is(false));
    }

    @Test
    public void invalidNumberBecauseOfCountryCodeShouldFail() {
        assertThat(instance.isValid("305 509-6995", null), is(false));
    }

    @Test
    public void countryCodeShouldBeApplied() {
        Phone phone = mock(Phone.class);
        when(phone.defaultRegion()).thenReturn("US");
        instance.initialize(phone);
        assertThat(instance.isValid("305 509-6995", null), is(true));
    }

    @Test
    public void countryCodeShouldBeApplied2() {
        Phone phone = mock(Phone.class);
        when(phone.defaultRegion()).thenReturn("UY");
        instance.initialize(phone);
        assertThat(instance.isValid("099 123 123", null), is(true));
    }

    @Test
    public void validNumberSholdPass() {
        assertThat(instance.isValid("+598 99 123 123", null), is(true));
    }

}
