package com.Project.project;

import android.content.Context;
import android.content.Intent;

import com.Project.project.Utilities.AlarmReceiver;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlarmReciverTest {
    Context context = mock(Context.class);
    Intent intent = mock(Intent.class);
    AlarmReceiver alarm = new AlarmReceiver();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void onReciveTest() {
        when(intent.getIntExtra("notificationId", 0)).thenReturn(1);
        when(intent.getStringExtra("userName")).thenReturn("Itzik");
        alarm.onReceive(context, intent);
    }
}