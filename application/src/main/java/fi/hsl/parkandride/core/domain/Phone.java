package fi.hsl.parkandride.core.domain;

import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
import static com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class Phone {

    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    private final String number;

    public Phone(String number) {
        if (phoneUtil.isPossibleNumber(number, "FI")) {
            try {
                PhoneNumber proto = phoneUtil.parse(number, "FI");
                if (!phoneUtil.isValidNumber(proto)) {
                    illegalNumber();
                }
                if (proto.getCountryCode() == 358) {
                    this.number = phoneUtil.format(proto, NATIONAL);
                } else {
                    this.number = phoneUtil.format(proto, INTERNATIONAL);
                }
            } catch (NumberParseException e) {
                throw illegalNumber(e);
            }
        } else {
            throw illegalNumber();
        }
    }

    private static IllegalArgumentException illegalNumber() {
        throw illegalNumber(null);
    }

    private static IllegalArgumentException illegalNumber(NumberParseException e) {
        throw new IllegalArgumentException("Invalid phone number", e);
    }

    public String toString() {
        return number;
    }
}
