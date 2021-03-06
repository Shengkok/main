package seedu.address.model.task;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents a Task's date in the task manager.
 * Guarantees: immutable; is valid as declared in {@link #isValidDate(String)}
 */
public class Due {

    public static final String MESSAGE_CONSTRAINTS =
            "Date must be valid and should only be of the format DD-MM, where D and M are numbers.\n"
                    + "DD must range from 01 to 31 and MM must range from 01 to 12";
    public static final String VALIDATION_REGEX = "[\\d]{2}" + "-" + "[\\d]{2}";


    private static final int DAY_MIN = 1;
    private static final int DAY_MAX_FEB = 28;
    private static final int DAY_MAX_FEB_LEAP = 29;
    private static final int DAY_MAX_30 = 30;
    private static final int DAY_MAX_31 = 31;
    private static final int MONTH_MIN = 1;
    private static final int MONTH_MAX = 12;

    public final String value;

    /**
     * Constructs an {@code Due}.
     *
     * @param date A valid date.
     */
    public Due(String date) {
        requireNonNull(date);
        checkArgument(isValidDate(date), MESSAGE_CONSTRAINTS);
        value = date;
    }

    /**
     * Returns if a given string is a valid date.
     */
    public static boolean isValidDate(String test) {
        if (test.matches(VALIDATION_REGEX)) {
            String[] data = test.split("-");
            int day = Integer.parseInt(data[0]);
            int month = Integer.parseInt(data[1]);
            int year = Calendar.getInstance().get(Calendar.YEAR);

            if (month < MONTH_MIN || month > MONTH_MAX || day < DAY_MIN) {
                return false;
            } else if (month == 2) {
                if ((year % 400 == 0) || ((year % 4 == 0 && year % 100 != 0))) {
                    return day <= DAY_MAX_FEB_LEAP;
                } else {
                    return day <= DAY_MAX_FEB;
                }
            } else if (month == 1 || month == 3 || month == 5 || month == 7
                    || month == 8 || month == 10 || month == 12) {
                return day <= DAY_MAX_31;
            } else {
                return day <= DAY_MAX_30;
            }
        }
        return false;
    }

    public Date getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateAsString = toString() + "-" + Calendar.getInstance().get(Calendar.YEAR);
        Date date = null;
        try {
            date = dateFormat.parse(dateAsString);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return date;
    }

    /**
     * Returns the current day
     * @return
     */
    public Date getCurrentDate() {
        return new Date();
    }

    /**
     * Finds the number of days till the task is due
     * @param currentDay
     * @param taskDay
     * @return
     */
    public int daysDifference(Date currentDay, Date taskDay) {
        long duration = taskDay.getTime() - currentDay.getTime();

        if (duration / (24 * 60 * 60 * 1000) < -1) {
            return -1;
        } else if (duration < 0) {
            return 0;
        } else {
            return (int) ((duration / (24 * 60 * 60 * 1000)) + 1);
        }
    }

    /**
     * @return number of days till task is due
     */
    public int daysRemaining() {
        int duration = daysDifference(getCurrentDate(), getDate());

        return duration;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Due // instanceof handles nulls
                && value.equals(((Due) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
