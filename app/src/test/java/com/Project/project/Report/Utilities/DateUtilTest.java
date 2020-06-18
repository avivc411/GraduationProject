package com.Project.project.Report.Utilities;

import android.content.Context;

import com.Project.project.GPS.GPS;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DateUtilTest {
    Context context = mock(Context.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testConvertDateToString() {
        Date date = (new GregorianCalendar(1998, 6, 12)).getTime();
        String x = DateUtils.convertDateToString(date);
        Assert.assertEquals("07-12-1998", x);
    }

    @Test
    public void testConvertStringToDate() {
        String date = "07-12-1998";
        Date d = DateUtils.convertStringToDate(date);
        Date dateExpected = (new GregorianCalendar(1998, 6, 12)).getTime();
        Assert.assertEquals(dateExpected, d);
    }
}