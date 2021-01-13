package com.anotherdeveloper.raidtimer.Utils;

/**
 * This class formats a time in seconds into days, hours, minutes, and seconds.
 * No more pesky huge integers like 5473973 seconds. With this utility,
 * separating into conventional time is easy. All you have to do is call
 * TimeUtil.format(integer) in another class.
 *
 * If you decide to use it, it would be really nice if you could accredit me.
 *
 * @author Anand
 * @version 1.0
 */
public class TimeUtil {

    // The input is an integer in seconds.
    public static String formatTime(int secs) {
        int remainder = secs % 86400;

        int days 	= secs / 86400;
        int hours 	= remainder / 3600;
        int minutes	= (remainder / 60) - (hours * 60);
        int seconds	= (remainder % 3600) - (minutes * 60);

        String fDays 	= (days > 0 	? " " + days + " day" 		+ (days > 1 ? "s" : "") 	: "");
        String fHours 	= (hours > 0 	? " " + hours + " hour" 	+ (hours > 1 ? "s" : "") 	: "");
        String fMinutes = (minutes > 0 	? " " + minutes + " minute"	+ (minutes > 1 ? "s" : "") 	: "");
        String fSeconds = (seconds > 0 	? " " + seconds + " second"	+ (seconds > 1 ? "s" : "") 	: "");

        return new StringBuilder().append(fDays).append(fHours)
                .append(fMinutes).append(fSeconds).toString();
    }
}